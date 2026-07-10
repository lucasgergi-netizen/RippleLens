package com.RippleLens.price;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PriceSeriesPointResponse(
        String label,
        LocalDateTime timestamp,
        BigDecimal price,
        String source,
        String mode) {
}
