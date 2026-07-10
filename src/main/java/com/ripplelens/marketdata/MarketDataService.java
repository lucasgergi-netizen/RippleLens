package com.ripplelens.marketdata;

import com.fasterxml.jackson.databind.JsonNode;
import com.ripplelens.asset.Asset;
import com.ripplelens.asset.AssetType;
import com.ripplelens.price.PricePoint;
import com.ripplelens.price.PricePointRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class MarketDataService {

    private static final Map<String, String> COINGECKO_IDS = Map.of(
            "BTC", "bitcoin",
            "ETH", "ethereum",
            "BNB", "binancecoin",
            "SOL", "solana",
            "XRP", "ripple",
            "USDC", "usd-coin",
            "DOGE", "dogecoin",
            "ADA", "cardano",
            "TRX", "tron",
            "AVAX", "avalanche-2"
    );

    private static final Map<String, String> BINANCE_USDT_SYMBOLS = Map.of(
            "BTC", "BTCUSDT",
            "ETH", "ETHUSDT",
            "BNB", "BNBUSDT",
            "SOL", "SOLUSDT",
            "XRP", "XRPUSDT",
            "USDC", "USDCUSDT",
            "DOGE", "DOGEUSDT",
            "ADA", "ADAUSDT",
            "TRX", "TRXUSDT",
            "AVAX", "AVAXUSDT"
    );

    private final PricePointRepository pricePointRepository;
    private final RestClient restClient;

    public MarketDataService(PricePointRepository pricePointRepository, RestClient.Builder restClientBuilder) {
        this.pricePointRepository = pricePointRepository;
        this.restClient = restClientBuilder.build();
    }

    public MarketPriceSeries pricesFor(Asset asset) {
        try {
            MarketPriceSeries liveSeries = switch (asset.getType()) {
                case CRYPTO -> cryptoPrices(asset);
                case FIAT_CURRENCY -> currencyPrices(asset);
                case METAL -> metalPrices(asset);
            };
            if (liveSeries != null && !liveSeries.prices().isEmpty()) {
                return liveSeries;
            }
        } catch (RuntimeException ignored) {
            // The app stays usable for demos/classes even when public APIs are down or rate-limited.
        }

        return storedPrices(asset);
    }

    private MarketPriceSeries cryptoPrices(Asset asset) {
        try {
            MarketPriceSeries binanceSeries = binanceCryptoPrices(asset);
            if (binanceSeries != null && !binanceSeries.prices().isEmpty()) {
                return binanceSeries;
            }
        } catch (RuntimeException ignored) {
            // Fall through to CoinGecko when Binance is unavailable for a region, symbol, or rate limit.
        }

        return coinGeckoCryptoPrices(asset);
    }

    private MarketPriceSeries binanceCryptoPrices(Asset asset) {
        String binanceSymbol = BINANCE_USDT_SYMBOLS.get(asset.getSymbol().toUpperCase(Locale.ROOT));
        if (binanceSymbol == null) {
            return null;
        }

        JsonNode response = restClient.get()
                .uri("https://api.binance.com/api/v3/klines?symbol={symbol}&interval=1d&limit=30", binanceSymbol)
                .retrieve()
                .body(JsonNode.class);

        if (response == null || !response.isArray()) {
            return null;
        }

        List<MarketPricePoint> points = new ArrayList<>();
        for (JsonNode candle : response) {
            if (!candle.isArray() || candle.size() < 5) {
                continue;
            }
            LocalDate date = Instant.ofEpochMilli(candle.get(0).asLong()).atZone(ZoneOffset.UTC).toLocalDate();
            BigDecimal closePrice = new BigDecimal(candle.get(4).asText());
            points.add(new MarketPricePoint(date, closePrice));
        }

        return new MarketPriceSeries(points, "Binance Spot API (" + binanceSymbol + ")", true);
    }

    private MarketPriceSeries coinGeckoCryptoPrices(Asset asset) {
        String coinId = COINGECKO_IDS.get(asset.getSymbol().toUpperCase(Locale.ROOT));
        if (coinId == null) {
            return null;
        }

        JsonNode response = restClient.get()
                .uri("https://api.coingecko.com/api/v3/coins/{id}/market_chart?vs_currency=usd&days=30&interval=daily", coinId)
                .retrieve()
                .body(JsonNode.class);

        if (response == null || !response.has("prices")) {
            return null;
        }

        Map<LocalDate, MarketPricePoint> pointsByDate = new LinkedHashMap<>();
        for (JsonNode point : response.get("prices")) {
            if (!point.isArray() || point.size() < 2) {
                continue;
            }
            LocalDate date = Instant.ofEpochMilli(point.get(0).asLong()).atZone(ZoneOffset.UTC).toLocalDate();
            BigDecimal price = BigDecimal.valueOf(point.get(1).asDouble());
            pointsByDate.put(date, new MarketPricePoint(date, price));
        }

        return new MarketPriceSeries(pointsByDate.values().stream().toList(), "CoinGecko API", true);
    }

    private MarketPriceSeries currencyPrices(Asset asset) {
        String symbol = asset.getSymbol().toUpperCase(Locale.ROOT);
        if ("USD".equals(symbol)) {
            return usdSeries();
        }

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LocalDate from = today.minusDays(30);

        JsonNode response = restClient.get()
                .uri("https://api.frankfurter.dev/v2/rates?from={from}&to={to}&base={base}&quotes=USD", from, today, symbol)
                .retrieve()
                .body(JsonNode.class);

        if (response == null || !response.isArray()) {
            return null;
        }

        List<MarketPricePoint> points = new ArrayList<>();
        for (JsonNode row : response) {
            if (!row.hasNonNull("date") || !row.hasNonNull("rate")) {
                continue;
            }
            points.add(new MarketPricePoint(LocalDate.parse(row.get("date").asText()), BigDecimal.valueOf(row.get("rate").asDouble())));
        }
        points.sort(Comparator.comparing(MarketPricePoint::observationDate));

        return new MarketPriceSeries(points, "Frankfurter API", true);
    }

    private MarketPriceSeries usdSeries() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        List<MarketPricePoint> points = new ArrayList<>();
        for (int i = 30; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            points.add(new MarketPricePoint(date, BigDecimal.ONE));
        }
        return new MarketPriceSeries(points, "Reference USD rate", true);
    }

    private MarketPriceSeries metalPrices(Asset asset) {
        JsonNode response = restClient.get()
                .uri("https://api.gold-api.com/price/{symbol}", asset.getSymbol().toUpperCase(Locale.ROOT))
                .retrieve()
                .body(JsonNode.class);

        if (response == null || !response.hasNonNull("price")) {
            return null;
        }

        List<MarketPricePoint> stored = storedPrices(asset).prices();
        if (stored.isEmpty()) {
            return new MarketPriceSeries(
                    List.of(new MarketPricePoint(LocalDate.now(ZoneOffset.UTC), BigDecimal.valueOf(response.get("price").asDouble()))),
                    "Gold API latest",
                    true
            );
        }

        List<MarketPricePoint> merged = new ArrayList<>(stored);
        merged.set(merged.size() - 1, new MarketPricePoint(LocalDate.now(ZoneOffset.UTC), BigDecimal.valueOf(response.get("price").asDouble())));

        return new MarketPriceSeries(merged, "Gold API latest + stored history", true);
    }

    private MarketPriceSeries storedPrices(Asset asset) {
        List<MarketPricePoint> points = pricePointRepository.findByAssetOrderByObservationDateAsc(asset).stream()
                .map(this::toMarketPoint)
                .toList();
        return new MarketPriceSeries(points, "Demo/stored data", false);
    }

    private MarketPricePoint toMarketPoint(PricePoint pricePoint) {
        return new MarketPricePoint(pricePoint.getObservationDate(), pricePoint.getPriceUsd());
    }
}
