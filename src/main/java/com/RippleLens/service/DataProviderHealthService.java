package com.RippleLens.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.stereotype.Service;

import com.RippleLens.live.DataHealthSummaryResponse;
import com.RippleLens.live.ProviderHealthResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DataProviderHealthService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final MarketDataIngestionService marketDataIngestionService;
    private final MetalsDevClient metalsDevClient;
    private final MarketNewsService marketNewsService;

    public DataProviderHealthService(
            MarketDataIngestionService marketDataIngestionService,
            MetalsDevClient metalsDevClient,
            MarketNewsService marketNewsService) {
        this.marketDataIngestionService = marketDataIngestionService;
        this.metalsDevClient = metalsDevClient;
        this.marketNewsService = marketNewsService;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public DataHealthSummaryResponse currentHealth() {
        Instant checkedAt = Instant.now();
        List<ProviderHealthResponse> providers = List.of(
                checkJson(
                        "Gold API",
                        "Metals live prices",
                        "https://api.gold-api.com/price/XAU",
                        json -> json.hasNonNull("price"),
                        "Checks live gold price endpoint."),
                metalsDevClient.healthCheck(),
                checkJson(
                        "Binance Spot",
                        "Crypto live/history",
                        "https://api.binance.com/api/v3/ticker/24hr?symbol=BTCUSDT",
                        json -> json.hasNonNull("lastPrice"),
                        "Checks BTCUSDT market endpoint."),
                checkJson(
                        "CoinGecko",
                        "Crypto fallback",
                        "https://api.coingecko.com/api/v3/ping",
                        json -> json.hasNonNull("gecko_says"),
                        "Checks fallback crypto API availability."),
                checkJson(
                        "Frankfurter",
                        "FX live/history",
                        "https://api.frankfurter.dev/v2/rates?base=USD&quotes=EUR,AED",
                        JsonNode::isArray,
                        "Checks FX rates including AED."),
                checkJson(
                        "World Bank",
                        "Macro context",
                        "https://api.worldbank.org/v2/country/US/indicator/FP.CPI.TOTL.ZG?format=json&per_page=1",
                        JsonNode::isArray,
                        "Checks macro indicator availability."),
                marketNewsService.healthCheck());

        int available = (int) providers.stream()
                .filter(provider -> "AVAILABLE".equals(provider.status()))
                .count();
        String overallStatus;
        if (available == providers.size()) {
            overallStatus = "AVAILABLE";
        } else if (available >= Math.max(1, providers.size() / 2)) {
            overallStatus = "DEGRADED";
        } else {
            overallStatus = "UNAVAILABLE";
        }

        return new DataHealthSummaryResponse(
                checkedAt,
                overallStatus,
                available,
                providers.size(),
                marketDataIngestionService.getLastRefreshAt(),
                providers);
    }

    private ProviderHealthResponse checkJson(
            String provider,
            String category,
            String url,
            Predicate<JsonNode> validator,
            String successDetail) {
        Instant checkedAt = Instant.now();
        long started = System.nanoTime();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(7))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long latencyMs = elapsedMs(started);
            if (response.statusCode() == 429) {
                return new ProviderHealthResponse(provider, category, "DEGRADED", latencyMs, checkedAt,
                        "Rate limited by provider; stored fallback data may be used.");
            }
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return new ProviderHealthResponse(provider, category, "UNAVAILABLE", latencyMs, checkedAt,
                        "HTTP " + response.statusCode() + " from provider.");
            }

            JsonNode json = objectMapper.readTree(response.body());
            if (!validator.test(json)) {
                return new ProviderHealthResponse(provider, category, "DEGRADED", latencyMs, checkedAt,
                        "Provider responded, but the payload shape was unexpected.");
            }
            return new ProviderHealthResponse(provider, category, "AVAILABLE", latencyMs, checkedAt, successDetail);
        } catch (IOException | InterruptedException | RuntimeException error) {
            if (error instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return new ProviderHealthResponse(provider, category, "UNAVAILABLE", elapsedMs(started), checkedAt,
                    "Connection failed: " + error.getClass().getSimpleName());
        }
    }

    private long elapsedMs(long started) {
        return Math.max(1, Duration.ofNanos(System.nanoTime() - started).toMillis());
    }
}
