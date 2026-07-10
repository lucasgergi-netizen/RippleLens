package com.RippleLens.insight;

import java.math.BigDecimal;
import java.util.List;

public record InsightResponse(
        String symbol,
        String assetName,
        String category,
        BigDecimal latestPrice,
        double selectedPeriodChangePercentage,
        String movementDirection,
        String confidence,
        String analysisType,
        String modelName,
        String summary,
        List<String> possibleCauses,
        List<String> possibleConsequences,
        List<String> supportingSignals,
        String disclaimer) {
}
