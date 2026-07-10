package com.RippleLens.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.RippleLens.live.CurrencyHistoricalRatePoint;
import com.RippleLens.live.CurrencyRateResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FrankfurterClient {

    private static final String LATEST_USD_V2_URL = "https://api.frankfurter.dev/v2/rates"
            + "?base=USD&quotes=EUR,JPY,GBP,CNY,CAD,AUD,CHF,HKD,SGD,SEK,AED";
    private static final String LATEST_USD_V1_URL = "https://api.frankfurter.dev/v1/latest"
            + "?base=USD&symbols=EUR,JPY,GBP,CNY,CAD,AUD,CHF,HKD,SGD,SEK";
    private static final String HISTORY_USD_V2_URL = "https://api.frankfurter.dev/v2/rates"
            + "?from=%s&to=%s&base=USD&quotes=EUR,JPY,GBP,CNY,CAD,AUD,CHF,HKD,SGD,SEK,AED";
    private static final String AED_USD_URL = "https://api.exchangerate-api.com/v4/latest/USD";
    private static final Map<String, String> CURRENCY_NAMES = Map.ofEntries(
            Map.entry("USD", "US Dollar"),
            Map.entry("EUR", "Euro"),
            Map.entry("JPY", "Japanese Yen"),
            Map.entry("GBP", "British Pound"),
            Map.entry("CNY", "Chinese Yuan"),
            Map.entry("CAD", "Canadian Dollar"),
            Map.entry("AUD", "Australian Dollar"),
            Map.entry("CHF", "Swiss Franc"),
            Map.entry("HKD", "Hong Kong Dollar"),
            Map.entry("SGD", "Singapore Dollar"),
            Map.entry("SEK", "Swedish Krona"),
            Map.entry("AED", "UAE Dirham"));

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public FrankfurterClient() {
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .build();
    }

    public List<CurrencyRateResponse> getLiveCurrencyRates() {
        List<CurrencyRateResponse> v2Results = getLiveCurrencyRatesFromFrankfurterV2();
        if (!v2Results.isEmpty()) {
            return v2Results;
        }
        return getLiveCurrencyRatesFromFrankfurterV1();
    }

    public List<CurrencyHistoricalRatePoint> getHistoricalDailyRates(int days) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(Math.max(7, days));
        String url = HISTORY_USD_V2_URL.formatted(start, end);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(12))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return List.of();
            }

            JsonNode rows = objectMapper.readTree(response.body());
            if (!rows.isArray()) {
                return List.of();
            }

            List<CurrencyHistoricalRatePoint> results = new ArrayList<>();
            Set<LocalDate> dates = new LinkedHashSet<>();
            for (JsonNode row : rows) {
                String symbol = row.path("quote").asText();
                if (symbol.isBlank()) {
                    continue;
                }

                LocalDate date = LocalDate.parse(row.path("date").asText(LocalDate.now().toString()));
                BigDecimal unitsPerUsd = decimal(row.path("rate"));
                if (BigDecimal.ZERO.compareTo(unitsPerUsd) == 0) {
                    continue;
                }

                BigDecimal usdPerUnit = BigDecimal.ONE.divide(unitsPerUsd, MathContext.DECIMAL64);
                results.add(new CurrencyHistoricalRatePoint(
                        symbol,
                        date,
                        usdPerUnit,
                        "Frankfurter v2 historical daily"));
                dates.add(date);
            }

            for (LocalDate date : dates) {
                results.add(new CurrencyHistoricalRatePoint("USD", date, BigDecimal.ONE, "USD reference rate"));
            }
            return results;
        } catch (IOException | InterruptedException | RuntimeException error) {
            if (error instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return List.of();
        }
    }

    private List<CurrencyRateResponse> getLiveCurrencyRatesFromFrankfurterV2() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(LATEST_USD_V2_URL))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return List.of();
            }

            JsonNode root = objectMapper.readTree(response.body());
            if (!root.isArray()) {
                return List.of();
            }

            List<CurrencyRateResponse> results = new ArrayList<>();
            results.add(new CurrencyRateResponse("USD", "US Dollar", BigDecimal.ONE, BigDecimal.ZERO, LocalDate.now(), "Frankfurter"));
            for (JsonNode row : root) {
                String symbol = row.path("quote").asText();
                BigDecimal unitsPerUsd = decimal(row.path("rate"));
                if (symbol.isBlank() || BigDecimal.ZERO.compareTo(unitsPerUsd) == 0) {
                    continue;
                }
                LocalDate date = LocalDate.parse(row.path("date").asText(LocalDate.now().toString()));
                BigDecimal usdPerUnit = BigDecimal.ONE.divide(unitsPerUsd, MathContext.DECIMAL64);
                results.add(new CurrencyRateResponse(
                        symbol,
                        CURRENCY_NAMES.getOrDefault(symbol, symbol),
                        usdPerUnit,
                        BigDecimal.ZERO,
                        date,
                        symbol.equals("AED") ? "Frankfurter v2" : "Frankfurter"));
            }
            return results;
        } catch (IOException | InterruptedException | RuntimeException error) {
            if (error instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return List.of();
        }
    }

    private List<CurrencyRateResponse> getLiveCurrencyRatesFromFrankfurterV1() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(LATEST_USD_V1_URL))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Frankfurter returned HTTP " + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());
            LocalDate date = LocalDate.parse(root.path("date").asText(LocalDate.now().toString()));
            JsonNode rates = root.path("rates");

            List<CurrencyRateResponse> results = new ArrayList<>();
            results.add(new CurrencyRateResponse("USD", "US Dollar", BigDecimal.ONE, BigDecimal.ZERO, date, "Frankfurter"));
            addUsdPrice(results, rates, "EUR", date);
            addUsdPrice(results, rates, "JPY", date);
            addUsdPrice(results, rates, "GBP", date);
            addUsdPrice(results, rates, "CNY", date);
            addUsdPrice(results, rates, "CAD", date);
            addUsdPrice(results, rates, "AUD", date);
            addUsdPrice(results, rates, "CHF", date);
            addUsdPrice(results, rates, "HKD", date);
            addUsdPrice(results, rates, "SGD", date);
            addUsdPrice(results, rates, "SEK", date);
            addAedPrice(results);
            return results;
        } catch (IOException | InterruptedException | RuntimeException error) {
            if (error instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return List.of();
        }
    }

    private void addUsdPrice(List<CurrencyRateResponse> results, JsonNode rates, String symbol, LocalDate date) {
        BigDecimal unitsPerUsd = decimal(rates.path(symbol));
        if (BigDecimal.ZERO.compareTo(unitsPerUsd) == 0) {
            return;
        }

        BigDecimal usdPerUnit = BigDecimal.ONE.divide(unitsPerUsd, MathContext.DECIMAL64);
        results.add(new CurrencyRateResponse(symbol, CURRENCY_NAMES.getOrDefault(symbol, symbol), usdPerUnit, BigDecimal.ZERO, date, "Frankfurter"));
    }

    private void addAedPrice(List<CurrencyRateResponse> results) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AED_USD_URL))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return;
            }

            JsonNode root = objectMapper.readTree(response.body());
            BigDecimal aedPerUsd = decimal(root.path("rates").path("AED"));
            if (BigDecimal.ZERO.compareTo(aedPerUsd) == 0) {
                return;
            }

            LocalDate date = LocalDate.parse(root.path("date").asText(LocalDate.now().toString()));
            BigDecimal usdPerAed = BigDecimal.ONE.divide(aedPerUsd, MathContext.DECIMAL64);
            results.add(new CurrencyRateResponse("AED", "UAE Dirham", usdPerAed, BigDecimal.ZERO, date, "ExchangeRate API"));
        } catch (IOException | InterruptedException | RuntimeException error) {
            if (error instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private BigDecimal decimal(JsonNode node) {
        return new BigDecimal(node.asText("0"));
    }
}
