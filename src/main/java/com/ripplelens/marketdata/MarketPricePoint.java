package com.ripplelens.marketdata;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MarketPricePoint(
        LocalDate observationDate,
        BigDecimal priceUsd
) {
}
