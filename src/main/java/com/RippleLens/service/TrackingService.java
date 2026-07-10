package com.RippleLens.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.RippleLens.alert.AlertResponse;
import com.RippleLens.alert.CreateAlertRequest;
import com.RippleLens.alert.UpdateAlertRequest;
import com.RippleLens.model.AlertStatus;
import com.RippleLens.model.AlertType;
import com.RippleLens.model.Asset;
import com.RippleLens.model.AssetAlert;
import com.RippleLens.model.NotificationPreference;
import com.RippleLens.model.WatchlistItem;
import com.RippleLens.repository.AssetAlertRepository;
import com.RippleLens.repository.AssetRepository;
import com.RippleLens.repository.WatchlistItemRepository;
import com.RippleLens.watchlist.CreateWatchlistItemRequest;
import com.RippleLens.watchlist.WatchlistItemResponse;

@Service
public class TrackingService {

    private final WatchlistItemRepository watchlistRepository;
    private final AssetAlertRepository alertRepository;
    private final AssetRepository assetRepository;
    private final CurrentUserService currentUserService;

    public TrackingService(
            WatchlistItemRepository watchlistRepository,
            AssetAlertRepository alertRepository,
            AssetRepository assetRepository,
            CurrentUserService currentUserService) {
        this.watchlistRepository = watchlistRepository;
        this.alertRepository = alertRepository;
        this.assetRepository = assetRepository;
        this.currentUserService = currentUserService;
    }

    public List<WatchlistItemResponse> getWatchlist() {
        return watchlistRepository.findBySessionKeyOrderByCreatedAtAsc(sessionKey())
                .stream()
                .map(this::toWatchlistResponse)
                .toList();
    }

    @Transactional
    public WatchlistItemResponse addWatchlistItem(CreateWatchlistItemRequest request) {
        Asset asset = findAsset(request.symbol());
        String sessionKey = sessionKey();
        return watchlistRepository.findBySessionKeyAndAsset(sessionKey, asset)
                .map(this::toWatchlistResponse)
                .orElseGet(() -> toWatchlistResponse(watchlistRepository.save(new WatchlistItem(sessionKey, asset))));
    }

    @Transactional
    public void removeWatchlistItem(Long id) {
        String sessionKey = sessionKey();
        WatchlistItem item = watchlistRepository.findById(id)
                .filter(existing -> sessionKey.equals(existing.getSessionKey()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Watchlist item not found"));
        watchlistRepository.delete(item);
    }

    public List<AlertResponse> getAlerts(String symbol, AlertStatus status) {
        String sessionKey = sessionKey();
        List<AssetAlert> alerts = symbol == null || symbol.isBlank()
                ? alertRepository.findBySessionKeyOrderByCreatedAtDesc(sessionKey)
                : alertRepository.findBySessionKeyAndAssetOrderByCreatedAtDesc(sessionKey, findAsset(symbol));
        return alerts.stream()
                .map(this::evaluateAndSave)
                .filter(alert -> status == null || alert.getStatus() == status)
                .map(this::toAlertResponse)
                .toList();
    }

    @Transactional
    public AlertResponse createAlert(CreateAlertRequest request) {
        Asset asset = findAsset(request.symbol());
        validateAlert(request.alertType(), request.targetValue());
        boolean recurring = Boolean.TRUE.equals(request.recurring());
        String customName = normalizeName(request.customName());
        NotificationPreference notificationPreference = normalizeNotificationPreference(request.notificationPreference());

        alertRepository
                .findBySessionKeyAndAssetAndAlertTypeAndTargetValueAndRecurringAndCustomNameAndNotificationPreference(
                        sessionKey(),
                        asset,
                        request.alertType(),
                        request.targetValue(),
                        recurring,
                        customName,
                        notificationPreference)
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "An identical alert already exists.");
                });

