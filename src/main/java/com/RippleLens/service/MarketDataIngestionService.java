package com.RippleLens.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RippleLens.live.CryptoHistoricalPricePoint;
import com.RippleLens.live.CryptoPriceResponse;
import com.RippleLens.live.CurrencyHistoricalRatePoint;
import com.RippleLens.live.CurrencyRateResponse;
import com.RippleLens.live.IngestionResult;
import com.RippleLens.live.MetalHistoricalPricePoint;
import com.RippleLens.live.MetalPriceResponse;
import com.RippleLens.model.Asset;
import com.RippleLens.model.AssetPricePoint;
import com.RippleLens.repository.AssetPricePointRepository;
import com.RippleLens.repository.AssetRepository;

@Service
public class MarketDataIngestionService {

    private final CoinGeckoClient coinGeckoClient;
    private final FrankfurterClient frankfurterClient;
    private final GoldApiMetalsClient goldApiMetalsClient;
    private final MetalsDevClient metalsDevClient;
    private final AssetRepository assetRepository;
    private final AssetPricePointRepository pricePointRepository;
    private final ReentrantLock refreshLock = new ReentrantLock();
    private volatile Instant lastRefreshAt = Instant.EPOCH;

    public MarketDataIngestionService(
            CoinGeckoClient coinGeckoClient,
            FrankfurterClient frankfurterClient,
            GoldApiMetalsClient goldApiMetalsClient,
            MetalsDevClient metalsDevClient,
            AssetRepository assetRepository,
            AssetPricePointRepository pricePointRepository) {
        this.coinGeckoClient = coinGeckoClient;
        this.frankfurterClient = frankfurterClient;
        this.goldApiMetalsClient = goldApiMetalsClient;
        this.metalsDevClient = metalsDevClient;
        this.assetRepository = assetRepository;
        this.pricePointRepository = pricePointRepository;
    }

    @Scheduled(fixedDelay = 6 * 60 * 60 * 1000L, initialDelay = 90 * 1000L)
    @Transactional
    public void scheduledRefresh() {
        refreshLiveData();
    }

    @Transactional
    public IngestionResult refreshLiveData() {
        refreshLock.lock();
        try {
            List<String> messages = new ArrayList<>();
            int metalCount = updateMetalAssets(messages);
            int metalHistoricalPoints = updateMetalHistory(messages);
            int cryptoCount = updateCryptoAssets(messages);
            int currencyHistoricalPoints = updateCurrencyHistory(messages);
            int currencyCount = updateCurrencyAssets(messages);

            if (metalCount == 0) {
                messages.add("Metals API unavailable; existing demo/latest values kept.");
            }
            if (metalHistoricalPoints == 0) {
                if (metalsDevClient.isConfigured()) {
                    messages.add("Metals.Dev historical API unavailable or limited; existing stored metal history kept.");
                } else {
                    messages.add("Metals.Dev API key not configured; existing stored metal history kept.");
                }
            }
            if (cryptoCount == 0) {
                messages.add("Crypto API unavailable; existing demo/latest values kept.");
            }
            if (currencyCount == 0) {
                messages.add("Currency API unavailable; existing demo/latest values kept.");
            }
            if (currencyHistoricalPoints == 0) {
                messages.add("FX historical API unavailable or limited; existing stored history kept.");
            }

            lastRefreshAt = Instant.now();
            return new IngestionResult(lastRefreshAt, metalCount, cryptoCount, currencyCount, messages);
        } finally {
            refreshLock.unlock();
        }
    }

    public Instant getLastRefreshAt() {
        return lastRefreshAt;
    }

    private int updateMetalAssets(List<String> messages) {
        int updated = 0;
        for (MetalPriceResponse quote : goldApiMetalsClient.getLiveMetalPrices()) {
            Asset asset = assetRepository.findBySymbolIgnoreCase(quote.symbol()).orElse(null);
            if (asset == null) {
                continue;
            }

            BigDecimal change = calculateDailyChange(asset, quote.priceUsd());
            asset.setCurrentPrice(quote.priceUsd());
            asset.setDailyChangePercentage(change);
            assetRepository.save(asset);
            saveOrUpdateToday(asset, quote.priceUsd(), quote.source());
            updated++;
        }
        if (updated > 0) {
            messages.add("Updated " + updated + " metal assets from Gold API.");
        }
        return updated;
    }

    private int updateMetalHistory(List<String> messages) {
        int updated = 0;
        LocalDate today = LocalDate.now();
        for (MetalHistoricalPricePoint point : metalsDevClient.getHistoricalDailyPrices(35)) {
            Asset asset = assetRepository.findBySymbolIgnoreCase(point.symbol()).orElse(null);
            if (asset == null || !point.observationDate().isBefore(today)) {
                continue;
            }

            AssetPricePoint pricePoint = pricePointRepository
                    .findByAssetAndObservationDate(asset, point.observationDate())
                    .orElseGet(() -> new AssetPricePoint(asset, point.observationDate(), point.priceUsd(), point.source()));
            pricePoint.setPrice(point.priceUsd());
            pricePoint.setSource(point.source());
            pricePointRepository.save(pricePoint);
            updated++;
        }
        if (updated > 0) {
            messages.add("Updated " + updated + " metal historical daily observations from Metals.Dev.");
        }
        return updated;
    }

