package com.RippleLens.live;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.RippleLens.alert.AlertResponse;

public record LivePriceTickResponse(
        String symbol,
        String assetName,
        String category,
        BigDecimal price,
        BigDecimal previousPrice,
        BigDecimal dailyChangePercentage,
        BigDecimal priceChangeAmount,
        String direction,
        String quoteCurrency,
        String unit,
        String source,
        Instant changedAt,
        List<AlertResponse> triggeredAlerts) {
}
