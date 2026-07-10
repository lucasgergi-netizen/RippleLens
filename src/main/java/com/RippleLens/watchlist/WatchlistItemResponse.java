package com.RippleLens.watchlist;

import java.math.BigDecimal;
import java.time.Instant;

public record WatchlistItemResponse(
        Long id,
        String symbol,
        String assetName,
        String category,
        BigDecimal currentPrice,
        BigDecimal dailyChangePercentage,
        String quoteCurrency,
        Instant createdAt) {
}
