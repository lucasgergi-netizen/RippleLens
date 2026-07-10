package com.RippleLens.workspace;

public record UpdateUserPreferencesRequest(
        String defaultCategory,
        String defaultRangeKey,
        String displayCurrency,
        Boolean showWelcomeScreen) {
}
