package com.RippleLens.live;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MetalHistoricalPricePoint(
        String symbol,
        LocalDate observationDate,
        BigDecimal priceUsd,
        String source) {
}
