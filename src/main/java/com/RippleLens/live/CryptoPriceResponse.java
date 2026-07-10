package com.RippleLens.live;

import java.math.BigDecimal;
import java.time.Instant;

public record CryptoPriceResponse(
        String symbol,
        String name,
        BigDecimal priceUsd,
        BigDecimal change24hPercentage,
        Instant lastUpdatedAt,
        String source) {
}
