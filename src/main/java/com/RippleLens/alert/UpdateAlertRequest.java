package com.RippleLens.alert;

import java.math.BigDecimal;

import com.RippleLens.model.AlertType;
import com.RippleLens.model.NotificationPreference;

public record UpdateAlertRequest(
        AlertType alertType,
        BigDecimal targetValue,
        NotificationPreference notificationPreference,
        Boolean recurring,
        Boolean enabled,
        String customName) {
}
