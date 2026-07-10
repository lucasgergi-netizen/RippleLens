package com.RippleLens.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.RippleLens.live.MetalPriceResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GoldApiMetalsClient {

    private static final String BASE_URL = "https://api.gold-api.com/price/";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GoldApiMetalsClient() {
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .build();
    }

    public List<MetalPriceResponse> getLiveMetalPrices() {
        List<MetalPriceResponse> prices = new ArrayList<>();
        addPrice(prices, "XAU", "XAU", "Gold", "troy ounce");
        addPrice(prices, "XAG", "XAG", "Silver", "troy ounce");
        addPrice(prices, "HG", "XCU", "Copper", "pound");
        addPrice(prices, "XPT", "XPT", "Platinum", "troy ounce");
        addPrice(prices, "XPD", "XPD", "Palladium", "troy ounce");
        return prices;
    }

    private void addPrice(
            List<MetalPriceResponse> prices,
            String providerSymbol,
            String appSymbol,
            String fallbackName,
            String unit) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + providerSymbol))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return;
            }

            JsonNode root = objectMapper.readTree(response.body());
            if (root.has("error") || root.path("price").isMissingNode()) {
                return;
            }

            prices.add(new MetalPriceResponse(
                    appSymbol,
                    root.path("name").asText(fallbackName),
                    root.path("price").decimalValue(),
                    unit,
                    BigDecimal.ZERO,
                    parseInstant(root.path("updatedAt").asText()),
                    providerSymbol.equals(appSymbol) ? "Gold API" : "Gold API (" + providerSymbol + ")"));
        } catch (IOException | InterruptedException | RuntimeException error) {
            if (error instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private Instant parseInstant(String value) {
        try {
            return Instant.parse(value);
        } catch (RuntimeException error) {
            return Instant.now();
        }
    }
}