        AssetAlert alert = new AssetAlert(
                sessionKey(),
                asset,
                request.alertType(),
                request.targetValue(),
                asset.getCurrentPrice(),
                safeDailyChange(asset),
                customName,
                notificationPreference,
                recurring);
        return toAlertResponse(evaluateAndSave(alertRepository.save(alert)));
    }

    @Transactional
    public AlertResponse updateAlert(Long id, UpdateAlertRequest request) {
        AssetAlert alert = findAlert(id);
        AlertType alertType = request.alertType() == null ? alert.getAlertType() : request.alertType();
        BigDecimal targetValue = request.targetValue() == null ? alert.getTargetValue() : request.targetValue();
        boolean recurring = request.recurring() == null ? alert.isRecurring() : request.recurring();
        String customName = request.customName() == null ? alert.getCustomName() : normalizeName(request.customName());
        NotificationPreference notificationPreference = request.notificationPreference() == null
                ? normalizeNotificationPreference(alert.getNotificationPreference())
                : normalizeNotificationPreference(request.notificationPreference());
        validateAlert(alertType, targetValue);
        ensureAlertIsNotDuplicate(alert, alertType, targetValue, recurring, customName, notificationPreference);

        alert.setAlertType(alertType);
        alert.setTargetValue(targetValue);
        alert.setRecurring(recurring);
        alert.setNotificationPreference(notificationPreference);
        if (request.enabled() != null) {
            alert.setEnabled(request.enabled());
            if (!request.enabled()) {
                alert.setStatus(AlertStatus.DISABLED);
            } else if (alert.getStatus() == AlertStatus.DISABLED) {
                alert.setStatus(AlertStatus.ACTIVE);
            }
        }
        alert.setCustomName(customName);
        alert.setBaselinePrice(alert.getAsset().getCurrentPrice());
        alert.setBaselineDailyChangePercentage(safeDailyChange(alert.getAsset()));
        return toAlertResponse(evaluateAndSave(alertRepository.save(alert)));
    }

    @Transactional
    public AlertResponse setAlertEnabled(Long id, boolean enabled) {
        AssetAlert alert = findAlert(id);
        alert.setEnabled(enabled);
        alert.setStatus(enabled ? AlertStatus.ACTIVE : AlertStatus.DISABLED);
        return toAlertResponse(evaluateAndSave(alertRepository.save(alert)));
    }

    @Transactional
    public void deleteAlert(Long id) {
        alertRepository.delete(findAlert(id));
    }

    @Transactional
    public List<AlertResponse> evaluateAlertsForAsset(String symbol) {
        Asset asset = findAsset(symbol);
        List<AlertResponse> newlyTriggeredAlerts = new ArrayList<>();
        for (AssetAlert alert : alertRepository.findByAssetOrderByCreatedAtDesc(asset)) {
            AlertStatus previousStatus = alert.getStatus();
            Instant previousTriggeredAt = alert.getTriggeredAt();
            AssetAlert evaluated = evaluateAndSave(alert);
            boolean newlyTriggered = evaluated.getStatus() == AlertStatus.TRIGGERED
                    && (previousStatus != AlertStatus.TRIGGERED
                            || !Objects.equals(previousTriggeredAt, evaluated.getTriggeredAt()));
            if (newlyTriggered) {
                newlyTriggeredAlerts.add(toAlertResponse(evaluated));
            }
        }
        return newlyTriggeredAlerts;
    }

    private AssetAlert evaluateAndSave(AssetAlert alert) {
        if (!alert.isEnabled()) {
            if (alert.getStatus() != AlertStatus.TRIGGERED) {
                alert.setStatus(AlertStatus.DISABLED);
            }
            return alertRepository.save(alert);
        }

        boolean triggered = isTriggered(alert);
        if (triggered) {
            boolean alreadyTriggered = alert.getStatus() == AlertStatus.TRIGGERED;
            alert.setStatus(AlertStatus.TRIGGERED);
            if (!alreadyTriggered) {
                alert.setTriggeredAt(Instant.now());
                alert.setLastNotification(buildTriggeredMessage(alert));
            }
            if (!alert.isRecurring()) {
                alert.setEnabled(false);
            }
        } else {
            alert.setStatus(AlertStatus.ACTIVE);
        }
        return alertRepository.save(alert);
    }

    private boolean isTriggered(AssetAlert alert) {
        BigDecimal currentPrice = alert.getAsset().getCurrentPrice();
        BigDecimal target = alert.getTargetValue();
        BigDecimal baseline = alert.getBaselinePrice() == null ? currentPrice : alert.getBaselinePrice();
        BigDecimal dailyChange = safeDailyChange(alert.getAsset());
        return switch (alert.getAlertType()) {
            case PRICE_ABOVE -> currentPrice.compareTo(target) > 0;
            case PRICE_BELOW -> currentPrice.compareTo(target) < 0;
            case PRICE_TARGET_REACHED -> currentPrice.compareTo(target) == 0;
            case DAILY_PERCENT_CHANGE_REACHES -> dailyChange.abs().compareTo(target.abs()) >= 0;
            case PRICE_CHANGE_AMOUNT_REACHES -> currentPrice.subtract(baseline).abs().compareTo(target.abs()) >= 0;
            case PERCENT_INCREASE_REACHES -> percentChange(baseline, currentPrice).compareTo(target.abs()) >= 0;
            case PERCENT_DECREASE_REACHES -> percentChange(baseline, currentPrice).compareTo(target.abs().negate()) <= 0;
        };
    }

    private BigDecimal percentChange(BigDecimal baseline, BigDecimal currentPrice) {
        if (baseline == null || BigDecimal.ZERO.compareTo(baseline) == 0) {
            return BigDecimal.ZERO;
        }
        return currentPrice.subtract(baseline)
                .divide(baseline, MathContext.DECIMAL64)
                .multiply(BigDecimal.valueOf(100));
    }

    private void validateAlert(AlertType alertType, BigDecimal targetValue) {
        if (alertType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Alert type is required.");
        }
        if (targetValue == null || targetValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target value must be greater than zero.");
        }
    }

    private void ensureAlertIsNotDuplicate(
            AssetAlert currentAlert,
            AlertType alertType,
            BigDecimal targetValue,
            boolean recurring,
            String customName,
            NotificationPreference notificationPreference) {
        alertRepository
                .findBySessionKeyAndAssetAndAlertTypeAndTargetValueAndRecurringAndCustomNameAndNotificationPreference(
                        currentAlert.getSessionKey(),
                        currentAlert.getAsset(),
                        alertType,
                        targetValue,
                        recurring,
                        customName,
                        notificationPreference)
                .filter(existing -> !existing.getId().equals(currentAlert.getId()))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "An identical alert already exists.");
                });
    }

    private Asset findAsset(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Asset symbol is required.");
        }
        return assetRepository.findBySymbolIgnoreCase(symbol)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown asset symbol: " + symbol));
    }

    private AssetAlert findAlert(Long id) {
        String sessionKey = sessionKey();
        return alertRepository.findById(id)
                .filter(alert -> sessionKey.equals(alert.getSessionKey()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found"));
    }

    private String sessionKey() {
        return currentUserService.sessionKey();
    }

    private WatchlistItemResponse toWatchlistResponse(WatchlistItem item) {
        Asset asset = item.getAsset();
        return new WatchlistItemResponse(
                item.getId(),
                asset.getSymbol(),
                asset.getName(),
                asset.getCategory().name(),
                asset.getCurrentPrice(),
                safeDailyChange(asset),
                asset.getQuoteCurrency(),
                item.getCreatedAt());
    }

    private AlertResponse toAlertResponse(AssetAlert alert) {
        Asset asset = alert.getAsset();
        return new AlertResponse(
                alert.getId(),
                asset.getSymbol(),
                asset.getName(),
                asset.getCategory().name(),
                alert.getAlertType(),
                alert.getTargetValue(),
                asset.getCurrentPrice(),
                safeDailyChange(asset),
                alert.getStatus(),
                normalizeNotificationPreference(alert.getNotificationPreference()),
                alert.isEnabled(),
                alert.isRecurring(),
                alert.getCreatedAt(),
                alert.getTriggeredAt(),
                alert.getCustomName(),
                describeAlert(alert),
                alert.getLastNotification());
    }

    private BigDecimal safeDailyChange(Asset asset) {
        return asset.getDailyChangePercentage() == null ? BigDecimal.ZERO : asset.getDailyChangePercentage();
    }

    private String normalizeName(String customName) {
        if (customName == null || customName.isBlank()) {
            return null;
        }
        return customName.trim();
    }

    private NotificationPreference normalizeNotificationPreference(NotificationPreference preference) {
        return preference == null ? NotificationPreference.IN_APP : preference;
    }

    private String describeAlert(AssetAlert alert) {
        String label = switch (alert.getAlertType()) {
            case PRICE_ABOVE -> "price goes above";
            case PRICE_BELOW -> "price goes below";
            case PERCENT_INCREASE_REACHES -> "percentage increase reaches";
            case PERCENT_DECREASE_REACHES -> "percentage decrease reaches";
            case DAILY_PERCENT_CHANGE_REACHES -> "daily percentage change reaches";
            case PRICE_TARGET_REACHED -> "price reaches target";
            case PRICE_CHANGE_AMOUNT_REACHES -> "price changes by amount";
        };
        return alert.getAsset().getSymbol() + " " + label + " " + alert.getTargetValue();
    }

    private String buildTriggeredMessage(AssetAlert alert) {
        return "Alert reached: " + describeAlert(alert) + ". Current price: " + alert.getAsset().getCurrentPrice() + ".";
    }
}
