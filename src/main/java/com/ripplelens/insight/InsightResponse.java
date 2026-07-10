package com.ripplelens.insight;

import java.math.BigDecimal;
import java.util.List;

public record InsightResponse(
        String symbol,
        String assetName,
        BigDecimal latestPriceUsd,
        double thirtyDayChangePercent,
        String confidence,
        String dataSource,
        boolean live,
        List<String> possibleCauses,
        List<String> possibleConsequences,
        String disclaimer
) {
}
