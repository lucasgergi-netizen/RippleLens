package com.RippleLens.portfolio;

import java.math.BigDecimal;
import java.time.Instant;

public record PortfolioHoldingResponse(
        Long id,
        String symbol,
        String assetName,
        String category,
        BigDecimal quantity,
        BigDecimal averageCost,
        BigDecimal currentPrice,
        BigDecimal currentValue,
        BigDecimal costBasis,
        BigDecimal unrealizedProfitLoss,
        BigDecimal unrealizedProfitLossPercentage,
        BigDecimal dailyChangePercentage,
        BigDecimal dailyChangeAmount,
        BigDecimal allocationPercentage,
        String quoteCurrency,
        Instant updatedAt) {
}
