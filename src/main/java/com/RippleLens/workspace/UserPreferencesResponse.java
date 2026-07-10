package com.RippleLens.workspace;

public record UserPreferencesResponse(
        String defaultCategory,
        String defaultRangeKey,
        String displayCurrency,
        boolean showWelcomeScreen) {
}
