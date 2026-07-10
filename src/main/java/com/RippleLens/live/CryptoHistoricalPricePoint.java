package com.RippleLens.live;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CryptoHistoricalPricePoint(
        String symbol,
        LocalDate observationDate,
        BigDecimal priceUsd,
        String source) {
}
