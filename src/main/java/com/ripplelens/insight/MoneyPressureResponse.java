package com.ripplelens.insight;

public record MoneyPressureResponse(
        String metalSymbol,
        String interpretation,
        double metalMomentumScore,
        double cryptoRiskScore,
        double currencyStressScore,
        String confidence
) {
}
