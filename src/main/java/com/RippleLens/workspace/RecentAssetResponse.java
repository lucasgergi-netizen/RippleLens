package com.RippleLens.workspace;

import java.math.BigDecimal;
import java.time.Instant;

public record RecentAssetResponse(
        String symbol,
        String name,
        String category,
        BigDecimal currentPrice,
        BigDecimal dailyChangePercentage,
        String quoteCurrency,
        Instant viewedAt) {
}
