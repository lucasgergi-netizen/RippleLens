package com.RippleLens.alert;

import java.math.BigDecimal;

import com.RippleLens.model.AlertType;
import com.RippleLens.model.NotificationPreference;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAlertRequest(
        @NotBlank @Size(max = 32) String symbol,
        @NotNull AlertType alertType,
        @NotNull @DecimalMin(value = "0.00000001") @Digits(integer = 18, fraction = 8) BigDecimal targetValue,
        NotificationPreference notificationPreference,
        Boolean recurring,
        @Size(max = 120) String customName) {
}
