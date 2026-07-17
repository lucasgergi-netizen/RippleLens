package com.RippleLens.workspace;

import jakarta.validation.constraints.Pattern;

public record UpdateUserPreferencesRequest(
        @Pattern(regexp = "(?i)^(METALS|CRYPTO|CURRENCIES)$") String defaultCategory,
        @Pattern(regexp = "^(1|7|30|180|365)D$") String defaultRangeKey,
        @Pattern(regexp = "^[A-Za-z]{3}$") String displayCurrency,
        Boolean showWelcomeScreen) {
}
