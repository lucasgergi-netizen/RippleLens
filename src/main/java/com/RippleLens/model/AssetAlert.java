package com.RippleLens.model;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "asset_alerts")
public class AssetAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionKey;

    @ManyToOne(optional = false)
    private Asset asset;

    @Enumerated(EnumType.STRING)
    private AlertType alertType;

    @Enumerated(EnumType.STRING)
    private AlertStatus status;

    @Enumerated(EnumType.STRING)
    private NotificationPreference notificationPreference;

    private BigDecimal targetValue;
    private BigDecimal baselinePrice;
    private BigDecimal baselineDailyChangePercentage;
    private String customName;
    private boolean enabled;
    private boolean recurring;
    private Instant createdAt;
    private Instant triggeredAt;
    private String lastNotification;

    public AssetAlert() {
    }

    public AssetAlert(
            String sessionKey,
            Asset asset,
            AlertType alertType,
            BigDecimal targetValue,
            BigDecimal baselinePrice,
            BigDecimal baselineDailyChangePercentage,
            String customName,
            NotificationPreference notificationPreference,
            boolean recurring) {
        this.sessionKey = sessionKey;
        this.asset = asset;
        this.alertType = alertType;
        this.targetValue = targetValue;
        this.baselinePrice = baselinePrice;
        this.baselineDailyChangePercentage = baselineDailyChangePercentage;
        this.customName = customName;
        this.notificationPreference = notificationPreference;
        this.recurring = recurring;
        this.enabled = true;
        this.status = AlertStatus.ACTIVE;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public Asset getAsset() {
        return asset;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }

    public AlertStatus getStatus() {
        return status;
    }

    public void setStatus(AlertStatus status) {
        this.status = status;
    }

    public NotificationPreference getNotificationPreference() {
        return notificationPreference;
    }

    public void setNotificationPreference(NotificationPreference notificationPreference) {
        this.notificationPreference = notificationPreference;
    }

    public BigDecimal getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(BigDecimal targetValue) {
        this.targetValue = targetValue;
    }

    public BigDecimal getBaselinePrice() {
        return baselinePrice;
    }

    public void setBaselinePrice(BigDecimal baselinePrice) {
        this.baselinePrice = baselinePrice;
    }

    public BigDecimal getBaselineDailyChangePercentage() {
        return baselineDailyChangePercentage;
    }

    public void setBaselineDailyChangePercentage(BigDecimal baselineDailyChangePercentage) {
        this.baselineDailyChangePercentage = baselineDailyChangePercentage;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getTriggeredAt() {
        return triggeredAt;
    }

    public void setTriggeredAt(Instant triggeredAt) {
        this.triggeredAt = triggeredAt;
    }

    public String getLastNotification() {
        return lastNotification;
    }

    public void setLastNotification(String lastNotification) {
        this.lastNotification = lastNotification;
    }
}
