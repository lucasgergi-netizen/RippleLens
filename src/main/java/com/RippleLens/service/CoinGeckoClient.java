package com.RippleLens.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.RippleLens.live.CryptoHistoricalPricePoint;
import com.RippleLens.live.CryptoPriceResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CoinGeckoClient {

    private static final String SIMPLE_PRICE_URL = "https://api.coingecko.com/api/v3/simple/price"
            + "?ids=bitcoin,ethereum,tether,binancecoin,usd-coin,ripple,solana,tron,figure-heloc,hyperliquid"
            + "&vs_currencies=usd"
            + "&include_24hr_change=true"
            + "&include_last_updated_at=true";

    private static final Map<String, String> SYMBOL_TO_ID = Map.ofEntries(
            Map.entry("BTC", "bitcoin"),
            Map.entry("ETH", "ethereum"),
            Map.entry("USDT", "tether"),
            Map.entry("BNB", "binancecoin"),
            Map.entry("USDC", "usd-coin"),
            Map.entry("XRP", "ripple"),
            Map.entry("SOL", "solana"),
            Map.entry("TRX", "tron"),
            Map.entry("FIGR_HELOC", "figure-heloc"),
            Map.entry("HYPE", "hyperliquid"));

    private static final Map<String, String> BINANCE_USDT_SYMBOLS = Map.ofEntries(
            Map.entry("BTC", "BTCUSDT"),
            Map.entry("ETH", "ETHUSDT"),
            Map.entry("BNB", "BNBUSDT"),
            Map.entry("USDC", "USDCUSDT"),
            Map.entry("XRP", "XRPUSDT"),
            Map.entry("SOL", "SOLUSDT"),
            Map.entry("TRX", "TRXUSDT"));

    private static final Map<String, String> SYMBOL_NAMES = Map.ofEntries(
            Map.entry("BTC", "Bitcoin"),
            Map.entry("ETH", "Ethereum"),
            Map.entry("USDT", "Tether"),
            Map.entry("BNB", "BNB"),
            Map.entry("USDC", "USDC"),
            Map.entry("XRP", "XRP"),
            Map.entry("SOL", "Solana"),
            Map.entry("TRX", "TRON"),
            Map.entry("FIGR_HELOC", "Figure Heloc"),
            Map.entry("HYPE", "Hyperliquid"));

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public CoinGeckoClient() {
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .build();
    }

    public List<CryptoPriceResponse> getLiveCryptoPrices() {
        List<CryptoPriceResponse> results = new ArrayList<>();
        Set<String> symbolsWithBinanceData = new HashSet<>();

        for (Map.Entry<String, String> entry : BINANCE_USDT_SYMBOLS.entrySet()) {
            getBinanceLivePrice(entry.getKey(), entry.getValue()).ifPresent(price -> {
                results.add(price);
                symbolsWithBinanceData.add(price.symbol());
            });
        }

        for (CryptoPriceResponse price : getCoinGeckoLiveCryptoPrices()) {
            if (!symbolsWithBinanceData.contains(price.symbol())) {
                results.add(price);
            }
        }

        return results;
    }

    public List<CryptoHistoricalPricePoint> getHistoricalDailyPrices(String symbol, int days) {
        List<CryptoHistoricalPricePoint> binancePrices = getBinanceHistoricalDailyPrices(symbol, days);
        if (!binancePrices.isEmpty()) {
            return binancePrices;
        }
        return getCoinGeckoHistoricalDailyPrices(symbol, days);
    }

    private java.util.Optional<CryptoPriceResponse> getBinanceLivePrice(String symbol, String binanceSymbol) {
        String url = "https://api.binance.com/api/v3/ticker/24hr?symbol=" + binanceSymbol;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Binance returned HTTP " + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());
            BigDecimal price = new BigDecimal(root.path("lastPrice").asText("0"));
            BigDecimal change = new BigDecimal(root.path("priceChangePercent").asText("0"));
            long closeTimeMillis = root.path("closeTime").asLong(0);
            Instant lastUpdatedAt = closeTimeMillis > 0 ? Instant.ofEpochMilli(closeTimeMillis) : Instant.now();

            return java.util.Optional.of(new CryptoPriceResponse(
                    symbol,
                    SYMBOL_NAMES.getOrDefault(symbol, symbol),
                    price,
                    change,
                    lastUpdatedAt,
                    "Binance Spot"));
        } catch (IOException | InterruptedException | RuntimeException error) {
            if (error instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return java.util.Optional.empty();
        }
    }

    private List<CryptoHistoricalPricePoint> getBinanceHistoricalDailyPrices(String symbol, int days) {
        String binanceSymbol = BINANCE_USDT_SYMBOLS.get(symbol.toUpperCase());
        if (binanceSymbol == null) {
            return List.of();
        }

        String url = "https://api.binance.com/api/v3/klines?symbol=" + binanceSymbol
                + "&interval=1d&limit=" + days;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(12))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Binance historical endpoint returned HTTP " + response.statusCode());
            }

            JsonNode candles = objectMapper.readTree(response.body());
            if (!candles.isArray()) {
                return List.of();
            }

            List<CryptoHistoricalPricePoint> results = new ArrayList<>();
            for (JsonNode candle : candles) {
                if (!candle.isArray() || candle.size() < 5) {
                    continue;
                }
                long epochMillis = candle.get(0).asLong();
                LocalDate date = Instant.ofEpochMilli(epochMillis).atZone(ZoneOffset.UTC).toLocalDate();
                BigDecimal closePrice = new BigDecimal(candle.get(4).asText("0"));
                results.add(new CryptoHistoricalPricePoint(symbol.toUpperCase(), date, closePrice, "Binance Spot daily"));
            }
            return results;
        } catch (IOException | InterruptedException | RuntimeException error) {
            if (error instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return List.of();
        }
    }

    private List<CryptoPriceResponse> getCoinGeckoLiveCryptoPrices() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SIMPLE_PRICE_URL))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("CoinGecko returned HTTP " + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());
            List<CryptoPriceResponse> results = new ArrayList<>();
            addResponse(results, root, "bitcoin", "BTC", "Bitcoin");
            addResponse(results, root, "ethereum", "ETH", "Ethereum");
            addResponse(results, root, "tether", "USDT", "Tether");
            addResponse(results, root, "binancecoin", "BNB", "BNB");
            addResponse(results, root, "usd-coin", "USDC", "USDC");
            addResponse(results, root, "ripple", "XRP", "XRP");
            addResponse(results, root, "solana", "SOL", "Solana");
            addResponse(results, root, "tron", "TRX", "TRON");
            addResponse(results, root, "figure-heloc", "FIGR_HELOC", "Figure Heloc");
            addResponse(results, root, "hyperliquid", "HYPE", "Hyperliquid");
            return results;
        } catch (IOException | InterruptedException | RuntimeException error) {
            if (error instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return List.of();
        }
    }

    private List<CryptoHistoricalPricePoint> getCoinGeckoHistoricalDailyPrices(String symbol, int days) {
        String apiId = SYMBOL_TO_ID.get(symbol.toUpperCase());
        if (apiId == null) {
            return List.of();
        }

        String url = "https://api.coingecko.com/api/v3/coins/" + apiId
                + "/market_chart?vs_currency=usd&days=" + days + "&interval=daily";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(12))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("CoinGecko historical endpoint returned HTTP " + response.statusCode());
            }

            JsonNode prices = objectMapper.readTree(response.body()).path("prices");
            if (!prices.isArray()) {
                return List.of();
            }

            List<CryptoHistoricalPricePoint> results = new ArrayList<>();
            for (JsonNode point : prices) {
                if (!point.isArray() || point.size() < 2) {
                    continue;
                }
                long epochMillis = point.get(0).asLong();
                LocalDate date = Instant.ofEpochMilli(epochMillis).atZone(ZoneOffset.UTC).toLocalDate();
                BigDecimal price = point.get(1).decimalValue();
                results.add(new CryptoHistoricalPricePoint(symbol.toUpperCase(), date, price, "CoinGecko historical daily"));
            }
            return results;
        } catch (IOException | InterruptedException | RuntimeException error) {
            if (error instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return List.of();
        }
    }

    private void addResponse(List<CryptoPriceResponse> results, JsonNode root, String apiId, String symbol, String name) {
        JsonNode node = root.path(apiId);
        if (node.isMissingNode() || node.path("usd").isMissingNode()) {
            return;
        }
        results.add(toResponse(root, apiId, symbol, name));
    }

    private CryptoPriceResponse toResponse(JsonNode root, String apiId, String symbol, String name) {
        JsonNode node = root.path(apiId);
        BigDecimal price = node.path("usd").decimalValue();
        BigDecimal change = node.path("usd_24h_change").decimalValue();
        long updatedAt = node.path("last_updated_at").asLong(0);
        Instant lastUpdatedAt = updatedAt > 0 ? Instant.ofEpochSecond(updatedAt) : Instant.EPOCH;

        return new CryptoPriceResponse(symbol, name, price, change, lastUpdatedAt, "CoinGecko");
    }
}