    private int updateCryptoAssets(List<String> messages) {
        int updated = 0;
        for (CryptoPriceResponse quote : coinGeckoClient.getLiveCryptoPrices()) {
            Asset asset = assetRepository.findBySymbolIgnoreCase(quote.symbol()).orElse(null);
            if (asset == null) {
                continue;
            }

            asset.setCurrentPrice(quote.priceUsd());
            asset.setDailyChangePercentage(quote.change24hPercentage());
            assetRepository.save(asset);
            saveOrUpdateToday(asset, quote.priceUsd(), quote.source());
            updated++;
        }
        if (updated > 0) {
            messages.add("Updated " + updated + " cryptocurrency assets from Binance Spot/CoinGecko.");
        }

        int historicalPoints = updateCryptoHistory(messages);
        if (historicalPoints > 0) {
            messages.add("Updated " + historicalPoints + " crypto historical daily observations from Binance Spot/CoinGecko.");
        }
        return updated;
    }

    private int updateCryptoHistory(List<String> messages) {
        int updated = 0;
        LocalDate today = LocalDate.now();
        for (Asset asset : assetRepository.findAll()) {
            if (asset.getCategory() != com.RippleLens.model.AssetCategory.CRYPTOCURRENCY) {
                continue;
            }
            for (CryptoHistoricalPricePoint point : coinGeckoClient.getHistoricalDailyPrices(asset.getSymbol(), 30)) {
                if (!point.observationDate().isBefore(today)) {
                    continue;
                }
                AssetPricePoint pricePoint = pricePointRepository
                        .findByAssetAndObservationDate(asset, point.observationDate())
                        .orElseGet(() -> new AssetPricePoint(asset, point.observationDate(), point.priceUsd(), point.source()));
                pricePoint.setPrice(point.priceUsd());
                pricePoint.setSource(point.source());
                pricePointRepository.save(pricePoint);
                updated++;
            }
        }
        if (updated == 0) {
            messages.add("Crypto historical API unavailable or rate-limited; stored/demo history kept.");
        }
        return updated;
    }

    private int updateCurrencyHistory(List<String> messages) {
        int updated = 0;
        for (CurrencyHistoricalRatePoint point : frankfurterClient.getHistoricalDailyRates(35)) {
            Asset asset = assetRepository.findBySymbolIgnoreCase(point.symbol()).orElse(null);
            if (asset == null) {
                continue;
            }

            AssetPricePoint pricePoint = pricePointRepository
                    .findByAssetAndObservationDate(asset, point.observationDate())
                    .orElseGet(() -> new AssetPricePoint(asset, point.observationDate(), point.usdPrice(), point.source()));
            pricePoint.setPrice(point.usdPrice());
            pricePoint.setSource(point.source());
            pricePointRepository.save(pricePoint);
            updated++;
        }
        if (updated > 0) {
            messages.add("Updated " + updated + " FX historical daily observations from Frankfurter.");
        }
        return updated;
    }

    private int updateCurrencyAssets(List<String> messages) {
        int updated = 0;
        for (CurrencyRateResponse quote : frankfurterClient.getLiveCurrencyRates()) {
            Asset asset = assetRepository.findBySymbolIgnoreCase(quote.symbol()).orElse(null);
            if (asset == null) {
                continue;
            }

            asset.setCurrentPrice(quote.usdPrice());
            asset.setDailyChangePercentage(calculateDailyChange(asset, quote.usdPrice()));
            assetRepository.save(asset);
            saveOrUpdateToday(asset, quote.usdPrice(), quote.source());
            updated++;
        }
        if (updated > 0) {
            messages.add("Updated " + updated + " currency assets from Frankfurter v2 with ExchangeRate fallback.");
        }
        return updated;
    }

    private void saveOrUpdateToday(Asset asset, java.math.BigDecimal price, String source) {
        LocalDate today = LocalDate.now();
        AssetPricePoint pricePoint = pricePointRepository.findByAssetAndObservationDate(asset, today)
                .orElseGet(() -> new AssetPricePoint(asset, today, price, source));
        pricePoint.setPrice(price);
        pricePoint.setSource(source);
        pricePointRepository.save(pricePoint);
    }

    private BigDecimal calculateDailyChange(Asset asset, BigDecimal latestPrice) {
        return pricePointRepository
                .findTopByAssetAndObservationDateBeforeOrderByObservationDateDesc(asset, LocalDate.now())
                .filter(previousPoint -> !"Demo".equalsIgnoreCase(previousPoint.getSource()))
                .map(previousPoint -> percentageChange(previousPoint.getPrice(), latestPrice))
                .orElse(asset.getDailyChangePercentage());
    }

    private BigDecimal percentageChange(BigDecimal previousPrice, BigDecimal latestPrice) {
        if (previousPrice == null || BigDecimal.ZERO.compareTo(previousPrice) == 0) {
            return BigDecimal.ZERO;
        }
        return latestPrice.subtract(previousPrice)
                .divide(previousPrice, MathContext.DECIMAL64)
                .multiply(BigDecimal.valueOf(100));
    }
}
