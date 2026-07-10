package com.RippleLens.portfolio;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioResponse(
        BigDecimal totalValue,
        BigDecimal totalCost,
        BigDecimal unrealizedProfitLoss,
        BigDecimal unrealizedProfitLossPercentage,
        BigDecimal dailyChangeAmount,
        BigDecimal dailyChangePercentage,
        int holdingCount,
        PortfolioHoldingResponse bestPerformer,
        PortfolioHoldingResponse worstPerformer,
        PortfolioHoldingResponse largestPosition,
        BigDecimal largestPositionPercentage,
        String dominantCategory,
        String exposureSummary,
        String riskLevel,
        String riskWarning,
        List<PortfolioAllocationResponse> allocations,
        List<PortfolioHoldingResponse> holdings) {
}
