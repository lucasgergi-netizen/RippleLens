package com.RippleLens.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RippleLens.model.AlertType;
import com.RippleLens.model.Asset;
import com.RippleLens.model.AssetAlert;
import com.RippleLens.model.AlertStatus;
import com.RippleLens.model.NotificationPreference;

public interface AssetAlertRepository extends JpaRepository<AssetAlert, Long> {

    List<AssetAlert> findBySessionKeyOrderByCreatedAtDesc(String sessionKey);

    List<AssetAlert> findBySessionKeyAndAssetOrderByCreatedAtDesc(String sessionKey, Asset asset);

    List<AssetAlert> findByAssetOrderByCreatedAtDesc(Asset asset);

    long countBySessionKey(String sessionKey);

    long countBySessionKeyAndStatus(String sessionKey, AlertStatus status);

    Optional<AssetAlert> findBySessionKeyAndAssetAndAlertTypeAndTargetValueAndRecurringAndCustomNameAndNotificationPreference(
            String sessionKey,
            Asset asset,
            AlertType alertType,
            BigDecimal targetValue,
            boolean recurring,
            String customName,
            NotificationPreference notificationPreference);
}
