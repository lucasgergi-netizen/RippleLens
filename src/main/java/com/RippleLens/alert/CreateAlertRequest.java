package com.RippleLens.alert;

import java.math.BigDecimal;

import com.RippleLens.model.AlertType;
import com.RippleLens.model.NotificationPreference;

public record CreateAlertRequest(
        String symbol,
        AlertType alertType,
        BigDecimal targetValue,
        NotificationPreference notificationPreference,
        Boolean recurring,
        String customName) {
}
