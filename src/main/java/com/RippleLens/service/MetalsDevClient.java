package com.RippleLens.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.RippleLens.live.MetalHistoricalPricePoint;
import com.RippleLens.live.ProviderHealthResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MetalsDevClient {

    private static final String TIMESERIES_URL = "https://api.metals.dev/v1/timeseries";
    private static final Map<String, String> API_METAL_TO_SYMBOL = Map.of(
            "gold", "XAU",
            "silver", "XAG",
            "copper", "XCU",
            "platinum", "XPT",
            "palladium", "XPD");

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public MetalsDevClient(@Value("${ripplelens.metals-dev.api-key:${METALS_DEV_API_KEY:}}") String apiKey) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .build();
    }

    public List<MetalHistoricalPricePoint> getHistoricalDailyPrices(int days) {
        if (!isConfigured()) {
            return List.of();
        }

        int requestedDays = Math.min(30, Math.max(7, days));
        LocalDate end = LocalDate.now().minusDays(1);
        LocalDate start = end.minusDays(requestedDays - 1L);
        String url = TIMESERIES_URL
                + "?api_key=" + encode(apiKey)
                + "&start_date=" + start
                + "&end_date=" + end
                + "&currency=USD"
                + "&unit=toz";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(14))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return List.of();
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode rates = root.path("rates");
            if (!rates.isObject()) {
                return List.of();
            }

            List<MetalHistoricalPricePoint> points = new ArrayList<>();
            for (Map.Entry<String, JsonNode> dateEntry : orderedProperties(rates).entrySet()) {
                LocalDate date = LocalDate.parse(dateEntry.getKey());
                JsonNode metals = dateEntry.getValue().path("metals");
                if (!metals.isObject()) {
                    metals = dateEntry.getValue();
                }

                for (Map.Entry<String, String> mapping : API_METAL_TO_SYMBOL.entrySet()) {
                    BigDecimal price = usdPriceFor(metals, mapping.getKey());
                    if (price != null) {
                        points.add(new MetalHistoricalPricePoint(
                                mapping.getValue(),
                                date,
                                price,
                                "Metals.Dev historical daily"));
                    }
                }
            }
            return points;
        } catch (IOException | InterruptedException | RuntimeException error) {
            if (error instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return List.of();
        }
    }

    public ProviderHealthResponse healthCheck() {
        Instant checkedAt = Instant.now();
        if (!isConfigured()) {
            return new ProviderHealthResponse(
                    "Metals.Dev",
                    "Metals historical prices",
                    "NOT_CONFIGURED",
                    0,
                    checkedAt,
                    "Add METALS_DEV_API_KEY or ripplelens.metals-dev.api-key to enable real historical metals.");
        }

        long started = System.nanoTime();
        LocalDate today = LocalDate.now();
        String url = TIMESERIES_URL
                + "?api_key=" + encode(apiKey)
                + "&start_date=" + today.minusDays(3)
                + "&end_date=" + today
                + "&currency=USD"
                + "&unit=toz";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(8))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long latencyMs = elapsedMs(started);
            if (response.statusCode() == 401 || response.statusCode() == 403) {
                return new ProviderHealthResponse("Metals.Dev", "Metals historical prices", "UNAVAILABLE", latencyMs,
                        checkedAt, "API key was rejected by Metals.Dev.");
            }
            if (response.statusCode() == 429) {
                return new ProviderHealthResponse("Metals.Dev", "Metals historical prices", "DEGRADED", latencyMs,
                        checkedAt, "Provider rate limit reached; stored metal history kept.");
            }
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return new ProviderHealthResponse("Metals.Dev", "Metals historical prices", "UNAVAILABLE", latencyMs,
                        checkedAt, "HTTP " + response.statusCode() + " from Metals.Dev.");
            }

            JsonNode root = objectMapper.readTree(response.body());
            if (!root.path("rates").isObject()) {
                return new ProviderHealthResponse("Metals.Dev", "Metals historical prices", "DEGRADED", latencyMs,
                        checkedAt, "Provider responded, but no rates object was found.");
            }
            return new ProviderHealthResponse("Metals.Dev", "Metals historical prices", "AVAILABLE", latencyMs,
                    checkedAt, "Real metals history endpoint is reachable.");
        } catch (IOException | InterruptedException | RuntimeException error) {
            if (error instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return new ProviderHealthResponse("Metals.Dev", "Metals historical prices", "UNAVAILABLE", elapsedMs(started),
                    checkedAt, "Connection to Metals.Dev failed.");
        }
    }

    public boolean isConfigured() {
        return !apiKey.isBlank();
    }

    private BigDecimal usdPriceFor(JsonNode metals, String metalKey) {
        JsonNode direct = metals.path(metalKey);
        if (direct.isNumber()) {
            return direct.decimalValue();
        }

        JsonNode nestedPrice = direct.path("price");
        if (nestedPrice.isNumber()) {
            return nestedPrice.decimalValue();
        }

        JsonNode rate = direct.path("rate");
        if (rate.isNumber()) {
            BigDecimal unitsPerUsd = rate.decimalValue();
            if (BigDecimal.ZERO.compareTo(unitsPerUsd) != 0) {
                return BigDecimal.ONE.divide(unitsPerUsd, MathContext.DECIMAL64);
            }
        }

        JsonNode rootRate = metals.path("rates").path(metalKey);
        if (rootRate.isNumber()) {
            BigDecimal unitsPerUsd = rootRate.decimalValue();
            if (BigDecimal.ZERO.compareTo(unitsPerUsd) != 0) {
                return BigDecimal.ONE.divide(unitsPerUsd, MathContext.DECIMAL64);
            }
        }

        return null;
    }

    private Map<String, JsonNode> orderedProperties(JsonNode node) {
        Map<String, JsonNode> values = new LinkedHashMap<>();
        node.properties().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> values.put(entry.getKey(), entry.getValue()));
        return values;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private long elapsedMs(long started) {
        return Math.max(1, Duration.ofNanos(System.nanoTime() - started).toMillis());
    }
}
