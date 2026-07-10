package com.RippleLens.portfolio;

import java.math.BigDecimal;

public record PortfolioAllocationResponse(
        String category,
        BigDecimal value,
        BigDecimal allocationPercentage) {
}
