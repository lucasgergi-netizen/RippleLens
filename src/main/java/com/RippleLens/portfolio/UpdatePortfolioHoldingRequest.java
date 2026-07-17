package com.RippleLens.portfolio;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

public record UpdatePortfolioHoldingRequest(
        @DecimalMin(value = "0.00000001") @Digits(integer = 18, fraction = 8) BigDecimal quantity,
        @DecimalMin(value = "0.0") @Digits(integer = 18, fraction = 8) BigDecimal averageCost) {
}
