package com.RippleLens.live;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CurrencyHistoricalRatePoint(
        String symbol,
        LocalDate observationDate,
        BigDecimal usdPrice,
        String source) {
}
