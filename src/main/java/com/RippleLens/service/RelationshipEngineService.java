package com.RippleLens.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.RippleLens.model.Asset;
import com.RippleLens.model.AssetCategory;
import com.RippleLens.model.AssetPricePoint;
import com.RippleLens.relationship.RelationshipItemResponse;
import com.RippleLens.relationship.RelationshipReportResponse;
import com.RippleLens.repository.AssetPricePointRepository;
import com.RippleLens.repository.AssetRepository;
import com.RippleLens.security.InputPolicy;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RelationshipEngineService {

    private static final int MIN_PAIRED_RETURNS = 6;

    private final AssetRepository assetRepository;
    private final AssetPricePointRepository pricePointRepository;

    public RelationshipEngineService(AssetRepository assetRepository, AssetPricePointRepository pricePointRepository) {
        this.assetRepository = assetRepository;
        this.pricePointRepository = pricePointRepository;
    }

    public RelationshipReportResponse analyze(String symbol, int days) {
        int windowDays = Math.max(7, Math.min(days, 365));
        Asset selectedAsset = assetRepository.findBySymbolIgnoreCase(InputPolicy.assetSymbol(symbol))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown asset symbol."));
        List<AssetPricePoint> selectedWindow = windowFor(selectedAsset, windowDays);
        Map<LocalDate, Double> selectedReturns = dailyReturns(selectedWindow);

        List<RelationshipItemResponse> relationships = new ArrayList<>();
        for (Asset candidate : assetRepository.findAll()) {
            if (candidate.getSymbol().equalsIgnoreCase(selectedAsset.getSymbol())) {
                continue;
            }

            List<AssetPricePoint> candidateWindow = windowFor(candidate, windowDays);
            Map<LocalDate, Double> candidateReturns = dailyReturns(candidateWindow);
            List<Double> selectedAligned = new ArrayList<>();
            List<Double> candidateAligned = new ArrayList<>();

            for (Map.Entry<LocalDate, Double> entry : selectedReturns.entrySet()) {
                Double candidateReturn = candidateReturns.get(entry.getKey());
                if (candidateReturn != null) {
                    selectedAligned.add(entry.getValue());
                    candidateAligned.add(candidateReturn);
                }
            }

            if (selectedAligned.size() < MIN_PAIRED_RETURNS) {
                continue;
            }

            double correlation = pearson(selectedAligned, candidateAligned);
            if (!Double.isFinite(correlation)) {
                continue;
            }

            double selectedMove = movePercentage(selectedWindow);
            double candidateMove = movePercentage(candidateWindow);
            boolean unusual = isUnusual(correlation, selectedMove, candidateMove);
            relationships.add(toRelationship(selectedAsset, candidate, correlation, selectedAligned.size(),
                    selectedMove, candidateMove, unusual));
        }

        relationships.sort(Comparator
                .comparingDouble((RelationshipItemResponse item) -> Math.abs(item.correlation()))
                .reversed()
                .thenComparing(RelationshipItemResponse::symbol));

        List<RelationshipItemResponse> topRelationships = relationships.stream().limit(6).toList();
        String topRelationship = topRelationships.isEmpty()
                ? "No reliable relationship detected yet"
                : topRelationships.get(0).symbol() + " (" + topRelationships.get(0).strength() + " "
                        + topRelationships.get(0).direction() + ")";

        return new RelationshipReportResponse(
                selectedAsset.getSymbol(),
                selectedAsset.getName(),
                windowDays,
                selectedReturns.size(),
                topRelationship,
                summaryFor(selectedAsset, topRelationships, windowDays),
                topRelationships,
                insightsFor(selectedAsset, topRelationships),
                "Correlation is historical context only. It is not a forecast or financial advice.");
    }

    private RelationshipItemResponse toRelationship(
            Asset selectedAsset,
            Asset candidate,
            double correlation,
            int pairedObservations,
            double selectedMove,
            double candidateMove,
            boolean unusual) {
        String strength = strengthFor(correlation);
        String direction = correlation >= 0 ? "positive" : "negative";
        String type = typeFor(selectedAsset, candidate, direction);
        return new RelationshipItemResponse(
                candidate.getSymbol(),
                candidate.getName(),
                candidate.getCategory().name(),
                round(correlation),
                strength,
                direction,
                type,
                pairedObservations,
                round(selectedMove),
                round(candidateMove),
                unusual,
                explanationFor(selectedAsset, candidate, correlation, strength, direction, unusual));
    }

    private List<AssetPricePoint> windowFor(Asset asset, int days) {
        List<AssetPricePoint> history = pricePointRepository.findByAssetOrderByObservationDateAsc(asset);
        return history.size() > days ? history.subList(history.size() - days, history.size()) : history;
    }

    private Map<LocalDate, Double> dailyReturns(List<AssetPricePoint> points) {
        Map<LocalDate, Double> returns = new LinkedHashMap<>();
        for (int index = 1; index < points.size(); index++) {
            BigDecimal previous = points.get(index - 1).getPrice();
            BigDecimal current = points.get(index).getPrice();
            if (previous == null || current == null || previous.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            double returnValue = current.subtract(previous)
                    .divide(previous, 10, RoundingMode.HALF_UP)
                    .doubleValue();
            returns.put(points.get(index).getObservationDate(), returnValue);
        }
        return returns;
    }

    private double pearson(List<Double> first, List<Double> second) {
        double firstMean = mean(first);
        double secondMean = mean(second);
        double numerator = 0;
        double firstVariance = 0;
        double secondVariance = 0;

        for (int index = 0; index < first.size(); index++) {
            double firstDelta = first.get(index) - firstMean;
            double secondDelta = second.get(index) - secondMean;
            numerator += firstDelta * secondDelta;
            firstVariance += firstDelta * firstDelta;
            secondVariance += secondDelta * secondDelta;
        }

        double denominator = Math.sqrt(firstVariance * secondVariance);
        return denominator == 0 ? 0 : numerator / denominator;
    }

    private double mean(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    private double movePercentage(List<AssetPricePoint> points) {
        if (points.size() < 2) {
            return 0;
        }
        BigDecimal first = points.get(0).getPrice();
        BigDecimal last = points.get(points.size() - 1).getPrice();
        if (first == null || last == null || first.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        return last.subtract(first)
                .divide(first, 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    private boolean isUnusual(double correlation, double selectedMove, double candidateMove) {
        if (Math.abs(correlation) < 0.45 || Math.abs(selectedMove) < 0.2 || Math.abs(candidateMove) < 0.2) {
            return false;
        }
        boolean sameDirectionMove = Math.signum(selectedMove) == Math.signum(candidateMove);
        return correlation > 0 ? !sameDirectionMove : sameDirectionMove;
    }

    private String strengthFor(double correlation) {
        double absolute = Math.abs(correlation);
        if (absolute >= 0.75) {
            return "strong";
        }
        if (absolute >= 0.45) {
            return "moderate";
        }
        return "weak";
    }

    private String typeFor(Asset selectedAsset, Asset candidate, String direction) {
        if (selectedAsset.getCategory() == candidate.getCategory()) {
            return direction.equals("positive") ? "same-market movement" : "divergence inside same market";
        }
        if (selectedAsset.getCategory() == AssetCategory.METAL && candidate.getCategory() == AssetCategory.CURRENCY) {
            return "metal versus currency pressure";
        }
        if (selectedAsset.getCategory() == AssetCategory.CURRENCY && candidate.getCategory() == AssetCategory.METAL) {
            return "currency versus hard-asset pressure";
        }
        if (selectedAsset.getCategory() == AssetCategory.CRYPTOCURRENCY
                && candidate.getCategory() == AssetCategory.CRYPTOCURRENCY) {
            return "crypto risk basket";
        }
        return "cross-market signal";
    }

    private String explanationFor(
            Asset selectedAsset,
            Asset candidate,
            double correlation,
            String strength,
            String direction,
            boolean unusual) {
        String base = selectedAsset.getSymbol() + " and " + candidate.getSymbol() + " show a " + strength + " "
                + direction + " relationship over the selected window.";
        if (unusual) {
            return base + " Recent direction is unusual versus that relationship, so this pair deserves attention.";
        }
        if (Math.abs(correlation) < 0.45) {
            return base + " The link is weak, so avoid over-reading the connection.";
        }
        return base + " This can help explain whether the move is isolated or part of a broader market pattern.";
    }

    private String summaryFor(Asset asset, List<RelationshipItemResponse> relationships, int windowDays) {
        if (relationships.isEmpty()) {
            return "RippleLens does not yet have enough aligned observations to detect reliable relationships for "
                    + asset.getSymbol() + ".";
        }
        RelationshipItemResponse top = relationships.get(0);
        return asset.getSymbol() + " currently has its clearest " + windowDays + "-day relationship with "
                + top.symbol() + ", showing a " + top.strength() + " " + top.direction()
                + " correlation. Use this as context for explaining movement, not as a prediction.";
    }

    private List<String> insightsFor(Asset asset, List<RelationshipItemResponse> relationships) {
        List<String> insights = new ArrayList<>();
        long unusualCount = relationships.stream().filter(RelationshipItemResponse::unusualBehavior).count();
        if (relationships.isEmpty()) {
            insights.add("More historical observations are needed before relationship signals become useful.");
            return insights;
        }
        insights.add("The engine compares daily percentage returns, not raw prices, so expensive and cheap assets can be compared fairly.");
        insights.add("Positive correlation means assets tended to move together; negative correlation means they tended to move in opposite directions.");
        if (unusualCount > 0) {
            insights.add(unusualCount + " relationship signal is behaving unusually versus its recent history.");
        } else {
            insights.add(asset.getSymbol() + " is not showing major relationship breaks in the strongest detected pairs.");
        }
        return insights;
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
