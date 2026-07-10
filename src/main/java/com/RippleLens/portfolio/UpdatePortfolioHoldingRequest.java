package com.RippleLens.portfolio;

import java.math.BigDecimal;

public record UpdatePortfolioHoldingRequest(
        BigDecimal quantity,
        BigDecimal averageCost) {
}
