package com.RippleLens.live;

import java.math.BigDecimal;
import java.time.Instant;

public record MetalPriceResponse(
        String symbol,
        String name,
        BigDecimal priceUsd,
        String unit,
        BigDecimal dailyChangePercentage,
        Instant updatedAt,
        String source) {
}
