package com.RippleLens.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.RippleLens.model.AlertStatus;
import com.RippleLens.model.Asset;
import com.RippleLens.model.AssetCategory;
import com.RippleLens.model.RecentAssetView;
import com.RippleLens.model.UserPreference;
import com.RippleLens.portfolio.PortfolioResponse;
import com.RippleLens.repository.AppUserRepository;
import com.RippleLens.repository.AssetAlertRepository;
import com.RippleLens.repository.AssetRepository;
import com.RippleLens.repository.PortfolioHoldingRepository;
import com.RippleLens.repository.RecentAssetViewRepository;
import com.RippleLens.repository.UserPreferenceRepository;
import com.RippleLens.repository.WatchlistItemRepository;
import com.RippleLens.workspace.RecentAssetResponse;
import com.RippleLens.workspace.UpdateUserPreferencesRequest;
import com.RippleLens.workspace.UserPreferencesResponse;
import com.RippleLens.workspace.WorkspaceSummaryResponse;

@Service
public class WorkspaceService {

    private static final Set<String> ALLOWED_RANGES = Set.of("1D", "7D", "30D", "6M", "1Y");
    private static final Set<String> ALLOWED_CURRENCIES = Set.of("USD", "EUR", "AED", "GBP");

    private final CurrentUserService currentUserService;
    private final AppUserRepository appUserRepository;
    private final UserPreferenceRepository preferenceRepository;
    private final RecentAssetViewRepository recentAssetViewRepository;
    private final AssetRepository assetRepository;
    private final WatchlistItemRepository watchlistItemRepository;
    private final AssetAlertRepository assetAlertRepository;
    private final PortfolioHoldingRepository portfolioHoldingRepository;
    private final PortfolioService portfolioService;

    public WorkspaceService(
            CurrentUserService currentUserService,
            AppUserRepository appUserRepository,
            UserPreferenceRepository preferenceRepository,
            RecentAssetViewRepository recentAssetViewRepository,
            AssetRepository assetRepository,
            WatchlistItemRepository watchlistItemRepository,
            AssetAlertRepository assetAlertRepository,
            PortfolioHoldingRepository portfolioHoldingRepository,
            PortfolioService portfolioService) {
        this.currentUserService = currentUserService;
        this.appUserRepository = appUserRepository;
        this.preferenceRepository = preferenceRepository;
        this.recentAssetViewRepository = recentAssetViewRepository;
        this.assetRepository = assetRepository;
        this.watchlistItemRepository = watchlistItemRepository;
        this.assetAlertRepository = assetAlertRepository;
        this.portfolioHoldingRepository = portfolioHoldingRepository;
        this.portfolioService = portfolioService;
    }

    public WorkspaceSummaryResponse getWorkspace() {
        String sessionKey = currentUserService.sessionKey();
        UserPreference preferences = preferencesFor(sessionKey);
        PortfolioResponse portfolio = portfolioService.getPortfolio();
        long alertCount = assetAlertRepository.countBySessionKey(sessionKey);
        return new WorkspaceSummaryResponse(
                currentUserService.currentEmail().isPresent(),
                currentFullName(),
                currentUserService.currentEmail().orElse(null),
                currentUserService.currentEmail().isPresent() ? "Signed in" : "Guest mode",
                watchlistItemRepository.countBySessionKey(sessionKey),
                alertCount,
                assetAlertRepository.countBySessionKeyAndStatus(sessionKey, AlertStatus.ACTIVE),
                assetAlertRepository.countBySessionKeyAndStatus(sessionKey, AlertStatus.TRIGGERED),
                assetAlertRepository.countBySessionKeyAndStatus(sessionKey, AlertStatus.DISABLED),
                portfolioHoldingRepository.countBySessionKey(sessionKey),
                safe(portfolio.totalValue()),
                safe(portfolio.dailyChangeAmount()),
                safe(portfolio.dailyChangePercentage()),
                toPreferencesResponse(preferences),
                recentAssets(sessionKey));
    }

    @Transactional
    public UserPreferencesResponse updatePreferences(UpdateUserPreferencesRequest request) {
        UserPreference preferences = preferencesFor(currentUserService.sessionKey());
        if (request.defaultCategory() != null) {
            preferences.setDefaultCategory(parseCategory(request.defaultCategory()));
        }
        if (request.defaultRangeKey() != null) {
            preferences.setDefaultRangeKey(parseRange(request.defaultRangeKey()));
        }
        if (request.displayCurrency() != null) {
            preferences.setDisplayCurrency(parseCurrency(request.displayCurrency()));
        }
        if (request.showWelcomeScreen() != null) {
            preferences.setShowWelcomeScreen(request.showWelcomeScreen());
        }
        return toPreferencesResponse(preferenceRepository.save(preferences));
    }

    @Transactional
    public List<RecentAssetResponse> recordRecentAsset(String symbol) {
        Asset asset = findAsset(symbol);
        String sessionKey = currentUserService.sessionKey();
        RecentAssetView view = recentAssetViewRepository.findBySessionKeyAndAsset(sessionKey, asset)
                .orElseGet(() -> new RecentAssetView(sessionKey, asset));
        view.touch();
        recentAssetViewRepository.save(view);
        return recentAssets(sessionKey);
    }

    private UserPreference preferencesFor(String sessionKey) {
        return preferenceRepository.findBySessionKey(sessionKey)
                .orElseGet(() -> preferenceRepository.save(new UserPreference(sessionKey)));
    }

    private List<RecentAssetResponse> recentAssets(String sessionKey) {
        return recentAssetViewRepository.findTop8BySessionKeyOrderByViewedAtDesc(sessionKey)
                .stream()
                .map(this::toRecentAssetResponse)
                .toList();
    }

    private String currentFullName() {
        return currentUserService.currentEmail()
                .flatMap(appUserRepository::findByEmailIgnoreCase)
                .map(user -> user.getFullName())
                .orElse("Guest");
    }

    private Asset findAsset(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Asset symbol is required.");
        }
        return assetRepository.findBySymbolIgnoreCase(symbol)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown asset symbol: " + symbol));
    }

    private AssetCategory parseCategory(String value) {
        try {
            return AssetCategory.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (RuntimeException error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown default category.");
        }
    }

    private String parseRange(String value) {
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (!ALLOWED_RANGES.contains(normalized)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported chart range.");
        }
        return normalized;
    }

    private String parseCurrency(String value) {
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (!ALLOWED_CURRENCIES.contains(normalized)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported display currency.");
        }
        return normalized;
    }

    private UserPreferencesResponse toPreferencesResponse(UserPreference preferences) {
        return new UserPreferencesResponse(
                preferences.getDefaultCategory().name(),
                preferences.getDefaultRangeKey(),
                preferences.getDisplayCurrency(),
                preferences.isShowWelcomeScreen());
    }

    private RecentAssetResponse toRecentAssetResponse(RecentAssetView view) {
        Asset asset = view.getAsset();
        return new RecentAssetResponse(
                asset.getSymbol(),
                asset.getName(),
                asset.getCategory().name(),
                safe(asset.getCurrentPrice()),
                safe(asset.getDailyChangePercentage()),
                asset.getQuoteCurrency(),
                view.getViewedAt());
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
