package com.RippleLens.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.RippleLens.intelligence.EventSignalResponse;
import com.RippleLens.intelligence.MovementTimelineItemResponse;
import com.RippleLens.intelligence.MovementTimelineResponse;
import com.RippleLens.intelligence.NewsItemResponse;
import com.RippleLens.model.Asset;
import com.RippleLens.model.AssetPricePoint;

@Service
public class MovementTimelineService {

    private final AssetService assetService;
    private final MarketNewsService marketNewsService;
    private final EventIntelligenceService eventIntelligenceService;

    public MovementTimelineService(
            AssetService assetService,
            MarketNewsService marketNewsService,
            EventIntelligenceService eventIntelligenceService) {
        this.assetService = assetService;
        this.marketNewsService = marketNewsService;
        this.eventIntelligenceService = eventIntelligenceService;
    }

    public MovementTimelineResponse buildTimeline(String symbol, int days) {
        int windowDays = Math.max(1, Math.min(365, days));
        Asset asset = assetService.getAssetBySymbol(symbol);
        List<AssetPricePoint> history = assetService.getPriceHistory(symbol);
        List<AssetPricePoint> window = history.size() > windowDays
                ? history.subList(history.size() - windowDays, history.size())
                : history;

        List<MovementTimelineItemResponse> items = new ArrayList<>();
        addPriceItems(asset, window, windowDays, items);
        addNewsItems(asset, items);
        addEventItems(asset, items);

        String summary = "Connects the selected price move with related headlines, event signals, and price milestones.";
        return new MovementTimelineResponse(
                asset.getSymbol(),
                asset.getName(),
                windowDays,
                summary,
                items,
                "Informational context only. Timeline items can help explain movement, but they do not prove causation or predict prices.");
    }

    private void addPriceItems(
            Asset asset,
            List<AssetPricePoint> window,
            int windowDays,
            List<MovementTimelineItemResponse> items) {
        if (window.isEmpty()) {
            return;
        }

        AssetPricePoint first = window.get(0);
        AssetPricePoint latest = window.get(window.size() - 1);
        BigDecimal windowMove = percentChange(first.getPrice(), latest.getPrice());

        items.add(new MovementTimelineItemResponse(
                "PRICE",
                "Window start",
                asset.getSymbol() + " baseline",
                "The selected " + windowDays + "-day review starts at " + money(first.getPrice(), asset) + ".",
                first.getObservationDate().toString(),
                "Price context",
                "Baseline",
                first.getSource(),
                null));

        window.stream()
                .skip(1)
                .map(point -> largestMoveCandidate(window, point))
                .filter(candidate -> candidate != null)
                .max(Comparator.comparing(candidate -> candidate.absoluteMove))
                .ifPresent(candidate -> items.add(new MovementTimelineItemResponse(
                        "PRICE",
                        "Largest daily move",
                        asset.getSymbol() + " " + directionLabel(candidate.move),
                        "Moved " + signedPercent(candidate.move) + " from the previous stored observation.",
                        candidate.point.getObservationDate().toString(),
                        "High attention",
                        candidate.move.compareTo(BigDecimal.ZERO) >= 0 ? "Price moved up" : "Price moved down",
                        candidate.point.getSource(),
                        null)));

        items.add(new MovementTimelineItemResponse(
                "PRICE",
                "Latest point",
                asset.getSymbol() + " current window position",
                "Latest stored price is " + money(latest.getPrice(), asset) + ", a "
                        + signedPercent(windowMove) + " move across this window.",
                latest.getObservationDate().toString(),
                "Price signal",
                windowMove.compareTo(BigDecimal.ZERO) >= 0 ? "Price moved up" : "Price moved down",
                latest.getSource(),
                null));
    }

    private PriceMoveCandidate largestMoveCandidate(List<AssetPricePoint> window, AssetPricePoint point) {
        int index = window.indexOf(point);
        if (index <= 0) {
            return null;
        }
        BigDecimal move = percentChange(window.get(index - 1).getPrice(), point.getPrice());
        return new PriceMoveCandidate(point, move, move.abs());
    }

    private void addNewsItems(Asset asset, List<MovementTimelineItemResponse> items) {
        List<NewsItemResponse> newsItems = marketNewsService.getRelatedNews(asset.getSymbol());
        for (NewsItemResponse news : newsItems.stream().limit(5).toList()) {
            items.add(new MovementTimelineItemResponse(
                    "NEWS",
                    news.impactLevel(),
                    news.title(),
                    news.whyItMatters() == null || news.whyItMatters().isBlank()
                            ? news.description()
                            : news.whyItMatters(),
                    news.publishedAt(),
                    news.impactLevel(),
                    news.priceEffect(),
                    news.source(),
                    news.url()));
        }
    }

    private void addEventItems(Asset asset, List<MovementTimelineItemResponse> items) {
        List<EventSignalResponse> signals = eventIntelligenceService.getSignals(asset.getSymbol());
        for (EventSignalResponse signal : signals.stream().limit(3).toList()) {
            items.add(new MovementTimelineItemResponse(
                    "EVENT",
                    signal.confidence() + " confidence",
                    signal.title(),
                    signal.description(),
                    "",
                    signal.confidence() + " confidence",
                    "Watch signal",
                    signal.sourceType(),
                    null));
        }
    }

    private BigDecimal percentChange(BigDecimal first, BigDecimal second) {
        if (first == null || BigDecimal.ZERO.compareTo(first) == 0 || second == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return second.subtract(first)
                .divide(first, MathContext.DECIMAL64)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private String directionLabel(BigDecimal move) {
        return move.compareTo(BigDecimal.ZERO) >= 0 ? "jumped" : "dropped";
    }

    private String signedPercent(BigDecimal value) {
        return (value.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") + value + "%";
    }

    private String money(BigDecimal value, Asset asset) {
        if (value == null) {
            return "unknown";
        }
        String price = value.setScale(scaleFor(asset), RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
        return asset.getQuoteCurrency() + " " + price;
    }

    private int scaleFor(Asset asset) {
        if (asset.getCurrentPrice() != null && asset.getCurrentPrice().compareTo(BigDecimal.ONE) < 0) {
            return 6;
        }
        return asset.getCategory().name().equals("CURRENCY") ? 4 : 2;
    }

    private record PriceMoveCandidate(AssetPricePoint point, BigDecimal move, BigDecimal absoluteMove) {
    }
}
