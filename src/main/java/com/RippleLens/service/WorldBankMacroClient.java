package com.RippleLens.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.RippleLens.intelligence.MacroIndicatorResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WorldBankMacroClient {

    private static final String WORLD_BANK_URL = "https://api.worldbank.org/v2/country/%s/indicator/%s?format=json&per_page=6";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public WorldBankMacroClient() {
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .build();
    }

    public List<MacroIndicatorResponse> getCoreIndicators() {
        List<MacroIndicatorResponse> indicators = new ArrayList<>();
        indicators.add(fetchIndicator(
                "US",
                "FP.CPI.TOTL.ZG",
                "US inflation",
                "%",
                "High inflation can support hard-asset narratives but can also invite tighter monetary policy.",
                fallback("FP.CPI.TOTL.ZG", "US inflation", "United States", "2.95", "2024", "%")));
        indicators.add(fetchIndicator(
                "US",
                "NY.GDP.MKTP.KD.ZG",
                "US GDP growth",
                "%",
                "US growth helps explain dollar strength, risk appetite, and commodity demand expectations.",
                fallback("NY.GDP.MKTP.KD.ZG", "US GDP growth", "United States", "2.16", "2025", "%")));
        indicators.add(fetchIndicator(
                "CN",
                "NY.GDP.MKTP.KD.ZG",
                "China GDP growth",
                "%",
                "China growth is important for industrial metals, global trade, and commodity-sensitive currencies.",
                fallback("NY.GDP.MKTP.KD.ZG", "China GDP growth", "China", "4.96", "2025", "%")));
        indicators.add(fetchIndicator(
                "EU",
                "FP.CPI.TOTL.ZG",
                "EU inflation",
                "%",
                "European inflation can affect ECB expectations, the euro, and local-currency commodity costs.",
                fallback("FP.CPI.TOTL.ZG", "EU inflation", "European Union", "2.47", "2025", "%")));
        indicators.add(fetchIndicator(
                "WLD",
                "NY.GDP.MKTP.KD.ZG",
                "World GDP growth",
                "%",
                "Global growth is a broad demand signal for industrial commodities and risk assets.",
                fallback("NY.GDP.MKTP.KD.ZG", "World GDP growth", "World", "2.92", "2025", "%")));
        return indicators;
    }

    private MacroIndicatorResponse fetchIndicator(
            String countryCode,
            String indicatorCode,
            String name,
            String unit,
            String interpretation,
            MacroIndicatorResponse fallback) {
        try {
            String url = WORLD_BANK_URL.formatted(
                    URLEncoder.encode(countryCode, StandardCharsets.UTF_8),
                    URLEncoder.encode(indicatorCode, StandardCharsets.UTF_8));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return fallback;
            }

            JsonNode root = objectMapper.readTree(response.body());
            if (!root.isArray() || root.size() < 2 || !root.get(1).isArray()) {
                return fallback;
            }

            for (JsonNode row : root.get(1)) {
                if (!row.path("value").isNull() && !row.path("value").isMissingNode()) {
                    return new MacroIndicatorResponse(
                            indicatorCode,
                            name,
                            row.path("country").path("value").asText(fallback.country()),
                            row.path("value").decimalValue(),
                            row.path("date").asText(fallback.year()),
                            unit,
                            "World Bank",
                            interpretation);
                }
            }
            return fallback;
        } catch (IOException | InterruptedException | RuntimeException error) {
            if (error instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return fallback;
        }
    }

    private MacroIndicatorResponse fallback(
            String code,
            String name,
            String country,
            String value,
            String year,
            String unit) {
        return new MacroIndicatorResponse(
                code,
                name,
                country,
                new BigDecimal(value),
                year,
                unit,
                "Fallback sample",
                "Fallback value used when the public macro API is unavailable.");
    }
}
