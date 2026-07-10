package com.ripplelens.api;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PricePointResponse(
        LocalDate date,
        BigDecimal priceUsd,
        String dataSource,
        boolean live
) {
}
