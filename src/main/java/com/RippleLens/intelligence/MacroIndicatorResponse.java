package com.RippleLens.intelligence;

import java.math.BigDecimal;

public record MacroIndicatorResponse(
        String code,
        String name,
        String country,
        BigDecimal value,
        String year,
        String unit,
        String source,
        String interpretation) {
}
