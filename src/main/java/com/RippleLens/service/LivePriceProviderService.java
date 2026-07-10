package com.RippleLens.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RippleLens.alert.AlertResponse;
import com.RippleLens.live.LivePriceTickResponse;
import com.RippleLens.model.Asset;
import com.RippleLens.model.AssetPricePoint;
import com.RippleLens.repository.AssetPricePointRepository;
import com.RippleLens.repository.AssetRepository;

@Service
public class LivePriceProviderService {

    private static final String STORED_API_SOURCE = "Latest stored/API snapshot";

    private final MarketDataIngestionService marketDataIngestionService;
    private final AssetRepository assetRepository;
    private final AssetPricePointRepository pricePointRepository;
    private final TrackingService trackingService;
    private final Duration refreshCadence;
    private final AtomicReference<Instant> lastProviderRefresh = new AtomicReference<>(Instant.EPOCH);
    private final Object refreshLock = new Object();

    public LivePriceProviderService(
            MarketDataIngestionService marketDataIngestionService,
            AssetRepository assetRepository,
            AssetPricePointRepository pricePointRepository,
            TrackingService trackingService,
            @Value("${ripplelens.live.real-refresh-ms:60000}") long realRefreshMs) {
        this.marketDataIngestionService = marketDataIngestionService;
        this.assetRepository = assetRepository;
        this.pricePointRepository = pricePointRepository;
        this.trackingService = trackingService;
        this.refreshCadence = Duration.ofMillis(Math.max(15_000L, realRefreshMs));
    }

    @Transactional(readOnly = true)
    public List<LivePriceTickResponse> getCurrentSnapshot() {
        return buildSnapshot(false);
    }

    public List<LivePriceTickResponse> getRealPriceBatch() {
        refreshProvidersIfDue();
        return buildSnapshot(true);
    }

    private void refreshProvidersIfDue() {
        Instant now = Instant.now();
        Instant latestKnownRefresh = latestKnownRefresh();
        if (Duration.between(latestKnownRefresh, now).compareTo(refreshCadence) < 0) {
            return;
        }

        synchronized (refreshLock) {
            now = Instant.now();
            latestKnownRefresh = latestKnownRefresh();
            if (Duration.between(latestKnownRefresh, now).compareTo(refreshCadence) < 0) {
                return;
            }
            try {
                lastProviderRefresh.set(marketDataIngestionService.refreshLiveData().updatedAt());
            } catch (RuntimeException error) {
                lastProviderRefresh.set(now);
            }
        }
    }

    private Instant latestKnownRefresh() {
        Instant ingestionRefresh = marketDataIngestionService.getLastRefreshAt();
        Instant providerRefresh = lastProviderRefresh.get();
        return ingestionRefresh.isAfter(providerRefresh) ? ingestionRefresh : providerRefresh;
    }

    @Transactional
    protected List<LivePriceTickResponse> buildSnapshot(boolean evaluateAlerts) {
        return assetRepository.findAll()
                .stream()
                .map(asset -> toTick(asset, evaluateAlerts))
                .toList();
    }

    private LivePriceTickResponse toTick(Asset asset, boolean evaluateAlerts) {
        BigDecimal price = safePrice(asset.getCurrentPrice());
        BigDecimal previousPrice = latestPreviousPrice(asset).orElse(price);
        BigDecimal priceChange = price.subtract(previousPrice);
        List<AlertResponse> triggeredAlerts = evaluateAlerts
                ? trackingService.evaluateAlertsForAsset(asset.getSymbol())
                : List.of();

        return new LivePriceTickResponse(
                asset.getSymbol(),
                asset.getName(),
                asset.getCategory().name(),
                price,
                previousPrice,
                safePrice(asset.getDailyChangePercentage()),
                priceChange,
                directionFor(priceChange),
                asset.getQuoteCurrency(),
                asset.getUnit(),
                latestSourceFor(asset),
                Instant.now(),
                triggeredAlerts);
    }

    private Optional<BigDecimal> latestPreviousPrice(Asset asset) {
        return pricePointRepository
                .findTopByAssetAndObservationDateBeforeOrderByObservationDateDesc(asset, java.time.LocalDate.now())
                .map(AssetPricePoint::getPrice)
                .map(this::safePrice);
    }

    private String latestSourceFor(Asset asset) {
        return pricePointRepository
                .findTopByAssetOrderByObservationDateDesc(asset)
                .map(AssetPricePoint::getSource)
                .filter(source -> source != null && !source.isBlank())
                .orElse(STORED_API_SOURCE);
    }

    private BigDecimal safePrice(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String directionFor(BigDecimal priceChange) {
        int comparison = priceChange.compareTo(BigDecimal.ZERO);
        if (comparison > 0) {
            return "UP";
        }
        if (comparison < 0) {
            return "DOWN";
        }
        return "FLAT";
    }
}
