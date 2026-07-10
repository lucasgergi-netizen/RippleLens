package com.RippleLens.portfolio;

import java.math.BigDecimal;

public record CreatePortfolioHoldingRequest(
        String symbol,
        BigDecimal quantity,
        BigDecimal averageCost) {
}
