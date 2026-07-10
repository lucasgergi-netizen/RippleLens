package com.RippleLens.workspace;

import java.math.BigDecimal;
import java.util.List;

public record WorkspaceSummaryResponse(
        boolean authenticated,
        String fullName,
        String email,
        String sessionMode,
        long watchlistCount,
        long alertCount,
        long activeAlertCount,
        long triggeredAlertCount,
        long disabledAlertCount,
        long portfolioHoldingCount,
        BigDecimal portfolioValue,
        BigDecimal portfolioDailyChangeAmount,
        BigDecimal portfolioDailyChangePercentage,
        UserPreferencesResponse preferences,
        List<RecentAssetResponse> recentAssets) {
}
