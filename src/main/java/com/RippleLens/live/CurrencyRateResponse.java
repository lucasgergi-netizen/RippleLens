package com.RippleLens.live;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CurrencyRateResponse(
        String symbol,
        String name,
        BigDecimal usdPrice,
        BigDecimal dailyChangePercentage,
        LocalDate date,
        String source) {
}
