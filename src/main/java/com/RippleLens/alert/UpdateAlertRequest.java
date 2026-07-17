package com.RippleLens.alert;

import java.math.BigDecimal;

import com.RippleLens.model.AlertType;
import com.RippleLens.model.NotificationPreference;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;

public record UpdateAlertRequest(
        AlertType alertType,
        @DecimalMin(value = "0.00000001") @Digits(integer = 18, fraction = 8) BigDecimal targetValue,
        NotificationPreference notificationPreference,
        Boolean recurring,
        Boolean enabled,
        @Size(max = 120) String customName) {
}
