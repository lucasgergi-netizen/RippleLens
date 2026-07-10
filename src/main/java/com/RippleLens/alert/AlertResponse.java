package com.RippleLens.alert;

import java.math.BigDecimal;
import java.time.Instant;

import com.RippleLens.model.AlertStatus;
import com.RippleLens.model.AlertType;
import com.RippleLens.model.NotificationPreference;

public record AlertResponse(
        Long id,
        String symbol,
        String assetName,
        String category,
        AlertType alertType,
        BigDecimal targetValue,
        BigDecimal currentPrice,
        BigDecimal currentDailyChangePercentage,
        AlertStatus status,
        NotificationPreference notificationPreference,
        boolean enabled,
        boolean recurring,
        Instant createdAt,
        Instant triggeredAt,
        String customName,
        String message,
        String lastNotification) {
}
