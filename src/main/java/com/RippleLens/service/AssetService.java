package com.RippleLens.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.springframework.stereotype.Service;

import com.RippleLens.model.Asset;
import com.RippleLens.model.AssetCategory;
import com.RippleLens.model.AssetPricePoint;
import com.RippleLens.price.PriceSeriesPointResponse;
import com.RippleLens.repository.AssetPricePointRepository;
import com.RippleLens.repository.AssetRepository;
import com.RippleLens.trust.AssetTrustReportResponse;

@Service
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetPricePointRepository assetPricePointRepository;

    public AssetService(AssetRepository assetRepository, AssetPricePointRepository assetPricePointRepository) {
        this.assetRepository = assetRepository;
        this.assetPricePointRepository = assetPricePointRepository;
    }

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public List<Asset> getAssetsByCategory(AssetCategory category) {
        return assetRepository.findByCategoryOrderByNameAsc(category);
    }

    public Asset getAssetBySymbol(String symbol) {
        return assetRepository.findBySymbolIgnoreCase(symbol)
                .orElseThrow(() -> new IllegalArgumentException("Unknown asset symbol: " + symbol));
    }

    public List<AssetPricePoint> getPriceHistory(String symbol) {
        Asset asset = getAssetBySymbol(symbol);
        return assetPricePointRepository.findByAssetOrderByObservationDateAsc(asset);
    }

    public AssetTrustReportResponse getTrustReport(String symbol) {
        Asset asset = getAssetBySymbol(symbol);
        List<AssetPricePoint> history = getPriceHistory(symbol);
        LinkedHashSet<String> sources = new LinkedHashSet<>();
        history.stream()
                .map(AssetPricePoint::getSource)
                .filter(source -> source != null && !source.isBlank())
                .forEach(sources::add);

        AssetPricePoint first = history.isEmpty() ? null : history.get(0);
        AssetPricePoint latest = history.isEmpty() ? null : history.get(history.size() - 1);
        int realApiObservations = (int) history.stream()
                .map(AssetPricePoint::getSource)
                .filter(this::isRealApiSource)
                .count();
        int fallbackObservations = (int) history.stream()
                .map(AssetPricePoint::getSource)
                .filter(this::isFallbackSource)
                .count();
        int modeledObservations = (int) history.stream()
                .map(AssetPricePoint::getSource)
                .filter(this::isModeledSource)
                .count();
        boolean hasLiveOrHistorical = realApiObservations > 0;
        boolean usesRealApi = latest != null && isRealApiSource(latest.getSource());
        long dataAgeDays = latest == null ? -1 : ChronoUnit.DAYS.between(latest.getObservationDate(), LocalDate.now());
        String dataQuality = dataQualityFor(asset, realApiObservations, fallbackObservations, hasLiveOrHistorical, dataAgeDays);
        int dataTrustScore = dataTrustScoreFor(realApiObservations, fallbackObservations, usesRealApi,
                latest == null ? null : latest.getObservationDate());
        String confidenceLevel = confidenceLevelFor(dataTrustScore);
        String sourceMode = sourceModeFor(usesRealApi, realApiObservations, fallbackObservations, modeledObservations);
        String freshnessStatus = freshnessStatusFor(dataAgeDays);
        String coverageSummary = coverageSummaryFor(first, latest, history.size(), realApiObservations, fallbackObservations);

        List<String> caveats = new ArrayList<>();
        if (fallbackObservations > 0 && realApiObservations > 0) {
            caveats.add("This asset combines real API observations with older stored fallback history.");
        } else if (fallbackObservations > 0) {
            caveats.add("This asset is currently using stored fallback history until more provider data is collected.");
        }
        if (asset.getCategory() == AssetCategory.CRYPTOCURRENCY) {
            caveats.add("Crypto historical data uses Binance Spot first, then CoinGecko when available; free APIs may be rate-limited.");
        }
        if (asset.getCategory() == AssetCategory.METAL) {
            if (realApiObservations > 1) {
                caveats.add("Metal history is API-backed when Metals.Dev is configured; live spot-style quotes still refresh from Gold API.");
            } else {
                caveats.add("Metals use real live Gold API quotes; historical metal charts need a Metals.Dev API key before fallback history is replaced.");
            }
        }
        if (asset.getCategory() != AssetCategory.CRYPTOCURRENCY) {
            caveats.add("Hourly points are modeled estimates, not exchange-grade intraday history.");
        }
        if (dataAgeDays > 1) {
            caveats.add("Latest stored observation is " + dataAgeDays + " days old; refresh live data before relying on the chart.");
        }
        caveats.add("RippleLens explains plausible relationships; it does not provide guaranteed forecasts.");

        return new AssetTrustReportResponse(
                asset.getSymbol(),
                asset.getName(),
                dataQuality,
                dataTrustScore,
                confidenceLevel,
                sourceMode,
                usesRealApi,
                history.size(),
                first == null ? null : first.getObservationDate(),
                latest == null ? null : latest.getObservationDate(),
                null,
                latest == null ? "No stored price observations" : latest.getSource(),
                new ArrayList<>(sources),
                realApiObservations,
                fallbackObservations,
                modeledObservations,
                dataAgeDays,
                freshnessStatus,
                coverageSummary,
                caveats,
                recommendationFor(dataQuality));
    }

    public List<PriceSeriesPointResponse> getHourlyPriceHistory(String symbol) {
        Asset asset = getAssetBySymbol(symbol);
        BigDecimal latestPrice = asset.getCurrentPrice();
        BigDecimal dailyChange = asset.getDailyChangePercentage() == null
                ? BigDecimal.ZERO
                : asset.getDailyChangePercentage();
        BigDecimal startPrice = estimateStartPrice(latestPrice, dailyChange);
        LocalDateTime firstHour = LocalDateTime.now().minusHours(23).withMinute(0).withSecond(0).withNano(0);
        List<PriceSeriesPointResponse> points = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            double progress = hour / 23.0;
            BigDecimal base = startPrice.add(latestPrice.subtract(startPrice).multiply(BigDecimal.valueOf(progress)));
            BigDecimal noise = base.multiply(BigDecimal.valueOf(intradayWave(asset, hour)));
            BigDecimal price = base.add(noise).setScale(scaleFor(asset), RoundingMode.HALF_UP);
            if (hour == 23) {
                price = latestPrice.setScale(scaleFor(asset), RoundingMode.HALF_UP);
            }

            LocalDateTime timestamp = firstHour.plusHours(hour);
            points.add(new PriceSeriesPointResponse(
                    timestamp.getHour() + ":00",
                    timestamp,
                    price,
                    "Modeled intraday estimate from latest stored/live price",
                    "HOURLY"));
        }

        return points;
    }

    private BigDecimal estimateStartPrice(BigDecimal latestPrice, BigDecimal dailyChange) {
        BigDecimal divisor = BigDecimal.ONE.add(dailyChange.divide(BigDecimal.valueOf(100), MathContext.DECIMAL64));
        if (BigDecimal.ZERO.compareTo(divisor) == 0) {
            return latestPrice;
        }
        return latestPrice.divide(divisor, MathContext.DECIMAL64);
    }

    private double intradayWave(Asset asset, int hour) {
        double volatility = switch (asset.getCategory()) {
            case CRYPTOCURRENCY -> 0.006;
            case METAL -> 0.0025;
            case CURRENCY -> 0.0008;
        };
        return Math.sin(hour * 0.9) * volatility;
    }

    private int scaleFor(Asset asset) {
        if (asset.getCurrentPrice().compareTo(BigDecimal.ONE) < 0) {
            return 6;
        }
        if (asset.getCategory() == AssetCategory.CURRENCY) {
            return 4;
        }
        return 2;
    }

    private String dataQualityFor(
            Asset asset,
            int realApiObservations,
            int fallbackObservations,
            boolean hasLiveOrHistorical,
            long dataAgeDays) {
        if (realApiObservations >= 25 && hasLiveOrHistorical && dataAgeDays <= 1) {
            return asset.getCategory() == AssetCategory.CRYPTOCURRENCY ? "strong" : "medium";
        }
        if (realApiObservations >= 10 || (hasLiveOrHistorical && fallbackObservations > 0)) {
            return "medium";
        }
        if (hasLiveOrHistorical) {
            return "mixed";
        }
        return "limited";
    }

    private boolean isRealApiSource(String source) {
        if (source == null || source.isBlank()) {
            return false;
        }
        String normalized = source.toLowerCase();
        return !isFallbackSource(source) && !isModeledSource(source) && !normalized.contains("stored fallback");
    }

    private boolean isFallbackSource(String source) {
        if (source == null || source.isBlank()) {
            return false;
        }
        String normalized = source.toLowerCase();
        return "demo".equalsIgnoreCase(source)
                || normalized.contains("fallback")
                || normalized.contains("stored seed");
    }

    private boolean isModeledSource(String source) {
        return source != null && source.toLowerCase().contains("modeled");
    }

    private int dataTrustScoreFor(
            int realApiObservations,
            int fallbackObservations,
            boolean usesRealApi,
            java.time.LocalDate latestObservationDate) {
        int score = Math.min(35, realApiObservations * 2);
        if (usesRealApi) {
            score += 35;
        }
        if (latestObservationDate != null) {
            long ageDays = ChronoUnit.DAYS.between(latestObservationDate, LocalDate.now());
            if (ageDays <= 1) {
                score += 20;
            } else if (ageDays <= 7) {
                score += 10;
            }
        }
        score -= Math.min(15, fallbackObservations / 2);
        return Math.max(0, Math.min(100, score));
    }

    private String confidenceLevelFor(int dataTrustScore) {
        if (dataTrustScore >= 80) {
            return "high";
        }
        if (dataTrustScore >= 55) {
            return "medium";
        }
        return "low";
    }

    private String recommendationFor(String dataQuality) {
        return switch (dataQuality) {
            case "strong" -> "Good for educational trend analysis, with source and caveat checks.";
            case "medium" -> "Useful for classroom analysis, but compare with external market data before relying on it.";
            case "mixed" -> "Useful for orientation, but distinguish live API values from fallback history.";
            default -> "Treat as exploratory only until more live or historical observations are collected.";
        };
    }

    private String sourceModeFor(
            boolean usesRealApi,
            int realApiObservations,
            int fallbackObservations,
            int modeledObservations) {
        if (usesRealApi && fallbackObservations > 0) {
            return "Real latest + mixed history";
        }
        if (usesRealApi) {
            return "Real API";
        }
        if (modeledObservations > 0 && realApiObservations == 0) {
            return "Modeled estimate";
        }
        return "Fallback/stored";
    }

    private String freshnessStatusFor(long dataAgeDays) {
        if (dataAgeDays < 0) {
            return "No stored observations";
        }
        if (dataAgeDays <= 1) {
            return "Fresh";
        }
        if (dataAgeDays <= 7) {
            return "Recent";
        }
        return "Stale";
    }

    private String coverageSummaryFor(
            AssetPricePoint first,
            AssetPricePoint latest,
            int totalObservations,
            int realApiObservations,
            int fallbackObservations) {
        if (first == null || latest == null) {
            return "No price history stored yet.";
        }
        long coveredDays = ChronoUnit.DAYS.between(first.getObservationDate(), latest.getObservationDate()) + 1;
        return totalObservations + " observations across " + coveredDays + " days: "
                + realApiObservations + " API-backed, "
                + fallbackObservations + " fallback.";
    }
}
