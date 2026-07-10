const assetSelect = document.querySelector("#assetSelect");
const compareSelect = document.querySelector("#compareSelect");
const analyzeButton = document.querySelector("#analyzeButton");
const compareButton = document.querySelector("#compareButton");
const clearCompareButton = document.querySelector("#clearCompareButton");
const watchButton = document.querySelector("#watchButton");
const setAlertButton = document.querySelector("#setAlertButton");
const categoryButtons = document.querySelectorAll(".category-button");
const snapshot = document.querySelector("#snapshot");
const causes = document.querySelector("#causes");
const consequences = document.querySelector("#consequences");
const mapTitle = document.querySelector("#mapTitle");
const rippleMap = document.querySelector("#rippleMap");
const priceChart = document.querySelector("#priceChart");
const chartContext = priceChart.getContext("2d");
const chartStats = document.querySelector("#chartStats");
const chartTooltip = document.querySelector("#chartTooltip");
const marketInsightTitle = document.querySelector("#marketInsightTitle");
const aiInsightBadge = document.querySelector("#aiInsightBadge");
const insightSignalGrid = document.querySelector("#insightSignalGrid");
const analysisSummary = document.querySelector("#analysisSummary");
const insightDrivers = document.querySelector("#insightDrivers");
const topFactorLabel = document.querySelector("#topFactorLabel");
const insightQuickTake = document.querySelector("#insightQuickTake");
const relatedNewsList = document.querySelector("#relatedNewsList");
const newsFeedLabel = document.querySelector("#newsFeedLabel");
const qualityStrip = document.querySelector("#qualityStrip");
const movementTimelineList = document.querySelector("#movementTimelineList");
const movementTimelineMeta = document.querySelector("#movementTimelineMeta");
const relationshipWindow = document.querySelector("#relationshipWindow");
const relationshipSummary = document.querySelector("#relationshipSummary");
const relationshipHighlights = document.querySelector("#relationshipHighlights");
const relationshipGrid = document.querySelector("#relationshipGrid");
const relationshipNotes = document.querySelector("#relationshipNotes");
const macroSummary = document.querySelector("#macroSummary");
const macroStrip = document.querySelector("#macroStrip");
const eventSignals = document.querySelector("#eventSignals");
const rangeButtons = document.querySelectorAll(".range-button");
const modeButtons = document.querySelectorAll(".mode-button");
const chartMovement = document.querySelector("#chartMovement");
const liveStatus = document.querySelector("#liveStatus");
const streamStatus = document.querySelector("#streamStatus");
const liveMetalStrip = document.querySelector("#liveMetalStrip");
const liveCryptoStrip = document.querySelector("#liveCryptoStrip");
const liveCurrencyStrip = document.querySelector("#liveCurrencyStrip");
const providerHealthGrid = document.querySelector("#providerHealthGrid");
const marketTape = document.querySelector("#marketTape");
const activeAssetTitle = document.querySelector("#activeAssetTitle");
const activeAssetSubtitle = document.querySelector("#activeAssetSubtitle");
const comparisonStatus = document.querySelector("#comparisonStatus");
const watchlistItems = document.querySelector("#watchlistItems");
const portfolioAssetSelect = document.querySelector("#portfolioAssetSelect");
const portfolioQuantity = document.querySelector("#portfolioQuantity");
const portfolioAverageCost = document.querySelector("#portfolioAverageCost");
const portfolioSaveButton = document.querySelector("#portfolioSaveButton");
const portfolioUseSelectedButton = document.querySelector("#portfolioUseSelectedButton");
const portfolioSummaryStats = document.querySelector("#portfolioSummaryStats");
const portfolioAllocation = document.querySelector("#portfolioAllocation");
const portfolioRiskPanel = document.querySelector("#portfolioRiskPanel");
const portfolioHoldings = document.querySelector("#portfolioHoldings");
const portfolioHoldingCount = document.querySelector("#portfolioHoldingCount");
const portfolioMessage = document.querySelector("#portfolioMessage");
const alertPanel = document.querySelector("#alertPanel");
const alertPanelTitle = document.querySelector("#alertPanelTitle");
const alertPanelSubtitle = document.querySelector("#alertPanelSubtitle");
const alertAssetPill = document.querySelector("#alertAssetPill");
const alertType = document.querySelector("#alertType");
const alertPrice = document.querySelector("#alertPrice");
const alertNotification = document.querySelector("#alertNotification");
const alertRecurring = document.querySelector("#alertRecurring");
const addAlertButton = document.querySelector("#addAlertButton");
const cancelAlertEditButton = document.querySelector("#cancelAlertEditButton");
const alertItems = document.querySelector("#alertItems");
const assetAlertSummary = document.querySelector("#assetAlertSummary");
const alertFilters = document.querySelectorAll(".alert-filter");
const trackingMessage = document.querySelector("#trackingMessage");
const trustReport = document.querySelector("#trustReport");
const demoModeButton = document.querySelector("#demoModeButton");
const authStatus = document.querySelector("#authStatus");
const authSubtext = document.querySelector("#authSubtext");
const authQuickActions = document.querySelector("#authQuickActions");
const guestAccountArea = document.querySelector("#guestAccountArea");
const accountProfileButton = document.querySelector("#accountProfileButton");
const profileAvatarButton = document.querySelector("#profileAvatarButton");
const accountProfileInitials = document.querySelector("#accountProfileInitials");
const accountStatusPill = document.querySelector("#accountStatusPill");
const accountProfileMenu = document.querySelector("#accountProfileMenu");
const openLoginButton = document.querySelector("#openLoginButton");
const openRegisterButton = document.querySelector("#openRegisterButton");
const continueGuestSidebarButton = document.querySelector("#continueGuestSidebarButton");
const welcomeGate = document.querySelector("#welcomeGate");
const welcomeLoginButton = document.querySelector("#welcomeLoginButton");
const welcomeRegisterButton = document.querySelector("#welcomeRegisterButton");
const continueGuestButton = document.querySelector("#continueGuestButton");
const authModal = document.querySelector("#authModal");
const closeAuthModalButton = document.querySelector("#closeAuthModalButton");
const authModalTitle = document.querySelector("#authModalTitle");
const authModalSubtitle = document.querySelector("#authModalSubtitle");
const authNameLabel = document.querySelector("#authNameLabel");
const authSwitchText = document.querySelector("#authSwitchText");
const authSwitchButton = document.querySelector("#authSwitchButton");
const authForms = document.querySelector("#authForms");
const authName = document.querySelector("#authName");
const authEmail = document.querySelector("#authEmail");
const authPassword = document.querySelector("#authPassword");
const loginButton = document.querySelector("#loginButton");
const registerButton = document.querySelector("#registerButton");
const logoutButton = document.querySelector("#logoutButton");
const authMessage = document.querySelector("#authMessage");
const workspaceMini = document.querySelector("#workspaceMini");
const workspaceGreeting = document.querySelector("#workspaceGreeting");
const workspaceMode = document.querySelector("#workspaceMode");
const workspaceSummaryText = document.querySelector("#workspaceSummaryText");
const workspaceStats = document.querySelector("#workspaceStats");
const recentAssetsCount = document.querySelector("#recentAssetsCount");
const recentAssetsList = document.querySelector("#recentAssetsList");
const alertCenterCount = document.querySelector("#alertCenterCount");
const alertNotificationCenter = document.querySelector("#alertNotificationCenter");
const workspaceNewsCount = document.querySelector("#workspaceNewsCount");
const workspaceNewsList = document.querySelector("#workspaceNewsList");
const defaultCategoryPreference = document.querySelector("#defaultCategoryPreference");
const defaultRangePreference = document.querySelector("#defaultRangePreference");
const displayCurrencyPreference = document.querySelector("#displayCurrencyPreference");
const welcomePreference = document.querySelector("#welcomePreference");
const preferenceStatus = document.querySelector("#preferenceStatus");
const workspace = document.querySelector(".workspace");
const sideNavLinks = document.querySelectorAll(".side-nav a");
const supportedAlertTypes = [
    "PRICE_ABOVE",
    "PRICE_BELOW",
    "PERCENT_INCREASE_REACHES",
    "PERCENT_DECREASE_REACHES"
];

const initialRoute = readRouteState();

let currentCategory = initialRoute.category ?? "METAL";
let currentRange = 30;
let currentRangeKey = "30D";
let chartMode = "DAILY";
let currentAsset = null;
let currentPriceHistory = [];
let currentHourlyHistory = [];
let currentInsight = null;
let currentEventSignals = [];
let currentNewsItems = [];
let currentMovementTimeline = null;
let comparisonAsset = null;
let comparisonPriceHistory = [];
let comparisonHourlyHistory = [];
let liveMetalPrices = {};
let liveCryptoPrices = {};
let liveCurrencyPrices = {};
let livePriceTicks = {};
let liveEventSource = null;
let livePollingTimer = null;
let liveReconnectTimer = null;
let providerHealthTimer = null;
let lastPriceBySymbol = {};
let chartHoverIndex = null;
let lastChartRender = null;
let watchlist = [];
let portfolio = null;
let alerts = [];
let alertFilter = "ALL";
let editingAlertId = null;
let demoIndex = 0;
let authUser = { authenticated: false, fullName: "Guest", email: null, sessionMode: "Guest mode" };
let authMode = "login";
let userWorkspace = null;

categoryButtons.forEach((button) => {
    button.addEventListener("click", async () => {
        currentCategory = button.dataset.category;
        setActiveCategoryButton(currentCategory);
        await loadCategory(currentCategory);
    });
});

rangeButtons.forEach((button) => {
    button.addEventListener("click", async () => {
        rangeButtons.forEach((item) => item.classList.remove("active"));
        button.classList.add("active");
        currentRange = Number(button.dataset.range);
        currentRangeKey = button.dataset.rangeKey ?? `${currentRange}D`;
        chartMode = currentRangeKey === "1D" ? "HOURLY" : "DAILY";
        if (currentAsset) {
            renderCurrentChart();
            await renderInsight(currentAsset.symbol);
            await renderRelationshipFor(currentAsset.symbol);
            loadMovementTimeline(currentAsset.symbol);
        }
    });
});

modeButtons.forEach((button) => {
    button.addEventListener("click", async () => {
        modeButtons.forEach((item) => item.classList.remove("active"));
        button.classList.add("active");
        chartMode = button.dataset.mode;
        if (currentAsset) {
            renderCurrentChart();
        }
    });
});

analyzeButton.addEventListener("click", () => analyzeAsset(assetSelect.value));
compareButton.addEventListener("click", () => compareSelectedAsset());
clearCompareButton.addEventListener("click", () => clearComparison());
watchButton.addEventListener("click", () => toggleCurrentWatchlistAsset());
openLoginButton.addEventListener("click", () => openAuthModal("login"));
openRegisterButton.addEventListener("click", () => openAuthModal("register"));
continueGuestSidebarButton.addEventListener("click", () => dismissWelcomeGate());
accountProfileButton.addEventListener("click", () => toggleAccountProfileMenu());
profileAvatarButton.addEventListener("click", () => toggleAccountProfileMenu());
welcomeLoginButton.addEventListener("click", () => openAuthModal("login"));
welcomeRegisterButton.addEventListener("click", () => openAuthModal("register"));
continueGuestButton.addEventListener("click", () => dismissWelcomeGate());
closeAuthModalButton.addEventListener("click", () => closeAuthModal());
authModal.addEventListener("click", (event) => {
    if (event.target === authModal) {
        closeAuthModal();
    }
});
authSwitchButton.addEventListener("click", () => openAuthModal(authMode === "login" ? "register" : "login"));
loginButton.addEventListener("click", () => loginUser());
registerButton.addEventListener("click", () => registerUser());
logoutButton.addEventListener("click", () => logoutUser());
document.addEventListener("click", (event) => {
    if (!accountProfileMenu || accountProfileMenu.hidden) {
        return;
    }
    const clickedMenu = accountProfileMenu.contains(event.target);
    const clickedProfile = accountProfileButton.contains(event.target) || profileAvatarButton.contains(event.target);
    if (!clickedMenu && !clickedProfile) {
        closeAccountProfileMenu();
    }
});
accountProfileMenu.querySelectorAll("[data-profile-action]").forEach((button) => {
    button.addEventListener("click", () => {
        closeAccountProfileMenu();
        showAuthMessage(`${button.textContent} will be available in the next account settings phase.`);
    });
});
defaultCategoryPreference.addEventListener("change", () => saveWorkspacePreferences());
defaultRangePreference.addEventListener("change", () => saveWorkspacePreferences());
displayCurrencyPreference.addEventListener("change", () => saveWorkspacePreferences());
welcomePreference.addEventListener("change", () => saveWorkspacePreferences());
portfolioSaveButton.addEventListener("click", () => savePortfolioHolding());
portfolioUseSelectedButton.addEventListener("click", () => useSelectedAssetForPortfolio());
setAlertButton.addEventListener("click", () => openAlertPanel());
addAlertButton.addEventListener("click", () => addCurrentAlert());
cancelAlertEditButton.addEventListener("click", () => resetAlertForm());
demoModeButton.addEventListener("click", () => openDataStatusPanel());
setupSideNavigation();
alertFilters.forEach((button) => {
    button.addEventListener("click", () => {
        alertFilters.forEach((item) => item.classList.remove("active"));
        button.classList.add("active");
        alertFilter = button.dataset.alertFilter;
        renderAlerts();
    });
});
priceChart.addEventListener("mousemove", handleChartHover);
priceChart.addEventListener("mouseleave", clearChartHover);
window.addEventListener("resize", () => {
    if (currentAsset) {
        renderCurrentChart();
    }
    updateActiveSection();
});

async function start() {
    setActiveCategoryButton(currentCategory);
    await loadAuthUser();
    await loadUserWorkspace();
    applyInitialWorkspacePreferences();
    maybeShowWelcomeGate();
    await refreshStoredLiveData();
    await loadLiveMarketData();
    loadProviderHealth();
    await loadComparisonAssets();
    await loadWatchlist();
    await loadPortfolio();
    await loadAlerts();
    if (initialRoute.asset) {
        await openAsset(initialRoute.asset, initialRoute.category);
    } else {
        await loadCategory(currentCategory);
    }
    connectLivePriceStream();
    startProviderHealthMonitor();
}

async function loadAuthUser() {
    try {
        authUser = await fetchJson("/api/auth/me");
        renderAuthState();
    } catch (error) {
        showAuthMessage("Account status unavailable.", true);
    }
}

async function loginUser() {
    await submitAuth("/api/auth/login", {
        email: authEmail.value,
        password: authPassword.value
    }, "Logged in.");
}

async function registerUser() {
    await submitAuth("/api/auth/register", {
        fullName: authName.value,
        email: authEmail.value,
        password: authPassword.value
    }, "Account created.");
}

async function submitAuth(url, payload, successMessage) {
    try {
        authUser = await fetchJson(url, jsonOptions("POST", payload));
        authPassword.value = "";
        renderAuthState();
        showAuthMessage(successMessage);
        dismissWelcomeGate();
        closeAuthModal();
        await reloadPersonalData();
    } catch (error) {
        showAuthMessage(error.message, true);
    }
}

async function logoutUser() {
    try {
        closeAccountProfileMenu();
        authUser = await fetchJson("/api/auth/logout", { method: "POST" });
        renderAuthState();
        showAuthMessage("Logged out. Guest mode active.");
        await reloadPersonalData();
    } catch (error) {
        showAuthMessage(error.message, true);
    }
}

async function reloadPersonalData() {
    await Promise.all([loadWatchlist(), loadPortfolio(), loadAlerts()]);
    await loadUserWorkspace();
    renderWatchButton();
    updateAssetAlertSummary();
}

function renderAuthState() {
    const signedIn = Boolean(authUser?.authenticated);
    const initials = initialsFor(authUser?.fullName || authUser?.email || "Guest");
    authStatus.textContent = signedIn ? authUser.fullName : "Guest mode";
    authSubtext.textContent = signedIn
        ? authUser.email
        : "Sign in to save your own Watchlist, Alerts, and Portfolio.";
    if (accountStatusPill) {
        accountStatusPill.textContent = signedIn ? "Personal data active" : "Guest session";
    }
    if (accountProfileInitials) {
        accountProfileInitials.textContent = initials;
    }
    if (profileAvatarButton) {
        profileAvatarButton.textContent = initials;
        profileAvatarButton.hidden = !signedIn;
    }
    if (accountProfileButton) {
        accountProfileButton.hidden = !signedIn;
    }
    if (guestAccountArea) {
        guestAccountArea.hidden = signedIn;
    }
    authQuickActions.hidden = signedIn;
    closeAccountProfileMenu();
    if (signedIn) {
        dismissWelcomeGate();
    }
}

function initialsFor(value) {
    const clean = String(value || "").trim();
    if (!clean) {
        return "G";
    }
    const nameParts = clean.includes("@") ? clean.split("@")[0].split(/[._-]+/) : clean.split(/\s+/);
    return nameParts
        .filter(Boolean)
        .slice(0, 2)
        .map((part) => part[0].toUpperCase())
        .join("") || clean[0].toUpperCase();
}

function toggleAccountProfileMenu() {
    if (!authUser?.authenticated || !accountProfileMenu) {
        return;
    }
    const nextHidden = !accountProfileMenu.hidden;
    accountProfileMenu.hidden = nextHidden;
    const expanded = String(!nextHidden);
    accountProfileButton?.setAttribute("aria-expanded", expanded);
    profileAvatarButton?.setAttribute("aria-expanded", expanded);
}

function closeAccountProfileMenu() {
    if (!accountProfileMenu) {
        return;
    }
    accountProfileMenu.hidden = true;
    accountProfileButton?.setAttribute("aria-expanded", "false");
    profileAvatarButton?.setAttribute("aria-expanded", "false");
}

function maybeShowWelcomeGate() {
    if (!welcomeGate || authUser?.authenticated) {
        return;
    }
    if (userWorkspace?.preferences?.showWelcomeScreen === false) {
        welcomeGate.hidden = true;
        return;
    }
    const dismissed = window.localStorage.getItem("ripplelensWelcomeDismissed") === "true";
    welcomeGate.hidden = dismissed;
}

function dismissWelcomeGate() {
    if (!welcomeGate) {
        return;
    }
    welcomeGate.hidden = true;
    window.localStorage.setItem("ripplelensWelcomeDismissed", "true");
}

function openAuthModal(mode = "login") {
    authMode = mode;
    const registerMode = mode === "register";
    authModal.hidden = false;
    authModalTitle.textContent = registerMode ? "Create your account" : "Sign in";
    authModalSubtitle.textContent = registerMode
        ? "Save your own Watchlist, Alerts, and Portfolio in RippleLens."
        : "Access your saved Watchlist, Alerts, and Portfolio.";
    authName.hidden = !registerMode;
    authNameLabel.hidden = !registerMode;
    loginButton.hidden = registerMode;
    registerButton.hidden = !registerMode;
    authSwitchText.textContent = registerMode ? "Already have an account?" : "Don't have an account?";
    authSwitchButton.textContent = registerMode ? "Sign in" : "Register";
    authPassword.setAttribute("autocomplete", registerMode ? "new-password" : "current-password");
    window.setTimeout(() => (registerMode ? authName : authEmail).focus(), 0);
}

function closeAuthModal() {
    if (!authModal) {
        return;
    }
    authModal.hidden = true;
}

function showAuthMessage(message, isError = false) {
    authMessage.textContent = message;
    authMessage.classList.toggle("error", isError);
    if (message) {
        window.setTimeout(() => {
            if (authMessage.textContent === message) {
                authMessage.textContent = "";
                authMessage.classList.remove("error");
            }
        }, 4500);
    }
}

async function loadUserWorkspace() {
    try {
        userWorkspace = await fetchJson("/api/workspace");
        renderUserWorkspace();
    } catch (error) {
        userWorkspace = null;
        workspaceMini.textContent = "Workspace unavailable.";
        if (preferenceStatus) {
            preferenceStatus.textContent = "Unavailable";
        }
    }
}

function applyInitialWorkspacePreferences() {
    const preferences = userWorkspace?.preferences;
    if (!preferences) {
        return;
    }
    if (!initialRoute.category && preferences.defaultCategory) {
        currentCategory = preferences.defaultCategory;
        setActiveCategoryButton(currentCategory);
    }
    if (!initialRoute.asset && preferences.defaultRangeKey) {
        applyRangePreference(preferences.defaultRangeKey);
    }
    if (preferences.showWelcomeScreen === false) {
        window.localStorage.setItem("ripplelensWelcomeDismissed", "true");
    }
}

function applyRangePreference(rangeKey) {
    const button = Array.from(rangeButtons).find((item) => item.dataset.rangeKey === rangeKey);
    if (!button) {
        return;
    }
    rangeButtons.forEach((item) => item.classList.remove("active"));
    button.classList.add("active");
    currentRange = Number(button.dataset.range);
    currentRangeKey = button.dataset.rangeKey;
    chartMode = currentRangeKey === "1D" ? "HOURLY" : "DAILY";
}

function renderUserWorkspace() {
    if (!userWorkspace) {
        return;
    }
    const name = userWorkspace.authenticated ? userWorkspace.fullName : "Guest";
    const firstName = name?.split(" ")[0] ?? "Guest";
    workspaceGreeting.textContent = userWorkspace.authenticated ? `Welcome back, ${firstName}` : "Guest workspace";
    workspaceMode.textContent = userWorkspace.sessionMode;
    workspaceSummaryText.textContent = userWorkspace.authenticated
        ? "Your saved watchlist, alerts, portfolio exposure, and recent analysis are connected to this account."
        : "Guest mode is active. Sign in when you want this workspace saved to your account.";
    workspaceStats.innerHTML = `
        ${workspaceStat("Watchlist", userWorkspace.watchlistCount, "saved assets")}
        ${workspaceStat("Active alerts", userWorkspace.activeAlertCount, `${userWorkspace.alertCount} total`)}
        ${workspaceStat("Portfolio", formatPrice(userWorkspace.portfolioValue, "USD"), `${userWorkspace.portfolioHoldingCount} holdings`)}
        ${workspaceStat("Daily P/L", `${formatSignedPrice(userWorkspace.portfolioDailyChangeAmount, "USD")} · ${formatSigned(userWorkspace.portfolioDailyChangePercentage)}%`, "portfolio move")}
    `;
    workspaceMini.innerHTML = `
        <div class="account-mini-status">${userWorkspace.authenticated ? "Signed in workspace" : "Guest workspace"}</div>
        <div class="account-mini-grid">
            <span><strong>${userWorkspace.watchlistCount}</strong><small>Watchlist</small></span>
            <span><strong>${userWorkspace.activeAlertCount}</strong><small>Active alerts</small></span>
            <span><strong>${userWorkspace.portfolioHoldingCount}</strong><small>Holdings</small></span>
        </div>
    `;
    renderRecentAssets(userWorkspace.recentAssets ?? []);
    renderAlertNotificationCenter();
    renderWorkspaceMarketBrief();
    renderWorkspacePreferences(userWorkspace.preferences);
}

function workspaceStat(label, value, detail) {
    return `
        <div class="workspace-stat">
            <span>${label}</span>
            <strong>${value}</strong>
            <small>${detail}</small>
        </div>
    `;
}

function renderRecentAssets(recentAssets) {
    recentAssetsCount.textContent = recentAssets.length === 0
        ? "No assets yet"
        : `${recentAssets.length} recent`;
    if (recentAssets.length === 0) {
        recentAssetsList.textContent = "Open an asset to start building your workspace history.";
        return;
    }
    recentAssetsList.innerHTML = "";
    recentAssets.forEach((asset) => {
        const button = document.createElement("button");
        button.type = "button";
        button.className = "recent-asset-button";
        button.dataset.liveSymbol = asset.symbol;
        button.innerHTML = `
            <span>
                <strong>${asset.symbol} - ${asset.name}</strong>
                <small>${formatCategory(asset.category)} · ${formatDateTime(asset.viewedAt)}</small>
            </span>
            <span class="${Number(asset.dailyChangePercentage) >= 0 ? "change-up" : "change-down"}">${formatSigned(asset.dailyChangePercentage)}%</span>
        `;
        button.addEventListener("click", () => openAsset(asset.symbol, asset.category));
        recentAssetsList.appendChild(button);
    });
}

function renderAlertNotificationCenter() {
    if (!alertNotificationCenter || !alertCenterCount) {
        return;
    }

    const triggered = alerts
        .filter((alert) => alert.status === "TRIGGERED")
        .sort((a, b) => new Date(b.triggeredAt ?? b.createdAt) - new Date(a.triggeredAt ?? a.createdAt));
    const activeCount = alerts.filter((alert) => alert.status === "ACTIVE").length;
    const disabledCount = alerts.filter((alert) => alert.status === "DISABLED").length;
    alertCenterCount.textContent = `${triggered.length} triggered`;

    alertNotificationCenter.innerHTML = "";
    const summary = document.createElement("div");
    summary.className = "alert-center-summary";
    summary.innerHTML = `
        <span><strong>${triggered.length}</strong> triggered</span>
        <span><strong>${activeCount}</strong> active</span>
        <span><strong>${disabledCount}</strong> disabled</span>
    `;
    alertNotificationCenter.appendChild(summary);

    if (triggered.length === 0) {
        const empty = document.createElement("div");
        empty.className = "alert-center-empty";
        empty.textContent = activeCount > 0
            ? "No alerts have triggered yet. Active alerts will appear here when reached."
            : "Create a price alert to start receiving notifications.";
        alertNotificationCenter.appendChild(empty);
        return;
    }

    triggered.slice(0, 4).forEach((alert) => {
        const row = document.createElement("button");
        row.type = "button";
        row.className = "alert-notification-row";
        row.dataset.liveSymbol = alert.symbol;
        row.innerHTML = `
            <span>
                <strong>${alert.customName || `${alert.symbol} alert reached`}</strong>
                <small>${alert.lastNotification || alert.message}</small>
            </span>
            <em>${formatDateTime(alert.triggeredAt ?? alert.createdAt)}</em>
        `;
        row.addEventListener("click", () => openAsset(alert.symbol, alert.category));
        alertNotificationCenter.appendChild(row);
    });
}

function renderWorkspaceMarketBrief() {
    if (!workspaceNewsList || !workspaceNewsCount) {
        return;
    }

    workspaceNewsList.innerHTML = "";
    if (!currentAsset) {
        workspaceNewsCount.textContent = "Waiting";
        workspaceNewsList.textContent = "Analyze an asset to see the most important related news and signals.";
        return;
    }

    const newsItems = currentNewsItems.slice(0, 3).map((news) => ({
        label: news.impactLevel || "Related",
        title: news.title,
        detail: news.whyItMatters || news.description || "Related market context.",
        meta: `${news.source || "NewsAPI.org"} · ${formatNewsTime(news.publishedAt)}`,
        url: news.url
    }));
    const signalItems = currentEventSignals.slice(0, 2).map((signal) => ({
        label: signal.confidence ? `${signal.confidence} confidence` : "Watch signal",
        title: signal.title,
        detail: signal.description,
        meta: signal.sourceType || "RippleLens signal"
    }));
    const items = newsItems.length > 0 ? newsItems : signalItems;
    workspaceNewsCount.textContent = `${currentAsset.symbol} · ${items.length || 0} signals`;

    if (items.length === 0) {
        workspaceNewsList.textContent = "No related news or event signals are available for this asset yet.";
        return;
    }

    items.forEach((item) => {
        const row = document.createElement(item.url ? "a" : "div");
        row.className = "workspace-news-row";
        if (item.url) {
            row.href = item.url;
            row.target = "_blank";
            row.rel = "noopener noreferrer";
        }
        const badge = document.createElement("span");
        badge.textContent = item.label;
        const title = document.createElement("strong");
        title.textContent = item.title;
        const detail = document.createElement("p");
        detail.textContent = item.detail;
        const meta = document.createElement("small");
        meta.textContent = item.meta;
        row.append(badge, title, detail, meta);
        workspaceNewsList.appendChild(row);
    });
}

function renderWorkspacePreferences(preferences) {
    if (!preferences) {
        return;
    }
    defaultCategoryPreference.value = preferences.defaultCategory;
    defaultRangePreference.value = preferences.defaultRangeKey;
    displayCurrencyPreference.value = preferences.displayCurrency;
    welcomePreference.checked = Boolean(preferences.showWelcomeScreen);
}

async function saveWorkspacePreferences() {
    const payload = {
        defaultCategory: defaultCategoryPreference.value,
        defaultRangeKey: defaultRangePreference.value,
        displayCurrency: displayCurrencyPreference.value,
        showWelcomeScreen: welcomePreference.checked
    };
    setPreferenceStatus("Saving...");
    try {
        const preferences = await fetchJson("/api/workspace/preferences", jsonOptions("PATCH", payload));
        userWorkspace = {
            ...(userWorkspace ?? {}),
            preferences
        };
        syncWelcomeStorage(preferences.showWelcomeScreen);
        applyRangePreference(preferences.defaultRangeKey);
        if (currentAsset) {
            renderCurrentChart();
            renderInsight(currentAsset.symbol);
            renderRelationshipFor(currentAsset.symbol);
            loadMovementTimeline(currentAsset.symbol);
        }
        setPreferenceStatus("Saved");
    } catch (error) {
        setPreferenceStatus("Save failed", true);
        showTrackingMessage(error.message, true);
    }
}

function setPreferenceStatus(message, isError = false) {
    preferenceStatus.textContent = message;
    preferenceStatus.classList.toggle("error", isError);
}

function syncWelcomeStorage(showWelcomeScreen) {
    if (showWelcomeScreen) {
        window.localStorage.removeItem("ripplelensWelcomeDismissed");
    } else {
        window.localStorage.setItem("ripplelensWelcomeDismissed", "true");
    }
}

async function recordRecentAsset(symbol) {
    try {
        const recentAssets = await fetchJson("/api/workspace/recent", jsonOptions("POST", { symbol }));
        userWorkspace = {
            ...(userWorkspace ?? {}),
            recentAssets
        };
        renderRecentAssets(recentAssets);
    } catch (error) {
        // Recent activity should never block asset analysis.
    }
}

async function refreshStoredLiveData() {
    try {
        await fetchJson("/api/live/refresh");
    } catch (error) {
        liveStatus.textContent = "Live refresh unavailable. Using stored fallback data.";
    }
}

async function loadLiveMarketData() {
    await Promise.all([loadLiveMetalPrices(), loadLiveCryptoPrices(), loadLiveCurrencyPrices()]);
}

function startProviderHealthMonitor() {
    if (providerHealthTimer) {
        window.clearInterval(providerHealthTimer);
    }
    providerHealthTimer = window.setInterval(loadProviderHealth, 90_000);
}

async function loadProviderHealth() {
    if (!providerHealthGrid) {
        return;
    }
    try {
        const health = await fetchJson("/api/live/health");
        renderProviderHealth(health);
    } catch (error) {
        providerHealthGrid.innerHTML = `
            <div class="provider-health-card unavailable">
                <strong>Provider health unavailable</strong>
                <span>Stored fallback data remains available.</span>
            </div>
        `;
    }
}

function renderProviderHealth(health) {
    const providers = health.providers ?? [];
    const overallClass = String(health.overallStatus || "DEGRADED").toLowerCase();
    providerHealthGrid.innerHTML = `
        <div class="provider-health-summary ${overallClass}">
            <span>Provider health</span>
            <strong>${health.availableProviders}/${health.providerCount} available</strong>
            <small>${formatHealthTime(health.checkedAt)} · last refresh ${formatHealthTime(health.lastIngestionRefreshAt)}</small>
        </div>
    `;

    providers.forEach((provider) => {
        const statusClass = String(provider.status || "UNAVAILABLE").toLowerCase();
        const card = document.createElement("div");
        card.className = `provider-health-card ${statusClass}`;
        card.innerHTML = `
            <div>
                <strong>${provider.provider}</strong>
                <span>${provider.category}</span>
            </div>
            <div class="provider-health-meta">
                <b>${formatProviderStatus(provider.status)}</b>
                <small>${provider.latencyMs}ms</small>
            </div>
            <p>${provider.detail}</p>
        `;
        providerHealthGrid.appendChild(card);
    });
}

function formatProviderStatus(status) {
    return String(status || "UNKNOWN").replaceAll("_", " ");
}

function formatHealthTime(value) {
    if (!value || value === "1970-01-01T00:00:00Z") {
        return "not refreshed yet";
    }
    return new Date(value).toLocaleTimeString(undefined, {
        hour: "2-digit",
        minute: "2-digit"
    });
}

function connectLivePriceStream() {
    stopLivePolling();
    if (liveEventSource) {
        liveEventSource.close();
    }

    if (!window.EventSource) {
        startLivePolling("Browser does not support live streams. Polling prices.");
        return;
    }

    setStreamStatus("Connecting live stream...", "connecting");
    liveEventSource = new EventSource("/api/live/stream");

    liveEventSource.addEventListener("connected", () => {
        setStreamStatus("Live stream connected", "connected");
        liveStatus.textContent = "Real provider snapshots update automatically from APIs.";
    });

    liveEventSource.addEventListener("price-batch", (event) => {
        setStreamStatus("Live stream connected", "connected");
        applyLiveTicks(JSON.parse(event.data));
    });

    liveEventSource.onerror = () => {
        setStreamStatus("Live stream retrying...", "error");
        liveEventSource.close();
        liveEventSource = null;
        window.clearTimeout(liveReconnectTimer);
        liveReconnectTimer = window.setTimeout(() => {
            startLivePolling("Live stream unavailable. Polling prices automatically.");
        }, 1800);
    };
}

function startLivePolling(message) {
    setStreamStatus("Live polling active", "polling");
    liveStatus.textContent = message;
    stopLivePolling();
    pollLiveTicks();
    livePollingTimer = window.setInterval(pollLiveTicks, 5000);
}

function stopLivePolling() {
    if (livePollingTimer) {
        window.clearInterval(livePollingTimer);
        livePollingTimer = null;
    }
}

async function pollLiveTicks() {
    try {
        const ticks = await fetchJson("/api/live/ticks");
        applyLiveTicks(ticks);
        setStreamStatus("Live polling active", "polling");
    } catch (error) {
        setStreamStatus("Live updates paused", "error");
        liveStatus.textContent = "Automatic live prices are temporarily unavailable.";
    }
}

function setStreamStatus(message, state) {
    if (!streamStatus) {
        return;
    }
    streamStatus.textContent = message;
    streamStatus.classList.remove("connected", "connecting", "polling", "error");
    streamStatus.classList.add(state);
}

function applyLiveTicks(ticks) {
    if (!Array.isArray(ticks) || ticks.length === 0) {
        return;
    }

    let currentAssetChanged = false;
    let comparisonAssetChanged = false;
    const triggeredAlerts = [];

    ticks.forEach((tick) => {
        livePriceTicks[tick.symbol] = tick;
        updateLiveQuoteMaps(tick);
        updateStoredAssetState(tick);
        updateWatchlistTick(tick);
        rememberPriceDirection(tick);
        if (currentAsset?.symbol === tick.symbol) {
            currentAssetChanged = true;
            applyTickToAsset(currentAsset, currentPriceHistory, currentHourlyHistory, tick);
        }
        if (comparisonAsset?.symbol === tick.symbol) {
            comparisonAssetChanged = true;
            applyTickToAsset(comparisonAsset, comparisonPriceHistory, comparisonHourlyHistory, tick);
        }
        if (Array.isArray(tick.triggeredAlerts)) {
            triggeredAlerts.push(...tick.triggeredAlerts);
        }
    });

    renderMarketTape();
    renderWatchlist();
    refreshPortfolioFromLiveTicks();
    if (currentAssetChanged) {
        renderSnapshot(currentAsset);
        updateAssetAlertSummary();
    }
    if (currentAssetChanged || comparisonAssetChanged) {
        renderCurrentChart();
    }
    pulseLiveElements(ticks);
    if (triggeredAlerts.length > 0) {
        showLiveAlertNotifications(triggeredAlerts);
        loadAlerts();
    }
}

function updateLiveQuoteMaps(tick) {
    if (tick.category === "METAL") {
        liveMetalPrices[tick.symbol] = {
            symbol: tick.symbol,
            name: tick.assetName,
            priceUsd: tick.price,
            dailyChangePercentage: tick.dailyChangePercentage,
            unit: tick.unit,
            source: tick.source,
            direction: tick.direction
        };
        return;
    }
    if (tick.category === "CRYPTOCURRENCY") {
        liveCryptoPrices[tick.symbol] = {
            symbol: tick.symbol,
            name: tick.assetName,
            priceUsd: tick.price,
            change24hPercentage: tick.dailyChangePercentage,
            source: tick.source,
            direction: tick.direction
        };
        return;
    }
    liveCurrencyPrices[tick.symbol] = {
        symbol: tick.symbol,
        name: tick.assetName,
        usdPrice: tick.price,
        dailyChangePercentage: tick.dailyChangePercentage,
        source: tick.source,
        direction: tick.direction
    };
}

function updateStoredAssetState(tick) {
    if (currentAsset?.symbol === tick.symbol) {
        currentAsset.currentPrice = tick.price;
        currentAsset.dailyChangePercentage = tick.dailyChangePercentage;
    }
    if (comparisonAsset?.symbol === tick.symbol) {
        comparisonAsset.currentPrice = tick.price;
        comparisonAsset.dailyChangePercentage = tick.dailyChangePercentage;
    }
}

function updateWatchlistTick(tick) {
    const item = watchlist.find((asset) => asset.symbol === tick.symbol);
    if (!item) {
        return;
    }
    item.currentPrice = tick.price;
    item.dailyChangePercentage = tick.dailyChangePercentage;
}

function rememberPriceDirection(tick) {
    const previous = Number(tick.previousPrice ?? lastPriceBySymbol[tick.symbol] ?? tick.price);
    const next = Number(tick.price);
    lastPriceBySymbol[tick.symbol] = next;
    tick.direction = next > previous ? "UP" : next < previous ? "DOWN" : "FLAT";
}

function applyTickToAsset(asset, dailyHistory, hourlyHistory, tick) {
    asset.currentPrice = tick.price;
    asset.dailyChangePercentage = tick.dailyChangePercentage;
    updateLatestHistoryPoint(dailyHistory, tick, "DAILY");
    appendLiveHourlyPoint(hourlyHistory, tick);
}

function updateLatestHistoryPoint(points, tick, granularity) {
    if (!points || points.length === 0) {
        points.push(livePointFromTick(tick, granularity));
        return;
    }
    const latest = points[points.length - 1];
    latest.price = tick.price;
    latest.source = tick.source;
}

function appendLiveHourlyPoint(points, tick) {
    if (!points) {
        return;
    }
    points.push(livePointFromTick(tick, "LIVE"));
    while (points.length > 80) {
        points.shift();
    }
}

function livePointFromTick(tick, granularity) {
    const timestamp = tick.changedAt ? new Date(tick.changedAt) : new Date();
    return {
        label: timestamp.toLocaleTimeString(undefined, { hour: "2-digit", minute: "2-digit", second: "2-digit" }),
        timestamp: timestamp.toISOString(),
        price: tick.price,
        source: tick.source,
        granularity
    };
}

function pulseLiveElements(ticks) {
    const symbols = new Set(ticks.map((tick) => tick.symbol));
    symbols.forEach((symbol) => {
        const tick = livePriceTicks[symbol];
        const directionClass = tick.direction === "UP" ? "live-flash-up" : tick.direction === "DOWN" ? "live-flash-down" : "";
        if (!directionClass) {
            return;
        }
        document.querySelectorAll(`[data-live-symbol="${symbol}"]`).forEach((element) => {
            element.classList.remove("live-flash-up", "live-flash-down");
            void element.offsetWidth;
            element.classList.add(directionClass);
        });
    });
}

function showLiveAlertNotifications(triggeredAlerts) {
    const visibleAlerts = triggeredAlerts.filter((alert) => alert.notificationPreference !== "SAVE_ONLY");
    if (visibleAlerts.length === 0) {
        return;
    }
    showTrackingMessage(visibleAlerts[0].lastNotification ?? `${visibleAlerts[0].symbol} alert reached.`);
}

async function loadCategory(category, preferredSymbol = null) {
    const assets = await fetchJson(`/api/assets/category/${category}`);

    assetSelect.innerHTML = "";
    assets.forEach((asset) => {
        const option = document.createElement("option");
        option.value = asset.symbol;
        option.textContent = `${asset.symbol} - ${asset.name}`;
        assetSelect.appendChild(option);
    });

    if (assets.length > 0) {
        const selected = assets.find((asset) => asset.symbol.toUpperCase() === preferredSymbol?.toUpperCase()) ?? assets[0];
        assetSelect.value = selected.symbol;
        await analyzeAsset(selected.symbol);
    } else {
        snapshot.textContent = "No assets found for this category.";
        clearChart();
    }
}

async function loadComparisonAssets() {
    const assets = await fetchJson("/api/assets");
    compareSelect.innerHTML = "";
    portfolioAssetSelect.innerHTML = "";
    const placeholder = document.createElement("option");
    placeholder.value = "";
    placeholder.textContent = "Choose comparison asset";
    compareSelect.appendChild(placeholder);
    assets.forEach((asset) => {
        const option = document.createElement("option");
        option.value = asset.symbol;
        option.textContent = `${asset.symbol} - ${asset.name}`;
        compareSelect.appendChild(option);

        const portfolioOption = document.createElement("option");
        portfolioOption.value = asset.symbol;
        portfolioOption.textContent = `${asset.symbol} - ${asset.name}`;
        portfolioAssetSelect.appendChild(portfolioOption);
    });
    updateCompareSelectState();
}

async function analyzeAsset(symbol) {
    const [asset, priceHistory, hourlyHistory, rippleMapData, macroContext, eventSignalData, trustReportData, relationshipData] = await Promise.all([
        fetchJson(`/api/assets/${symbol}`),
        fetchJson(`/api/assets/${symbol}/prices`),
        fetchJson(`/api/assets/${symbol}/prices/hourly`),
        fetchJson(`/api/metals/${symbol}/ripple-map`),
        fetchJson(`/api/intelligence/macro/${symbol}`),
        fetchJson(`/api/intelligence/events/${symbol}`),
        fetchJson(`/api/assets/${symbol}/trust`),
        fetchJson(`/api/relationships/${symbol}?days=${relationshipWindowDays()}`)
    ]);

    currentAsset = asset;
    currentCategory = asset.category;
    setActiveCategoryButton(currentCategory);
    assetSelect.value = asset.symbol;
    currentPriceHistory = priceHistory;
    currentHourlyHistory = hourlyHistory;
    comparisonAsset = null;
    comparisonPriceHistory = [];
    comparisonHourlyHistory = [];
    currentMovementTimeline = null;
    if (comparisonStatus) {
        comparisonStatus.textContent = "Single asset view";
    }
    updateCompareSelectState();
    updateAlertPanelAsset();
    updateAssetAlertSummary();
    syncPortfolioFormToAsset(asset);
    renderSnapshot(asset);
    renderWatchButton();
    renderAlerts();
    updateRoute(asset);
    renderCurrentChart();
    currentEventSignals = eventSignalData;
    currentNewsItems = [];
    await renderInsight(symbol);
    loadRelatedNews(symbol);
    renderMacroContext(macroContext);
    renderEventSignals(eventSignalData);
    renderRippleMap(rippleMapData);
    renderTrustReport(trustReportData);
    renderRelationshipEngine(relationshipData);
    loadMovementTimeline(symbol);
    recordRecentAsset(asset.symbol);
}

async function openAsset(symbol, knownCategory = null) {
    if (knownCategory) {
        currentCategory = knownCategory;
        setActiveCategoryButton(currentCategory);
        await loadCategory(currentCategory, symbol);
        return;
    }

    const asset = await fetchJson(`/api/assets/${symbol}`);
    currentCategory = asset.category;
    setActiveCategoryButton(currentCategory);
    await loadCategory(currentCategory, asset.symbol);
}

async function openDemoAsset() {
    const demoAssets = [
        { symbol: "XAU", category: "METAL" },
        { symbol: "BTC", category: "CRYPTOCURRENCY" },
        { symbol: "USDT", category: "CRYPTOCURRENCY" },
        { symbol: "AED", category: "CURRENCY" }
    ];
    const next = demoAssets[demoIndex % demoAssets.length];
    demoIndex++;
    await openAsset(next.symbol, next.category);
}

async function openDataStatusPanel() {
    const dataStatusPanel = document.querySelector("#dataStatus");
    if (dataStatusPanel) {
        dataStatusPanel.scrollIntoView({ behavior: "smooth", block: "start" });
        dataStatusPanel.classList.add("panel-focus");
        window.setTimeout(() => dataStatusPanel.classList.remove("panel-focus"), 1800);
    }
    if (liveStatus) {
        liveStatus.textContent = "Refreshing API status...";
    }
    await loadProviderHealth();
    if (liveStatus) {
        liveStatus.textContent = "API status updated";
    }
}

async function compareSelectedAsset() {
    if (!currentAsset || !compareSelect.value || compareSelect.value === currentAsset.symbol) {
        clearComparison();
        return;
    }

    const [asset, priceHistory, hourlyHistory] = await Promise.all([
        fetchJson(`/api/assets/${compareSelect.value}`),
        fetchJson(`/api/assets/${compareSelect.value}/prices`),
        fetchJson(`/api/assets/${compareSelect.value}/prices/hourly`)
    ]);

    comparisonAsset = asset;
    comparisonPriceHistory = priceHistory;
    comparisonHourlyHistory = hourlyHistory;
    if (comparisonStatus) {
        comparisonStatus.textContent = `${currentAsset.symbol} vs ${asset.symbol}`;
    }
    renderCurrentChart();
}

function clearComparison() {
    comparisonAsset = null;
    comparisonPriceHistory = [];
    comparisonHourlyHistory = [];
    if (compareSelect) {
        compareSelect.value = "";
    }
    if (comparisonStatus) {
        comparisonStatus.textContent = "Single asset view";
    }
    if (currentAsset) {
        renderCurrentChart();
    }
}

function updateCompareSelectState() {
    if (!compareSelect) {
        return;
    }

    Array.from(compareSelect.options).forEach((option) => {
        option.disabled = Boolean(currentAsset) && option.value === currentAsset.symbol;
    });

    if (!currentAsset || compareSelect.value === currentAsset.symbol) {
        compareSelect.value = "";
    }
}

async function renderInsight(symbol) {
    const insight = await fetchJson(`/api/insights/${symbol}?days=${currentRange}`);
    currentInsight = insight;
    renderMarketInsight(insight);
    renderList(causes, insight.possibleCauses);
    renderList(consequences, insight.possibleConsequences);
}

function renderMarketInsight(insight) {
    if (!currentAsset) {
        return;
    }

    const change = Number(insight.selectedPeriodChangePercentage ?? currentAsset.dailyChangePercentage ?? 0);
    const movementWord = Math.abs(change) < 0.05 ? "Flat" : change > 0 ? "Up" : "Down";
    const sentiment = sentimentFor(currentAsset, change);
    const topFactor = insight.possibleCauses?.[0] ?? topFactorFor(currentAsset);
    const analysisType = insight.analysisType ?? "Rule-based economic explanation";
    const modelName = insight.modelName ?? "RippleLens local rules engine";
    const signalWindow = isIntradayRange() ? "1D intraday path" : `${currentRangeLabel()} window`;

    marketInsightTitle.textContent = titleForMarketInsight(currentAsset, movementWord);
    analysisSummary.textContent = insight.summary;
    aiInsightBadge.textContent = analysisType.toLowerCase().includes("ai") ? "AI-assisted" : "Explainable engine";
    aiInsightBadge.classList.toggle("ai-active", analysisType.toLowerCase().includes("ai"));
    topFactorLabel.textContent = `Top factor: ${topFactor}`;

    insightSignalGrid.innerHTML = `
        <div class="insight-signal ${sentiment.className}">
            <span>Current direction</span>
            <strong>${sentiment.label}</strong>
            <small>${sentiment.description}</small>
        </div>
        <div class="insight-signal">
            <span>${signalWindow}</span>
            <strong>${formatSigned(change)}%</strong>
            <small>${currentAsset.symbol} ${insight.movementDirection ?? "moved"} over selected data.</small>
        </div>
        <div class="insight-signal">
            <span>Confidence</span>
            <strong>${insight.confidence}</strong>
            <small>Based on stored price data and rule/AI analysis.</small>
        </div>
        <div class="insight-signal">
            <span>Top factor</span>
            <strong>${topFactor}</strong>
            <small>${factorContextFor(currentAsset)}</small>
        </div>
    `;

    insightDrivers.innerHTML = "";
    const drivers = (insight.possibleCauses ?? []).slice(0, 5);
    drivers.forEach((driver, index) => {
        const item = document.createElement("div");
        item.className = "driver-item";
        item.innerHTML = `
            <span>${String(index + 1).padStart(2, "0")}</span>
            <strong>${driver}</strong>
            <small>${driverHintFor(currentAsset, driver)}</small>
        `;
        insightDrivers.appendChild(item);
    });

    insightQuickTake.textContent = quickTakeFor(currentAsset, insight, change, sentiment.label, topFactor);

    renderRelatedNewsItems();

    qualityStrip.innerHTML = `
        <div class="quality-item"><strong>Data mode</strong><span>${insight.supportingSignals[0] ?? "Stored observations available"}</span></div>
        <div class="quality-item"><strong>Analysis type</strong><span>${analysisType}</span></div>
        <div class="quality-item"><strong>Model</strong><span>${modelName}</span></div>
        <div class="quality-item"><strong>Confidence</strong><span>${insight.confidence}</span></div>
        <div class="quality-item"><strong>Advice status</strong><span>Informational only</span></div>
    `;
}

async function loadMovementTimeline(symbol) {
    if (!movementTimelineList || !movementTimelineMeta) {
        return;
    }

    movementTimelineMeta.textContent = `${currentRangeLabel()} context`;
    movementTimelineList.innerHTML = `<div class="timeline-empty">Building timeline from price, news, and event signals...</div>`;
    try {
        const timeline = await fetchJson(`/api/intelligence/timeline/${symbol}?days=${currentRange}`);
        if (!currentAsset || currentAsset.symbol !== symbol) {
            return;
        }
        currentMovementTimeline = timeline;
        renderMovementTimeline(timeline);
    } catch (error) {
        if (!currentAsset || currentAsset.symbol !== symbol) {
            return;
        }
        currentMovementTimeline = null;
        movementTimelineMeta.textContent = "Timeline unavailable";
        movementTimelineList.innerHTML = `<div class="timeline-empty error">Timeline context is unavailable right now. ${error.message}</div>`;
    }
}

function renderMovementTimeline(timeline) {
    if (!movementTimelineList || !movementTimelineMeta) {
        return;
    }

    const items = timeline?.items ?? [];
    movementTimelineMeta.textContent = timeline
        ? `${timeline.symbol} · ${timeline.windowDays}D · ${items.length} signals`
        : "Price + news + event context";

    if (items.length === 0) {
        movementTimelineList.innerHTML = `<div class="timeline-empty">No timeline signals yet. Price context will appear once history or news is available.</div>`;
        return;
    }

    movementTimelineList.innerHTML = "";
    items.forEach((item) => {
        movementTimelineList.appendChild(createTimelineItem(item));
    });

    if (timeline?.disclaimer) {
        const disclaimer = document.createElement("div");
        disclaimer.className = "timeline-disclaimer";
        disclaimer.textContent = timeline.disclaimer;
        movementTimelineList.appendChild(disclaimer);
    }
}

function createTimelineItem(item) {
    const row = document.createElement("div");
    row.className = `movement-timeline-item ${timelineTypeClass(item.type)}`;

    const marker = document.createElement("div");
    marker.className = "timeline-marker";
    marker.textContent = timelineTypeLabel(item.type);

    const content = document.createElement("div");
    content.className = "timeline-content";

    const header = document.createElement("div");
    header.className = "timeline-item-header";

    const label = document.createElement("span");
    label.textContent = item.label || item.impactLevel || item.type || "Signal";

    const time = document.createElement("small");
    time.textContent = formatTimelineTime(item.timestamp);
    header.append(label, time);

    const title = item.url ? document.createElement("a") : document.createElement("strong");
    if (item.url) {
        title.href = item.url;
        title.target = "_blank";
        title.rel = "noopener noreferrer";
    }
    title.textContent = item.title || "Timeline signal";

    const description = document.createElement("p");
    description.textContent = item.description || "No description available.";

    const meta = document.createElement("div");
    meta.className = "timeline-meta-row";
    [item.impactLevel, item.priceEffect, item.source].filter(Boolean).forEach((value) => {
        const chip = document.createElement("span");
        chip.textContent = value;
        meta.appendChild(chip);
    });

    content.append(header, title, description);
    if (meta.children.length > 0) {
        content.appendChild(meta);
    }
    row.append(marker, content);
    return row;
}

function timelineTypeClass(type) {
    const normalized = String(type || "").toLowerCase();
    if (normalized === "news") {
        return "timeline-news";
    }
    if (normalized === "event") {
        return "timeline-event";
    }
    return "timeline-price";
}

function timelineTypeLabel(type) {
    const normalized = String(type || "").toUpperCase();
    if (normalized === "NEWS") {
        return "N";
    }
    if (normalized === "EVENT") {
        return "E";
    }
    return "P";
}

function formatTimelineTime(value) {
    if (!value) {
        return "watching now";
    }
    const parsed = value.length === 10 ? new Date(`${value}T00:00:00`) : new Date(value);
    if (Number.isNaN(parsed.getTime())) {
        return value;
    }
    return parsed.toLocaleString(undefined, {
        month: "short",
        day: "numeric",
        year: "numeric",
        hour: value.includes("T") ? "2-digit" : undefined,
        minute: value.includes("T") ? "2-digit" : undefined
    });
}

async function renderRelationshipFor(symbol) {
    try {
        const data = await fetchJson(`/api/relationships/${symbol}?days=${relationshipWindowDays()}`);
        renderRelationshipEngine(data);
    } catch (error) {
        relationshipSummary.textContent = "Relationship signals are unavailable right now.";
        relationshipGrid.innerHTML = "";
        relationshipHighlights.innerHTML = "";
        relationshipNotes.innerHTML = `<span>${error.message}</span>`;
    }
}

function renderRelationshipEngine(data) {
    relationshipWindow.textContent = `${data.windowDays}D correlation`;
    relationshipSummary.textContent = data.summary;

    const relationships = data.relationships ?? [];
    const strongest = relationships[0];
    const unusualCount = relationships.filter((item) => item.unusualBehavior).length;
    relationshipHighlights.innerHTML = `
        ${relationshipHighlight("Top relationship", data.topRelationship, "Strongest aligned return pattern")}
        ${relationshipHighlight("Observations", `${data.observationsUsed}`, "Daily return points analyzed")}
        ${relationshipHighlight("Unusual signals", `${unusualCount}`, unusualCount > 0 ? "Pairs breaking recent behavior" : "No major breaks detected")}
        ${relationshipHighlight("Method", "Return correlation", "Compares percentage moves")}
    `;

    relationshipGrid.innerHTML = "";
    if (relationships.length === 0) {
        relationshipGrid.innerHTML = `<div class="relationship-empty">Not enough aligned history yet. Add more stored observations or use a shorter window.</div>`;
    } else {
        relationships.forEach((item) => {
            relationshipGrid.appendChild(createRelationshipCard(item, strongest?.symbol === item.symbol));
        });
    }

    relationshipNotes.innerHTML = `
        ${(data.insights ?? []).map((item) => `<span>${item}</span>`).join("")}
        <small>${data.disclaimer}</small>
    `;
}

function relationshipWindowDays() {
    return Math.max(7, currentRange);
}

function relationshipHighlight(label, value, detail) {
    return `
        <div class="relationship-highlight">
            <span>${label}</span>
            <strong>${value}</strong>
            <small>${detail}</small>
        </div>
    `;
}

function createRelationshipCard(item, isTop) {
    const card = document.createElement("div");
    const positive = item.correlation >= 0;
    card.className = `relationship-card ${positive ? "positive-relationship" : "negative-relationship"} ${item.unusualBehavior ? "unusual-relationship" : ""}`;
    const scoreWidth = Math.min(100, Math.abs(item.correlation) * 100);
    card.innerHTML = `
        <div class="relationship-card-header">
            <div>
                <span>${isTop ? "Strongest link" : item.relationshipType}</span>
                <strong>${item.symbol} - ${item.name}</strong>
            </div>
            <div class="correlation-score">${formatCorrelation(item.correlation)}</div>
        </div>
        <div class="correlation-meter" aria-label="Correlation strength">
            <div style="width: ${scoreWidth}%"></div>
        </div>
        <div class="relationship-meta">
            <span>${item.strength} ${item.direction}</span>
            <span>${item.pairedObservations} paired days</span>
            <span>${item.category.toLowerCase().replace("_", " ")}</span>
        </div>
        <div class="relationship-moves">
            <span>${currentAsset.symbol}: ${formatSigned(item.selectedAssetMovePercentage)}%</span>
            <span>${item.symbol}: ${formatSigned(item.relatedAssetMovePercentage)}%</span>
        </div>
        <p>${item.explanation}</p>
        ${item.unusualBehavior ? `<div class="relationship-alert">Unusual behavior detected</div>` : ""}
    `;
    return card;
}

function formatCorrelation(value) {
    const number = Number(value);
    const sign = number > 0 ? "+" : "";
    return `${sign}${number.toFixed(2)}`;
}

async function loadRelatedNews(symbol) {
    try {
        const newsItems = await fetchJson(`/api/intelligence/news/${symbol}`);
        if (!currentAsset || currentAsset.symbol !== symbol) {
            return;
        }
        currentNewsItems = Array.isArray(newsItems) ? newsItems : [];
        renderRelatedNewsItems();
    } catch (error) {
        if (!currentAsset || currentAsset.symbol !== symbol) {
            return;
        }
        currentNewsItems = [];
        renderRelatedNewsItems();
    }
}

function renderRelatedNewsItems() {
    if (!currentAsset) {
        return;
    }
    relatedNewsList.innerHTML = "";
    if (currentNewsItems.length > 0) {
        if (newsFeedLabel) {
            newsFeedLabel.textContent = "NewsAPI.org feed · top 5";
        }
        currentNewsItems.slice(0, 5).forEach((news, index) => {
            relatedNewsList.appendChild(createNewsItem(news, index));
        });
        renderWorkspaceMarketBrief();
        return;
    }

    if (newsFeedLabel) {
        newsFeedLabel.textContent = "News unavailable · showing watch signals";
    }
    const relatedSignals = currentEventSignals.length > 0 ? currentEventSignals.slice(0, 5) : fallbackRelatedSignals(currentAsset);
    relatedSignals.forEach((signal, index) => {
        relatedNewsList.appendChild(createSignalNewsItem(signal, index));
    });
    renderWorkspaceMarketBrief();
}

function createNewsItem(news, index) {
    const row = document.createElement("div");
    row.className = "related-news-item";
    const marker = document.createElement("div");
    marker.className = "news-marker";
    marker.textContent = String(index + 1);

    const content = document.createElement("div");
    const badges = document.createElement("div");
    badges.className = "news-badge-row";
    badges.append(createNewsBadge(news.impactLevel || "Medium impact", impactClassFor(news.impactLevel)));
    badges.append(createNewsBadge(news.priceEffect || "Unclear effect", effectClassFor(news.priceEffect)));

    const link = document.createElement("a");
    link.href = news.url;
    link.target = "_blank";
    link.rel = "noopener noreferrer";
    link.textContent = news.title;
    const meta = document.createElement("small");
    meta.textContent = `${news.source || "NewsAPI.org"} · ${formatNewsTime(news.publishedAt)}${news.sourceCountry ? ` · ${news.sourceCountry}` : ""}`;
    content.append(badges, link);
    if (news.description) {
        const description = document.createElement("p");
        description.textContent = news.description;
        content.append(description);
    }
    if (news.whyItMatters) {
        const reason = document.createElement("p");
        reason.className = "news-why";
        reason.textContent = `Why it matters: ${news.whyItMatters}`;
        content.append(reason);
    }
    content.append(meta);
    row.append(marker, content);
    return row;
}

function createNewsBadge(label, className) {
    const badge = document.createElement("span");
    badge.className = `news-badge ${className}`;
    badge.textContent = label;
    return badge;
}

function impactClassFor(level) {
    const normalized = String(level || "").toLowerCase();
    if (normalized.includes("high")) {
        return "impact-high";
    }
    if (normalized.includes("low")) {
        return "impact-low";
    }
    return "impact-medium";
}

function effectClassFor(effect) {
    const normalized = String(effect || "").toLowerCase();
    if (normalized.includes("support")) {
        return "effect-support";
    }
    if (normalized.includes("pressure")) {
        return "effect-pressure";
    }
    return "effect-unclear";
}

function createSignalNewsItem(signal, index) {
    const row = document.createElement("div");
    row.className = "related-news-item";
    row.innerHTML = `
        <div class="news-marker">${index + 1}</div>
        <div>
            <strong>${signal.title}</strong>
            <p>${signal.description}</p>
            <small>${signal.confidence ?? "medium"} confidence · ${signal.sourceType ?? "RippleLens signal"} · fallback signal</small>
        </div>
    `;
    return row;
}

function formatNewsTime(value) {
    if (!value) {
        return "recent";
    }
    const normalized = value.includes("T")
        ? value
        : `${value.slice(0, 4)}-${value.slice(4, 6)}-${value.slice(6, 8)}T${value.slice(8, 10)}:${value.slice(10, 12)}:${value.slice(12, 14)}Z`;
    const parsed = new Date(normalized);
    if (Number.isNaN(parsed.getTime())) {
        return "recent";
    }
    return parsed.toLocaleString(undefined, {
        month: "short",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit"
    });
}

function renderLegacySignalPreview() {
    const relatedSignals = currentEventSignals.length > 0 ? currentEventSignals.slice(0, 4) : fallbackRelatedSignals(currentAsset);
    relatedSignals.forEach((signal, index) => {
        const row = document.createElement("div");
        row.className = "related-news-item";
        row.innerHTML = `
            <div class="news-marker">${index + 1}</div>
            <div>
                <strong>${signal.title}</strong>
                <p>${signal.description}</p>
                <small>${signal.confidence ?? "medium"} confidence · ${signal.sourceType ?? "RippleLens signal"} · now</small>
            </div>
        `;
        relatedNewsList.appendChild(row);
    });
}

async function fetchJson(url, options = {}) {
    const response = await fetch(url, options);
    if (!response.ok) {
        let message = `Request failed: ${url}`;
        try {
            const error = await response.json();
            message = error.detail ?? error.message ?? message;
        } catch (parseError) {
            message = response.statusText || message;
        }
        throw new Error(message);
    }
    if (response.status === 204) {
        return null;
    }
    const text = await response.text();
    if (!text) {
        return null;
    }
    return JSON.parse(text);
}

function jsonOptions(method, body) {
    return {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
    };
}

function showTrackingMessage(message, isError = false) {
    trackingMessage.textContent = message;
    trackingMessage.classList.toggle("error", isError);
    if (message) {
        window.setTimeout(() => {
            if (trackingMessage.textContent === message) {
                trackingMessage.textContent = "";
                trackingMessage.classList.remove("error");
            }
        }, 4500);
    }
}

function readRouteState() {
    const params = new URLSearchParams(window.location.search);
    return {
        asset: params.get("asset"),
        category: params.get("category")
    };
}

function updateRoute(asset) {
    const params = new URLSearchParams(window.location.search);
    params.set("asset", asset.symbol);
    params.set("category", asset.category);
    window.history.replaceState({}, "", `${window.location.pathname}?${params.toString()}`);
}

function setActiveCategoryButton(category) {
    categoryButtons.forEach((item) => {
        item.classList.toggle("active", item.dataset.category === category);
    });
}

function setupSideNavigation() {
    sideNavLinks.forEach((link) => {
        link.addEventListener("click", (event) => {
            event.preventDefault();
            const target = document.querySelector(link.getAttribute("href"));
            if (!target || !workspace) {
                return;
            }

            setActiveNavLink(link);
            scrollWorkspaceToSection(target, "smooth");
            window.history.replaceState(null, "", `${window.location.pathname}${window.location.search}${link.getAttribute("href")}`);
        });
    });

    if (workspace) {
        let ticking = false;
        workspace.addEventListener("scroll", () => {
            if (ticking) {
                return;
            }
            window.requestAnimationFrame(() => {
                updateActiveSection();
                ticking = false;
            });
            ticking = true;
        });
    }

    const initialSection = window.location.hash ? document.querySelector(window.location.hash) : null;
    if (initialSection) {
        scrollWorkspaceToSection(initialSection, "auto");
    } else {
        updateActiveSection();
    }
}

function setActiveNavLink(activeLink) {
    sideNavLinks.forEach((link) => {
        const isActive = link === activeLink;
        link.classList.toggle("active", isActive);
        if (isActive) {
            link.setAttribute("aria-current", "page");
        } else {
            link.removeAttribute("aria-current");
        }
    });
}

function scrollWorkspaceToSection(section, behavior) {
    const workspaceTop = workspace.getBoundingClientRect().top;
    const targetTop = section.getBoundingClientRect().top - workspaceTop + workspace.scrollTop - fixedTopbarOffset();
    workspace.scrollTo({ top: Math.max(targetTop, 0), behavior });
}

function fixedTopbarOffset() {
    const topbar = document.querySelector(".topbar");
    return topbar ? topbar.getBoundingClientRect().height + 16 : 10;
}

function updateActiveSection() {
    if (!workspace || sideNavLinks.length === 0) {
        return;
    }

    const workspaceTop = workspace.getBoundingClientRect().top;
    const activationOffset = fixedTopbarOffset() + 32;
    let activeLink = sideNavLinks[0];

    sideNavLinks.forEach((link) => {
        const target = document.querySelector(link.getAttribute("href"));
        if (!target) {
            return;
        }
        const targetTop = target.getBoundingClientRect().top - workspaceTop;
        if (targetTop <= activationOffset) {
            activeLink = link;
        }
    });

    const nearBottom = workspace.scrollTop + workspace.clientHeight >= workspace.scrollHeight - 8;
    if (nearBottom) {
        activeLink = sideNavLinks[sideNavLinks.length - 1];
    }

    setActiveNavLink(activeLink);
}

async function loadWatchlist() {
    try {
        watchlist = await fetchJson("/api/watchlist");
        renderWatchlist();
        renderWatchButton();
    } catch (error) {
        showTrackingMessage(error.message, true);
    }
}

async function toggleCurrentWatchlistAsset() {
    if (!currentAsset) {
        return;
    }

    const existing = watchlist.find((asset) => asset.symbol === currentAsset.symbol);
    try {
        if (existing) {
            await fetchJson(`/api/watchlist/${existing.id}`, { method: "DELETE" });
            showTrackingMessage(`${currentAsset.symbol} removed from Watchlist.`);
        } else {
            await fetchJson("/api/watchlist", jsonOptions("POST", { symbol: currentAsset.symbol }));
            showTrackingMessage(`${currentAsset.symbol} added to Watchlist.`);
        }
        await loadWatchlist();
        await loadUserWorkspace();
    } catch (error) {
        showTrackingMessage(error.message, true);
    }
}

function renderWatchButton() {
    if (!currentAsset) {
        watchButton.textContent = "Save";
        return;
    }
    const saved = watchlist.some((asset) => asset.symbol === currentAsset.symbol);
    watchButton.textContent = saved ? "Saved" : "Save";
}

function renderWatchlist() {
    if (watchlist.length === 0) {
        watchlistItems.textContent = "No tracked assets yet.";
        return;
    }

    watchlistItems.innerHTML = "";
    watchlist.forEach((asset) => {
        const row = document.createElement("div");
        row.className = "watchlist-row";
        row.dataset.liveSymbol = asset.symbol;
        row.innerHTML = `
            <button type="button" class="watchlist-button watchlist-main">
                <strong>${asset.symbol} - ${asset.assetName}</strong>
                <small>${formatCategory(asset.category)} · ${formatPrice(asset.currentPrice, asset.quoteCurrency)} · ${formatSigned(asset.dailyChangePercentage)}%</small>
            </button>
            <div class="watchlist-actions">
                <button type="button" class="mini-button danger" data-remove-watch="${asset.id}">Remove</button>
            </div>
        `;
        row.querySelector(".watchlist-main").addEventListener("click", () => openAsset(asset.symbol, asset.category));
        row.querySelector("[data-remove-watch]").addEventListener("click", async () => {
            try {
                await fetchJson(`/api/watchlist/${asset.id}`, { method: "DELETE" });
                showTrackingMessage(`${asset.symbol} removed from Watchlist.`);
                await loadWatchlist();
                await loadUserWorkspace();
            } catch (error) {
                showTrackingMessage(error.message, true);
            }
        });
        watchlistItems.appendChild(row);
    });
}

async function loadPortfolio(silent = false) {
    try {
        portfolio = await fetchJson("/api/portfolio");
        renderPortfolio();
    } catch (error) {
        if (!silent) {
            showPortfolioMessage(error.message, true);
        }
    }
}

function refreshPortfolioFromLiveTicks() {
    if (!portfolio || portfolio.holdingCount === 0) {
        return;
    }
    loadPortfolio(true);
}

function syncPortfolioFormToAsset(asset) {
    if (!asset || !portfolioAssetSelect) {
        return;
    }
    portfolioAssetSelect.value = asset.symbol;
}

function useSelectedAssetForPortfolio() {
    if (!currentAsset) {
        showPortfolioMessage("Choose an asset first.", true);
        return;
    }
    portfolioAssetSelect.value = currentAsset.symbol;
    if (!portfolioAverageCost.value) {
        portfolioAverageCost.value = latestDisplayPrice(currentAsset).toFixed(4);
    }
    portfolioQuantity.focus();
}

async function savePortfolioHolding() {
    const symbol = portfolioAssetSelect.value;
    const quantity = Number(portfolioQuantity.value);
    const averageCost = portfolioAverageCost.value ? Number(portfolioAverageCost.value) : null;

    if (!symbol) {
        showPortfolioMessage("Choose an asset to track.", true);
        return;
    }
    if (!Number.isFinite(quantity) || quantity <= 0) {
        showPortfolioMessage("Quantity must be greater than zero.", true);
        return;
    }
    if (averageCost !== null && (!Number.isFinite(averageCost) || averageCost < 0)) {
        showPortfolioMessage("Average cost cannot be negative.", true);
        return;
    }

    try {
        portfolio = await fetchJson("/api/portfolio/holdings", jsonOptions("POST", {
            symbol,
            quantity,
            averageCost
        }));
        renderPortfolio();
        await loadUserWorkspace();
        showPortfolioMessage(`${symbol} holding saved.`);
    } catch (error) {
        showPortfolioMessage(error.message, true);
    }
}

function renderPortfolio() {
    const emptyPortfolio = {
        totalValue: 0,
        totalCost: 0,
        unrealizedProfitLoss: 0,
        unrealizedProfitLossPercentage: 0,
        dailyChangeAmount: 0,
        dailyChangePercentage: 0,
        holdingCount: 0,
        largestPosition: null,
        largestPositionPercentage: 0,
        dominantCategory: "none",
        exposureSummary: "No tracked exposure yet.",
        riskLevel: "No holdings",
        riskWarning: "Add holdings to see concentration, category exposure, and unrealized performance.",
        allocations: [],
        holdings: []
    };
    const data = portfolio ?? emptyPortfolio;
    const profitClass = Number(data.unrealizedProfitLoss) >= 0 ? "change-up" : "change-down";
    const dailyClass = Number(data.dailyChangeAmount) >= 0 ? "change-up" : "change-down";

    portfolioSummaryStats.innerHTML = `
        ${portfolioStat("Total Value", formatPrice(data.totalValue, "USD"))}
        ${portfolioStat("Cost Basis", formatPrice(data.totalCost, "USD"))}
        ${portfolioStat("Unrealized P/L", `${formatSignedPrice(data.unrealizedProfitLoss, "USD")} · ${formatSigned(data.unrealizedProfitLossPercentage)}%`, profitClass)}
        ${portfolioStat("Daily Move", `${formatSignedPrice(data.dailyChangeAmount, "USD")} · ${formatSigned(data.dailyChangePercentage)}%`, dailyClass)}
        ${portfolioStat("Largest Position", data.largestPosition ? `${data.largestPosition.symbol} · ${Number(data.largestPositionPercentage).toFixed(2)}%` : "None")}
        ${portfolioStat("Risk", data.riskLevel ?? "No holdings", riskClassFor(data.riskLevel))}
    `;

    renderPortfolioAllocation(data.allocations ?? []);
    renderPortfolioRisk(data);
    renderPortfolioHoldings(data);
}

function portfolioStat(label, value, className = "") {
    return `
        <div class="portfolio-stat">
            <span>${label}</span>
            <strong class="${className}">${value}</strong>
        </div>
    `;
}

function renderPortfolioAllocation(allocations) {
    if (!allocations || allocations.length === 0) {
        portfolioAllocation.innerHTML = `
            <div class="allocation-row">
                <div class="allocation-header">
                    <strong>No allocation yet</strong>
                    <span>0.00%</span>
                </div>
                <div class="allocation-bar"><div class="allocation-fill" style="width: 0%"></div></div>
                <span>Add a holding to see exposure by metals, crypto, and currencies.</span>
            </div>
        `;
        return;
    }

    portfolioAllocation.innerHTML = "";
    allocations.forEach((allocation) => {
        const percent = Math.max(0, Math.min(100, Number(allocation.allocationPercentage)));
        const row = document.createElement("div");
        row.className = "allocation-row";
        row.innerHTML = `
            <div class="allocation-header">
                <strong>${formatCategory(allocation.category)}</strong>
                <span>${percent.toFixed(2)}%</span>
            </div>
            <div class="allocation-bar"><div class="allocation-fill" style="width: ${percent}%"></div></div>
            <span>${formatPrice(allocation.value, "USD")} exposure</span>
        `;
        portfolioAllocation.appendChild(row);
    });
}

function renderPortfolioRisk(data) {
    if (!portfolioRiskPanel) {
        return;
    }
    const riskClass = riskClassFor(data.riskLevel);
    portfolioRiskPanel.className = `portfolio-risk-panel ${riskClass}`;
    portfolioRiskPanel.innerHTML = `
        <div>
            <span>Risk concentration</span>
            <strong>${data.riskLevel ?? "No holdings"}</strong>
        </div>
        <p>${data.riskWarning ?? "Add holdings to see risk context."}</p>
        <small>Exposure: ${data.exposureSummary ?? "No tracked exposure yet."}</small>
    `;
}

function riskClassFor(riskLevel) {
    const normalized = String(riskLevel || "").toLowerCase();
    if (normalized.includes("high")) {
        return "risk-high";
    }
    if (normalized.includes("moderate")) {
        return "risk-medium";
    }
    if (normalized.includes("diversified")) {
        return "risk-low";
    }
    return "";
}

function renderPortfolioHoldings(data) {
    const holdings = data.holdings ?? [];
    portfolioHoldingCount.textContent = holdings.length === 0
        ? "No holdings yet"
        : `${holdings.length} holding${holdings.length === 1 ? "" : "s"}`;

    if (holdings.length === 0) {
        portfolioHoldings.textContent = "No portfolio holdings yet.";
        return;
    }

    portfolioHoldings.innerHTML = "";
    holdings.forEach((holding) => {
        const profitClass = Number(holding.unrealizedProfitLoss) >= 0 ? "change-up" : "change-down";
        const dailyClass = Number(holding.dailyChangeAmount) >= 0 ? "change-up" : "change-down";
        const row = document.createElement("div");
        row.className = "portfolio-row";
        row.dataset.liveSymbol = holding.symbol;
        row.dataset.portfolioId = holding.id;
        row.innerHTML = `
            <div>
                <strong>${holding.symbol} - ${holding.assetName}</strong>
                <small>${formatCategory(holding.category)} · ${holding.allocationPercentage}% allocation</small>
            </div>
            <label>
                <small>Quantity</small>
                <input type="number" step="0.00000001" value="${formatQuantityInput(holding.quantity)}" data-portfolio-quantity>
            </label>
            <label>
                <small>Avg cost</small>
                <input type="number" step="0.0001" value="${Number(holding.averageCost)}" data-portfolio-cost>
            </label>
            <div>
                <small>Value</small>
                <span>${formatPrice(holding.currentValue, "USD")}</span>
            </div>
            <div>
                <small>Unrealized P/L</small>
                <span class="${profitClass}">${formatSignedPrice(holding.unrealizedProfitLoss, "USD")} · ${formatSigned(holding.unrealizedProfitLossPercentage)}%</span>
            </div>
            <div>
                <small>Daily</small>
                <span class="${dailyClass}">${formatSignedPrice(holding.dailyChangeAmount, "USD")} · ${formatSigned(holding.dailyChangePercentage)}%</span>
            </div>
            <div class="portfolio-actions">
                <button type="button" class="mini-button success" data-open-portfolio="${holding.symbol}:${holding.category}">Open</button>
                <button type="button" class="mini-button" data-update-portfolio="${holding.id}">Update</button>
                <button type="button" class="mini-button danger" data-delete-portfolio="${holding.id}">Delete</button>
            </div>
        `;
        row.querySelector("[data-open-portfolio]").addEventListener("click", () => openAsset(holding.symbol, holding.category));
        row.querySelector("[data-update-portfolio]").addEventListener("click", () => updatePortfolioHolding(holding.id));
        row.querySelector("[data-delete-portfolio]").addEventListener("click", () => deletePortfolioHolding(holding.id, holding.symbol));
        portfolioHoldings.appendChild(row);
    });
}

async function updatePortfolioHolding(id) {
    const row = portfolioHoldings.querySelector(`[data-portfolio-id="${id}"]`);
    if (!row) {
        return;
    }
    const quantity = Number(row.querySelector("[data-portfolio-quantity]").value);
    const averageCost = Number(row.querySelector("[data-portfolio-cost]").value);
    if (!Number.isFinite(quantity) || quantity <= 0) {
        showPortfolioMessage("Quantity must be greater than zero.", true);
        return;
    }
    if (!Number.isFinite(averageCost) || averageCost < 0) {
        showPortfolioMessage("Average cost cannot be negative.", true);
        return;
    }

    try {
        portfolio = await fetchJson(`/api/portfolio/holdings/${id}`, jsonOptions("PATCH", { quantity, averageCost }));
        renderPortfolio();
        await loadUserWorkspace();
        showPortfolioMessage("Holding updated.");
    } catch (error) {
        showPortfolioMessage(error.message, true);
    }
}

async function deletePortfolioHolding(id, symbol) {
    try {
        portfolio = await fetchJson(`/api/portfolio/holdings/${id}`, { method: "DELETE" });
        renderPortfolio();
        await loadUserWorkspace();
        showPortfolioMessage(`${symbol} removed from portfolio.`);
    } catch (error) {
        showPortfolioMessage(error.message, true);
    }
}

function showPortfolioMessage(message, isError = false) {
    portfolioMessage.textContent = message;
    portfolioMessage.classList.toggle("error", isError);
    if (message) {
        window.setTimeout(() => {
            if (portfolioMessage.textContent === message) {
                portfolioMessage.textContent = "";
                portfolioMessage.classList.remove("error");
            }
        }, 4500);
    }
}

async function loadAlerts() {
    try {
        alerts = await fetchJson("/api/alerts");
        renderAlerts();
        renderAlertNotificationCenter();
        updateAssetAlertSummary();
        showTriggeredNotifications();
    } catch (error) {
        showTrackingMessage(error.message, true);
    }
}

function openAlertPanel(alert = null) {
    if (!currentAsset && !alert) {
        showTrackingMessage("Choose an asset before creating an alert.", true);
        return;
    }

    alertPanel.hidden = false;
    if (alert) {
        editingAlertId = alert.id;
        alertPanelTitle.textContent = "Edit Price Alert";
        alertPanelSubtitle.textContent = `Update the alert for ${alert.symbol} - ${alert.assetName}.`;
        alertType.value = supportedAlertTypes.includes(alert.alertType) ? alert.alertType : "PRICE_ABOVE";
        alertPrice.value = alert.targetValue;
        alertNotification.value = alert.notificationPreference ?? "IN_APP";
        alertRecurring.value = String(Boolean(alert.recurring));
        addAlertButton.textContent = "Update Alert";
        cancelAlertEditButton.hidden = false;
    } else {
        resetAlertForm(false);
    }
    updateAlertPanelAsset(alert);
    scrollWorkspaceToSection(alertPanel, "smooth");
}

function resetAlertForm(keepPanelOpen = true) {
    editingAlertId = null;
    alertPanelTitle.textContent = "Set Price Alert";
    alertPanelSubtitle.textContent = "Create an alert for the selected asset.";
    alertType.value = "PRICE_ABOVE";
    alertPrice.value = "";
    alertNotification.value = "IN_APP";
    alertRecurring.value = "false";
    addAlertButton.textContent = "Create Alert";
    cancelAlertEditButton.hidden = true;
    updateAlertPanelAsset();
    if (!keepPanelOpen) {
        showTrackingMessage("");
    }
}

function updateAlertPanelAsset(alert = null) {
    const assetSymbol = alert?.symbol ?? currentAsset?.symbol;
    const assetName = alert?.assetName ?? currentAsset?.name;
    if (!assetSymbol) {
        alertAssetPill.textContent = "No asset selected";
        return;
    }
    alertAssetPill.textContent = `${assetSymbol} - ${assetName}`;
}

async function addCurrentAlert() {
    if (!currentAsset || !alertPrice.value) {
        showTrackingMessage("Choose an asset and enter a target value.", true);
        return;
    }

    const targetValue = Number(alertPrice.value);
    if (!Number.isFinite(targetValue) || targetValue <= 0) {
        showTrackingMessage("Target value must be greater than zero.", true);
        return;
    }

    try {
        const payload = {
            alertType: alertType.value,
            targetValue,
            notificationPreference: alertNotification.value,
            recurring: alertRecurring.value === "true",
            customName: null
        };
        if (editingAlertId) {
            await fetchJson(`/api/alerts/${editingAlertId}`, jsonOptions("PATCH", payload));
            showTrackingMessage(`Alert updated for ${currentAsset.symbol}.`);
        } else {
            await fetchJson("/api/alerts", jsonOptions("POST", { ...payload, symbol: currentAsset.symbol }));
            showTrackingMessage(`Alert created for ${currentAsset.symbol}.`);
        }
        resetAlertForm();
        await loadAlerts();
        await loadUserWorkspace();
    } catch (error) {
        showTrackingMessage(error.message, true);
    }
}

function renderAlerts() {
    const visibleAlerts = alerts.filter((alert) => {
        if (alertFilter === "CURRENT") {
            return currentAsset && alert.symbol === currentAsset.symbol;
        }
        if (alertFilter === "ALL") {
            return true;
        }
        return alert.status === alertFilter;
    });

    if (visibleAlerts.length === 0) {
        alertItems.textContent = alertFilter === "CURRENT" ? "No alerts for this asset yet." : "No alerts match this filter.";
        renderAlertNotificationCenter();
        return;
    }

    alertItems.innerHTML = "";
    visibleAlerts.forEach((alert) => {
        const row = document.createElement("div");
        row.className = `alert-row ${alert.status.toLowerCase()}`;
        row.innerHTML = `
            <div>
                <strong>${alert.customName || `${alert.symbol} alert`}</strong>
                <span class="alert-status ${alert.status.toLowerCase()}">${alert.status}</span>
            </div>
            <div class="alert-meta">
                <small>${alert.symbol} - ${alert.assetName}</small>
                <small>${formatAlertType(alert.alertType)} · target ${formatAlertTarget(alert)}</small>
                <small>Created ${formatDateTime(alert.createdAt)}${alert.triggeredAt ? ` · Triggered ${formatDateTime(alert.triggeredAt)}` : ""}</small>
                <small>${alert.recurring ? "Recurring" : "One-time"} · ${formatNotificationPreference(alert.notificationPreference)} · ${alert.enabled ? "Enabled" : "Disabled"}</small>
            </div>
            <div class="alert-actions">
                <button type="button" class="mini-button success" data-open-alert="${alert.symbol}:${alert.category}">Open</button>
                <button type="button" class="mini-button" data-toggle-alert="${alert.id}">${alert.enabled ? "Disable" : "Enable"}</button>
                <button type="button" class="mini-button" data-edit-alert="${alert.id}">Edit</button>
                <button type="button" class="mini-button danger" data-delete-alert="${alert.id}">Delete</button>
            </div>
        `;
        row.querySelector("[data-open-alert]").addEventListener("click", () => openAsset(alert.symbol, alert.category));
        row.querySelector("[data-toggle-alert]").addEventListener("click", () => toggleAlertEnabled(alert));
        row.querySelector("[data-edit-alert]").addEventListener("click", () => editAlert(alert));
        row.querySelector("[data-delete-alert]").addEventListener("click", () => deleteAlert(alert));
        alertItems.appendChild(row);
    });
    renderAlertNotificationCenter();
}

function updateAssetAlertSummary() {
    if (!assetAlertSummary) {
        return;
    }

    if (!currentAsset) {
        assetAlertSummary.textContent = "Select an asset";
        return;
    }

    const currentAlerts = alerts.filter((alert) => alert.symbol === currentAsset.symbol);
    if (currentAlerts.length === 0) {
        assetAlertSummary.textContent = `No alerts for ${currentAsset.symbol}`;
        return;
    }

    const activeCount = currentAlerts.filter((alert) => alert.status === "ACTIVE").length;
    const triggeredCount = currentAlerts.filter((alert) => alert.status === "TRIGGERED").length;
    const disabledCount = currentAlerts.filter((alert) => alert.status === "DISABLED").length;
    assetAlertSummary.textContent = `${currentAlerts.length} saved · ${activeCount} active · ${triggeredCount} triggered · ${disabledCount} disabled`;
}

async function toggleAlertEnabled(alert) {
    try {
        await fetchJson(`/api/alerts/${alert.id}/enabled?enabled=${!alert.enabled}`, { method: "PATCH" });
        showTrackingMessage(`Alert ${alert.enabled ? "disabled" : "enabled"}.`);
        await loadAlerts();
        await loadUserWorkspace();
    } catch (error) {
        showTrackingMessage(error.message, true);
    }
}

async function deleteAlert(alert) {
    try {
        await fetchJson(`/api/alerts/${alert.id}`, { method: "DELETE" });
        showTrackingMessage("Alert deleted.");
        await loadAlerts();
        await loadUserWorkspace();
    } catch (error) {
        showTrackingMessage(error.message, true);
    }
}

async function editAlert(alert) {
    if (currentAsset?.symbol !== alert.symbol) {
        await openAsset(alert.symbol, alert.category);
    }
    openAlertPanel(alert);
}

function showTriggeredNotifications() {
    const triggered = alerts.filter((alert) => alert.status === "TRIGGERED" && alert.lastNotification);
    if (triggered.length > 0) {
        showTrackingMessage(triggered[0].lastNotification);
    }
}

function formatAlertType(type) {
    return {
        PRICE_ABOVE: "Price goes above",
        PRICE_BELOW: "Price goes below",
        PERCENT_INCREASE_REACHES: "Percentage increases by",
        PERCENT_DECREASE_REACHES: "Percentage decreases by",
        DAILY_PERCENT_CHANGE_REACHES: "Daily percentage reaches",
        PRICE_TARGET_REACHED: "Price reaches target",
        PRICE_CHANGE_AMOUNT_REACHES: "Price changes by amount"
    }[type] ?? type.toLowerCase().replaceAll("_", " ");
}

function formatAlertTarget(alert) {
    if (alert.alertType.includes("PERCENT")) {
        return `${Number(alert.targetValue).toFixed(2)}%`;
    }
    return formatPrice(alert.targetValue, "USD");
}

function formatNotificationPreference(preference) {
    return {
        IN_APP: "In-app",
        EMAIL: "Email",
        BOTH: "In-app + email",
        SAVE_ONLY: "Saved only"
    }[preference] ?? "In-app";
}

function formatDateTime(value) {
    if (!value) {
        return "N/A";
    }
    return new Date(value).toLocaleString(undefined, {
        month: "short",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit"
    });
}

function formatSigned(value) {
    const number = Number(value);
    if (!Number.isFinite(number)) {
        return "+0.00";
    }
    return `${number >= 0 ? "+" : ""}${number.toFixed(2)}`;
}

function formatSignedPrice(value, currency) {
    const number = Number(value);
    if (!Number.isFinite(number)) {
        return `+${formatPrice(0, currency)}`;
    }
    const sign = number >= 0 ? "+" : "-";
    return `${sign}${formatPrice(Math.abs(number), currency)}`;
}

function formatQuantityInput(value) {
    const number = Number(value);
    if (!Number.isFinite(number)) {
        return "";
    }
    return Number(number.toFixed(8)).toString();
}

function latestDisplayPrice(asset) {
    const liveMetal = liveMetalPrices[asset.symbol];
    const liveCrypto = liveCryptoPrices[asset.symbol];
    const liveCurrency = liveCurrencyPrices[asset.symbol];
    return Number(liveMetal?.priceUsd ?? liveCrypto?.priceUsd ?? liveCurrency?.usdPrice ?? asset.currentPrice);
}

function sourceLabelFor(asset, liveQuote) {
    if (!liveQuote) {
        return "Stored fallback data";
    }

    const rawSource = String(liveQuote.source || "Stored/API snapshot");
    const normalizedSource = rawSource.toLowerCase();
    if (normalizedSource.includes("simulation") || normalizedSource.includes("simulated")) {
        return "Temporary fallback simulation";
    }
    if (normalizedSource.includes("stored/api")) {
        return "Latest stored/API snapshot";
    }
    if (normalizedSource === "demo" || normalizedSource.includes("demo/")) {
        return "Stored fallback seed";
    }

    if (asset.category === "METAL") {
        return `${rawSource} metals API`;
    }
    if (asset.category === "CURRENCY") {
        return `${rawSource} FX API`;
    }
    if (asset.category === "CRYPTOCURRENCY") {
        return `${rawSource} crypto market API`;
    }
    return rawSource;
}

function renderSnapshot(asset) {
    const liveMetal = liveMetalPrices[asset.symbol];
    const liveCrypto = liveCryptoPrices[asset.symbol];
    const liveCurrency = liveCurrencyPrices[asset.symbol];
    const liveQuote = liveMetal ?? liveCrypto ?? liveCurrency;
    const displayPrice = liveMetal?.priceUsd ?? liveCrypto?.priceUsd ?? liveCurrency?.usdPrice ?? asset.currentPrice;
    const displayChange = liveMetal?.dailyChangePercentage ?? liveCrypto?.change24hPercentage ?? liveCurrency?.dailyChangePercentage ?? asset.dailyChangePercentage;
    const source = sourceLabelFor(asset, liveQuote);
    const change = Number(displayChange);
    const changeClass = change >= 0 ? "change-up" : "change-down";
    const sign = change >= 0 ? "+" : "";

    activeAssetTitle.textContent = `${asset.symbol} - ${asset.name}`;
    activeAssetSubtitle.textContent = `${formatCategory(asset.category)} · ${source} · ${sign}${change.toFixed(2)}% daily move`;

    snapshot.innerHTML = `
        <div class="price" data-live-symbol="${asset.symbol}"><span>${formatPrice(displayPrice, asset.quoteCurrency)}</span><small>/ ${asset.unit}</small></div>
        <div class="meta-grid">
            <div class="metric">
                <span class="metric-label">Category</span>
                <span class="metric-value">${formatCategory(asset.category)}</span>
            </div>
            <div class="metric">
                <span class="metric-label">Daily Move</span>
                <span class="metric-value ${changeClass}">${sign}${change.toFixed(2)}%</span>
            </div>
            <div class="metric">
                <span class="metric-label">Data Source</span>
                <span class="metric-value">${source}</span>
            </div>
            <div class="metric">
                <span class="metric-label">Symbol</span>
                <span class="metric-value">${asset.symbol}</span>
            </div>
        </div>
        <p class="muted">${asset.description}</p>
    `;
}

function refreshCurrentSnapshot() {
    if (currentAsset) {
        renderSnapshot(currentAsset);
    }
}

function renderTrustReport(data) {
    const sourceList = data.sources.length === 0 ? "No source labels yet" : data.sources.join(", ");
    const latestObservation = data.latestObservationDate ? formatDateOnly(data.latestObservationDate) : "N/A";
    const scoreClass = trustScoreClass(data.dataTrustScore);
    trustReport.innerHTML = `
        <div class="trust-hero">
            <div>
                <span class="eyebrow">Reliability Lens</span>
                <h3>${data.symbol} data confidence</h3>
                <p>${data.coverageSummary ?? "Coverage summary unavailable."}</p>
            </div>
            <div class="trust-score-ring ${scoreClass}">
                <strong>${data.dataTrustScore}</strong>
                <span>/100</span>
            </div>
        </div>
        <div class="trust-grid">
            <div class="trust-item">
                <span>Confidence</span>
                <strong>${data.confidenceLevel}</strong>
                <small>${data.confidenceLevel} confidence</small>
            </div>
            <div class="trust-item">
                <span>Data Source</span>
                <strong>${data.latestObservationSource}</strong>
                <small>${sourceList}</small>
            </div>
            <div class="trust-item">
                <span>Latest observation</span>
                <strong>${latestObservation}</strong>
                <small>${data.freshnessStatus ?? "Freshness unknown"}</small>
            </div>
            <div class="trust-item">
                <span>Source Type</span>
                <strong>${data.sourceMode}</strong>
                <small>${data.usesRealApi ? "Live/API-backed source" : "Fallback data active"}</small>
            </div>
        </div>
        <div class="trust-source-strip">
            <span><strong>${data.realApiObservations ?? 0}</strong> API-backed</span>
            <span><strong>${data.fallbackObservations ?? 0}</strong> fallback</span>
            <span><strong>${data.modeledObservations ?? 0}</strong> modeled</span>
            <span><strong>${data.storedObservations ?? 0}</strong> total points</span>
        </div>
        <p class="trust-note">${data.recommendation}</p>
        <ul class="trust-list">${data.caveats.map((item) => `<li>${item}</li>`).join("")}</ul>
    `;
}

function formatDateOnly(value) {
    if (!value) {
        return "N/A";
    }
    const [year, month, day] = String(value).split("-").map(Number);
    const date = year && month && day ? new Date(year, month - 1, day) : new Date(value);
    return date.toLocaleDateString(undefined, {
        month: "short",
        day: "numeric",
        year: "numeric"
    });
}

function trustScoreClass(score) {
    const value = Number(score);
    if (value >= 80) {
        return "high";
    }
    if (value >= 55) {
        return "medium";
    }
    return "low";
}

async function loadLiveCryptoPrices() {
    try {
        const prices = await fetchJson("/api/live/cryptos");
        liveCryptoPrices = Object.fromEntries(prices.map((item) => [item.symbol, item]));
        renderLiveCryptoStrip(prices);
        renderMarketTape();
        refreshCurrentSnapshot();
    } catch (error) {
        liveStatus.textContent = "Live crypto data unavailable. Using stored fallback prices.";
        liveCryptoStrip.innerHTML = "";
        renderMarketTape();
        refreshCurrentSnapshot();
    }
}

async function loadLiveMetalPrices() {
    try {
        const prices = await fetchJson("/api/live/metals");
        liveMetalPrices = Object.fromEntries(prices.map((item) => [item.symbol, item]));
        renderLiveMetalStrip(prices);
        renderMarketTape();
        refreshCurrentSnapshot();
    } catch (error) {
        liveMetalStrip.innerHTML = "";
        renderMarketTape();
        refreshCurrentSnapshot();
    }
}

async function loadLiveCurrencyPrices() {
    try {
        const prices = await fetchJson("/api/live/currencies");
        liveCurrencyPrices = Object.fromEntries(prices.map((item) => [item.symbol, item]));
        renderLiveCurrencyStrip(prices);
        renderMarketTape();
        refreshCurrentSnapshot();
    } catch (error) {
        liveCurrencyStrip.innerHTML = "";
        renderMarketTape();
        refreshCurrentSnapshot();
    }
}

function renderMarketTape() {
    const metalItems = Object.values(liveMetalPrices).map((item) => ({
        symbol: item.symbol,
        name: item.name,
        price: item.priceUsd,
        change: item.dailyChangePercentage ?? 0,
        source: item.source
    }));
    const cryptoItems = Object.values(liveCryptoPrices).map((item) => ({
        symbol: item.symbol,
        name: item.name,
        price: item.priceUsd,
        change: item.change24hPercentage ?? 0,
        source: item.source
    }));
    const currencyItems = Object.values(liveCurrencyPrices).map((item) => ({
        symbol: item.symbol,
        name: item.name,
        price: item.usdPrice,
        change: item.dailyChangePercentage ?? 0,
        source: item.source
    }));

    const items = [...metalItems, ...cryptoItems, ...currencyItems].slice(0, 16);
    if (items.length === 0) {
        marketTape.textContent = "Live market tape unavailable. Stored fallback data is still available.";
        return;
    }

    marketTape.innerHTML = "";
    marketTape.style.setProperty("--ticker-duration", `${Math.max(30, items.length * 2.9)}s`);

    const track = document.createElement("div");
    track.className = "market-tape-track";
    track.appendChild(createTickerGroup(items, false));
    track.appendChild(createTickerGroup(items, true));
    marketTape.appendChild(track);
}

function createTickerGroup(items, hidden) {
    const group = document.createElement("div");
    group.className = "ticker-group";
    if (hidden) {
        group.setAttribute("aria-hidden", "true");
    }

    items.forEach((item) => {
        group.appendChild(createTickerPill(item));
    });
    return group;
}

function createTickerPill(item) {
    const change = Number(item.change);
    const sign = change >= 0 ? "+" : "";
    const pill = document.createElement("div");
    pill.className = `ticker-pill ${change >= 0 ? "positive" : "negative"}`;
    pill.dataset.liveSymbol = item.symbol;
    pill.title = `${item.name || item.symbol} · ${item.source || "Latest source"}`;
    pill.innerHTML = `
        <strong>${item.symbol}</strong>
        <span class="ticker-price">${formatPrice(item.price, "USD")}</span>
        <span class="ticker-change ${change >= 0 ? "change-up" : "change-down"}">${sign}${change.toFixed(2)}%</span>
    `;
    return pill;
}

function renderLiveCryptoStrip(prices) {
    if (prices.length === 0) {
        liveStatus.textContent = "Live crypto data unavailable. Using stored fallback prices.";
        liveCryptoStrip.innerHTML = "";
        return;
    }

    liveStatus.textContent = "Connected to Gold API, Binance Spot, CoinGecko, and Frankfurter FX";
    liveCryptoStrip.innerHTML = "";
    prices.forEach((item) => {
        const change = Number(item.change24hPercentage);
        const sign = change >= 0 ? "+" : "";
        const card = document.createElement("div");
        card.className = "live-card";
        card.dataset.liveSymbol = item.symbol;
        card.innerHTML = `
            <strong>${item.symbol} - ${item.name}</strong>
            <div>${formatPrice(item.priceUsd, "USD")}</div>
            <div class="${change >= 0 ? "change-up" : "change-down"}">${sign}${change.toFixed(2)}% 24h · ${item.source}</div>
        `;
        liveCryptoStrip.appendChild(card);
    });
}

function renderLiveMetalStrip(prices) {
    if (prices.length === 0) {
        liveMetalStrip.innerHTML = "";
        return;
    }

    liveStatus.textContent = "Connected to Gold API, Binance Spot, CoinGecko, and Frankfurter FX";
    liveMetalStrip.innerHTML = "";
    prices.forEach((item) => {
        const card = document.createElement("div");
        card.className = "live-card metal-live-card";
        card.dataset.liveSymbol = item.symbol;
        card.innerHTML = `
            <strong>${item.symbol} - ${item.name}</strong>
            <div>${formatPrice(item.priceUsd, "USD")}</div>
            <div class="muted">per ${item.unit} · ${item.source}</div>
        `;
        liveMetalStrip.appendChild(card);
    });
}

function renderLiveCurrencyStrip(prices) {
    liveCurrencyStrip.innerHTML = "";
    prices.forEach((item) => {
        const card = document.createElement("div");
        card.className = "live-card";
        card.dataset.liveSymbol = item.symbol;
        card.innerHTML = `
            <strong>${item.symbol} - ${item.name}</strong>
            <div>${formatPrice(item.usdPrice, "USD")}</div>
            <div class="muted">USD value · ${item.source}</div>
        `;
        liveCurrencyStrip.appendChild(card);
    });
}

function renderMacroContext(data) {
    macroSummary.textContent = data.summary;
    macroStrip.innerHTML = "";

    data.indicators.forEach((indicator) => {
        const card = document.createElement("div");
        card.className = "macro-card";
        card.innerHTML = `
            <strong>${indicator.name}</strong>
            <div class="macro-value">${Number(indicator.value).toFixed(2)}${indicator.unit}</div>
            <div class="muted">${indicator.country} · ${indicator.year} · ${indicator.source}</div>
            <p>${indicator.interpretation}</p>
        `;
        macroStrip.appendChild(card);
    });

    const channelCard = document.createElement("div");
    channelCard.className = "macro-card channel-card";
    channelCard.innerHTML = `
        <strong>Pressure channels</strong>
        <ul>${data.pressureChannels.map((item) => `<li>${item}</li>`).join("")}</ul>
    `;
    macroStrip.appendChild(channelCard);
}

function renderEventSignals(signals) {
    eventSignals.innerHTML = "";
    signals.forEach((signal) => {
        const card = document.createElement("div");
        card.className = "event-card";
        card.innerHTML = `
            <strong>${signal.title}</strong>
            <p>${signal.description}</p>
            <div class="muted">Confidence: ${signal.confidence} · ${signal.sourceType}</div>
            <div class="event-tags">${signal.affectedAreas.map((item) => `<span>${item}</span>`).join("")}</div>
        `;
        eventSignals.appendChild(card);
    });
}

function titleForMarketInsight(asset, movementWord) {
    if (asset.category === "METAL") {
        return `Why Is ${asset.name} ${movementWord} Today?`;
    }
    if (asset.category === "CRYPTOCURRENCY") {
        return `What's Driving ${asset.name} Today?`;
    }
    return `What's Moving ${asset.name} Today?`;
}

function sentimentFor(asset, change) {
    const absolute = Math.abs(change);
    if (absolute < 0.1) {
        return {
            label: "Almost unchanged",
            className: "neutral",
            description: "The price barely moved in the selected window."
        };
    }
    if (change > 0) {
        return {
            label: asset.category === "CURRENCY" ? "Getting stronger" : "Going up",
            className: "positive",
            description: "The recent move is upward, but this is not a forecast."
        };
    }
    return {
        label: asset.category === "CURRENCY" ? "Getting weaker" : "Going down",
        className: "negative",
        description: "The recent move is downward, but this is not a forecast."
    };
}

function topFactorFor(asset) {
    if (asset.category === "METAL") {
        return "USD and real-rate pressure";
    }
    if (asset.category === "CRYPTOCURRENCY") {
        return "risk appetite and liquidity";
    }
    return "central-bank expectations";
}

function factorContextFor(asset) {
    if (asset.category === "METAL") {
        return "Metals often react to rates, inflation, supply, and dollar moves.";
    }
    if (asset.category === "CRYPTOCURRENCY") {
        return "Crypto often reacts to liquidity, regulation, flows, and sentiment.";
    }
    return "Currencies often react to rates, inflation, growth, and capital flows.";
}

function driverHintFor(asset, driver) {
    const text = driver.toLowerCase();
    if (text.includes("dollar") || text.includes("usd")) {
        return "A stronger dollar can pressure USD-priced assets; a weaker dollar can support them.";
    }
    if (text.includes("rate") || text.includes("central") || text.includes("bank")) {
        return "Rate expectations can change opportunity cost, currency strength, and risk appetite.";
    }
    if (text.includes("supply") || text.includes("mining") || text.includes("inventory")) {
        return "Supply stress can make prices more sensitive to new disruptions.";
    }
    if (text.includes("liquidity") || text.includes("risk")) {
        return "Liquidity and risk appetite can quickly change demand for speculative assets.";
    }
    if (text.includes("demand") || text.includes("industrial")) {
        return "Demand shifts can affect both spot prices and future expectations.";
    }
    if (asset.category === "METAL") {
        return "A metal-specific driver that can affect supply, demand, or hedging demand.";
    }
    if (asset.category === "CRYPTOCURRENCY") {
        return "A crypto-specific driver linked to flows, usage, or market confidence.";
    }
    return "A macro driver that can influence exchange-rate expectations.";
}

function quickTakeFor(asset, insight, change, sentiment, topFactor) {
    const direction = change >= 0 ? "supporting" : "pressuring";
    const movement = Math.abs(change).toFixed(2);
    return `${asset.symbol} is ${sentiment.toLowerCase()} over the selected window (${movement}%), with ${topFactor} currently the clearest ${direction} force. Treat this as context, not a trade signal.`;
}

function fallbackRelatedSignals(asset) {
    return [
        {
            title: `${asset.symbol} movement monitor`,
            description: `RippleLens is tracking ${asset.name} through price changes, macro context, and category-specific risk signals.`,
            confidence: "low",
            sourceType: "RippleLens generated signal"
        }
    ];
}

function formatPrice(value, currency) {
    if (currency === "USD") {
        return `$${Number(value).toLocaleString(undefined, { maximumFractionDigits: 2 })}`;
    }
    return `${Number(value).toLocaleString(undefined, { maximumFractionDigits: 4 })} ${currency}`;
}

function formatCategory(category) {
    return category.toLowerCase().replace("_", " ");
}

function renderChart(points, asset) {
    clearChart();
    chartMovement.innerHTML = "";
    chartStats.innerHTML = "";
    chartTooltip.hidden = true;

    if (points.length === 0) {
        drawEmptyChart("No price history available.");
        return;
    }

    const visiblePoints = getVisibleChartPoints(points);
    const values = visiblePoints.map((point) => Number(point.price));
    const size = prepareChartCanvas();
    const model = buildChartModel(values, size);
    const color = chartColor(asset.category);

    renderChartStats([{ asset, points: visiblePoints }], false);
    drawTerminalFrame(model, asset.quoteCurrency);
    drawTerminalHeader(model, chartTitle(asset), asset, values);
    drawAreaSeries(model, values, color);
    drawHighLowMarkers(model, values, asset.quoteCurrency);
    drawLatestPriceTag(model, values[values.length - 1], asset.quoteCurrency, color);

    lastChartRender = {
        model,
        series: [{ asset, points: visiblePoints, values, color, quoteCurrency: asset.quoteCurrency }]
    };
    drawHoverLayer();
    renderMovementStrip([{ asset, points: visiblePoints, hourlyPoints: currentHourlyHistory }]);
}

function renderCurrentChart() {
    const primaryPoints = getActiveChartPoints(currentPriceHistory, currentHourlyHistory);
    if (comparisonAsset && comparisonPriceHistory.length > 0) {
        const secondaryPoints = getActiveChartPoints(comparisonPriceHistory, comparisonHourlyHistory);
        renderComparisonChart(primaryPoints, currentAsset, secondaryPoints, comparisonAsset);
        return;
    }
    renderChart(primaryPoints, currentAsset);
}

function renderComparisonChart(primaryPoints, primaryAsset, secondaryPoints, secondaryAsset) {
    clearChart();
    chartMovement.innerHTML = "";
    chartStats.innerHTML = "";
    chartTooltip.hidden = true;

    const primary = getVisibleChartPoints(primaryPoints);
    const secondary = getVisibleChartPoints(secondaryPoints);
    const length = Math.min(primary.length, secondary.length);
    if (length < 2) {
        drawEmptyChart("Not enough overlapping history for comparison.");
        return;
    }

    const alignedPrimary = primary.slice(-length);
    const alignedSecondary = secondary.slice(-length);
    const primaryValues = normalizeSeries(alignedPrimary.map((point) => Number(point.price)));
    const secondaryValues = normalizeSeries(alignedSecondary.map((point) => Number(point.price)));
    const allValues = [...primaryValues, ...secondaryValues];
    const size = prepareChartCanvas();
    const model = buildChartModel(allValues, size);
    const primaryColor = chartColor(primaryAsset.category);
    const secondaryColor = chartColor(secondaryAsset.category, true);

    renderChartStats([
        { asset: primaryAsset, points: alignedPrimary },
        { asset: secondaryAsset, points: alignedSecondary }
    ], true);
    drawTerminalFrame(model, "%");
    drawTerminalHeader(model, `${primaryAsset.symbol} vs ${secondaryAsset.symbol} normalized comparison`, primaryAsset, primaryValues);
    drawLineSeries(model, primaryValues, primaryColor, false);
    drawLineSeries(model, secondaryValues, secondaryColor, true);
    drawComparisonLegend(model, primaryAsset, secondaryAsset, primaryColor, secondaryColor);

    lastChartRender = {
        model,
        normalized: true,
        series: [
            { asset: primaryAsset, points: alignedPrimary, values: primaryValues, color: primaryColor, quoteCurrency: primaryAsset.quoteCurrency },
            { asset: secondaryAsset, points: alignedSecondary, values: secondaryValues, color: secondaryColor, quoteCurrency: secondaryAsset.quoteCurrency }
        ]
    };
    drawHoverLayer();
    renderMovementStrip([
        { asset: primaryAsset, points: alignedPrimary, hourlyPoints: currentHourlyHistory },
        { asset: secondaryAsset, points: alignedSecondary, hourlyPoints: comparisonHourlyHistory }
    ]);
}

function chartTitle(asset) {
    if (isIntradayRange()) {
        return `${asset.name} 1-day price path`;
    }
    return `${asset.name} ${currentRangeLabel().toLowerCase()} price history`;
}

function getActiveChartPoints(dailyPoints, hourlyPoints) {
    if (isIntradayRange() && hourlyPoints && hourlyPoints.length > 0) {
        return hourlyPoints;
    }
    return dailyPoints ?? [];
}

function getVisibleChartPoints(points) {
    if (!points || points.length === 0) {
        return [];
    }
    if (isIntradayRange()) {
        return points;
    }
    return points.slice(-currentRange);
}

function isIntradayRange() {
    return currentRangeKey === "1D";
}

function currentRangeLabel() {
    return currentRangeKey === "1D" ? "1D" : currentRangeKey;
}

function renderMovementStrip(items) {
    chartMovement.innerHTML = "";
    items.forEach(({ asset, points, hourlyPoints }) => {
        if (!points || points.length < 2) {
            return;
        }
        const first = Number(points[0].price);
        const last = Number(points[points.length - 1].price);
        chartMovement.appendChild(createMovementCard(
            asset,
            isIntradayRange() ? "1D PATH" : currentRangeLabel(),
            first,
            last,
            asset.symbol,
            false
        ));

        renderQuickMovementCards(asset, hourlyPoints ?? points);
    });
}

function renderQuickMovementCards(asset, hourlyPoints) {
    if (!hourlyPoints || hourlyPoints.length < 2) {
        return;
    }

    const latest = Number(hourlyPoints[hourlyPoints.length - 1].price);
    const previousHour = Number(hourlyPoints[hourlyPoints.length - 2].price);
    const firstHour = Number(hourlyPoints[0].price);
    const oneHourDelta = latest - previousHour;
    const oneMinuteDelta = oneHourDelta / 60;
    const previousMinuteEstimate = latest - oneMinuteDelta;

    chartMovement.appendChild(createMovementCardFromDelta(
        asset,
        "1M EST",
        oneMinuteDelta,
        previousMinuteEstimate,
        asset.symbol,
        true
    ));
    chartMovement.appendChild(createMovementCard(
        asset,
        "1H",
        previousHour,
        latest,
        asset.symbol,
        true
    ));
    chartMovement.appendChild(createMovementCard(
        asset,
        "24H",
        firstHour,
        latest,
        asset.symbol,
        true
    ));
}

function createMovementCard(asset, title, first, last, detail, compact) {
    return createMovementCardFromDelta(asset, title, last - first, first, detail, compact);
}

function createMovementCardFromDelta(asset, title, delta, basePrice, detail, compact) {
    const percent = basePrice === 0 ? 0 : (delta / basePrice) * 100;
    const sign = delta >= 0 ? "+" : "";
    const card = document.createElement("div");
    card.className = `movement-card ${compact ? "movement-quick" : ""} ${delta >= 0 ? "movement-up" : "movement-down"}`;
    card.innerHTML = `
        <strong>${title}</strong>
        <span>${sign}${percent.toFixed(2)}%</span>
        <small>${sign}${formatPrice(delta, asset.quoteCurrency)} · ${detail}</small>
    `;
    return card;
}

function normalizeSeries(values) {
    const first = values[0] || 1;
    return values.map((value) => (value / first) * 100);
}

function prepareChartCanvas() {
    const rect = priceChart.getBoundingClientRect();
    const width = Math.max(320, Math.floor(rect.width || 960));
    const height = Math.max(320, Math.floor(rect.height || 420));
    const pixelRatio = window.devicePixelRatio || 1;
    const targetWidth = Math.floor(width * pixelRatio);
    const targetHeight = Math.floor(height * pixelRatio);

    if (priceChart.width !== targetWidth || priceChart.height !== targetHeight) {
        priceChart.width = targetWidth;
        priceChart.height = targetHeight;
    }

    chartContext.setTransform(pixelRatio, 0, 0, pixelRatio, 0, 0);
    return { width, height };
}

function buildChartModel(values, size) {
    const rawMin = Math.min(...values);
    const rawMax = Math.max(...values);
    const rawRange = rawMax - rawMin || Math.max(Math.abs(rawMax) * 0.01, 1);
    const domainPadding = rawRange * 0.10;
    const min = rawMin - domainPadding;
    const max = rawMax + domainPadding;
    const range = max - min || 1;
    return {
        size,
        min,
        max,
        range,
        rawMin,
        rawMax,
        bounds: {
            left: 28,
            right: size.width - 78,
            top: 58,
            bottom: size.height - 46
        }
    };
}

function renderChartStats(items, comparisonMode) {
    chartStats.innerHTML = "";
    if (items.length === 0) {
        return;
    }

    if (!comparisonMode) {
        const { asset, points } = items[0];
        const values = points.map((point) => Number(point.price));
        const first = values[0];
        const last = values[values.length - 1];
        const high = Math.max(...values);
        const low = Math.min(...values);
        const move = last - first;
        const percent = first === 0 ? 0 : (move / first) * 100;
        addChartStat("Price", formatPrice(last, asset.quoteCurrency), "Latest visible point");
        addChartStat("Period Move", `${move >= 0 ? "+" : ""}${percent.toFixed(2)}%`, `${move >= 0 ? "+" : ""}${formatPrice(move, asset.quoteCurrency)}`, move >= 0);
        addChartStat("High", formatPrice(high, asset.quoteCurrency), isIntradayRange() ? "1D high" : `${currentRangeLabel()} high`);
        addChartStat("Low", formatPrice(low, asset.quoteCurrency), isIntradayRange() ? "1D low" : `${currentRangeLabel()} low`);
        return;
    }

    items.forEach(({ asset, points }) => {
        const first = Number(points[0].price);
        const last = Number(points[points.length - 1].price);
        const move = last - first;
        const percent = first === 0 ? 0 : (move / first) * 100;
        addChartStat(`${asset.symbol} Move`, `${move >= 0 ? "+" : ""}${percent.toFixed(2)}%`, `${move >= 0 ? "+" : ""}${formatPrice(move, asset.quoteCurrency)}`, move >= 0);
    });
    addChartStat("Scale", "100 Base", "Both assets start at 100");
    addChartStat("Window", currentRangeLabel(), isIntradayRange() ? "Intraday path" : "Stored daily observations");
}

function addChartStat(label, value, detail, isPositive = null) {
    const card = document.createElement("div");
    card.className = "chart-stat";
    if (isPositive !== null) {
        card.classList.add(isPositive ? "positive-stat" : "negative-stat");
    }
    card.innerHTML = `
        <span>${label}</span>
        <strong>${value}</strong>
        <small>${detail}</small>
    `;
    chartStats.appendChild(card);
}

function drawTerminalFrame(model, unit) {
    const { width, height } = model.size;
    const { left, right, top, bottom } = model.bounds;
    chartContext.fillStyle = "#05070a";
    chartContext.fillRect(0, 0, width, height);

    chartContext.fillStyle = "#080d12";
    chartContext.fillRect(left, top, right - left, bottom - top);

    chartContext.strokeStyle = "#222a34";
    chartContext.lineWidth = 1;
    chartContext.strokeRect(left, top, right - left, bottom - top);

    chartContext.font = "12px system-ui";
    chartContext.textAlign = "right";
    chartContext.textBaseline = "middle";

    for (let i = 0; i <= 6; i++) {
        const y = top + (i / 6) * (bottom - top);
        const value = model.max - (i / 6) * model.range;
        chartContext.strokeStyle = i === 6 ? "#303945" : "#1d2530";
        chartContext.beginPath();
        chartContext.moveTo(left, y);
        chartContext.lineTo(right, y);
        chartContext.stroke();
        chartContext.fillStyle = "#a7b0bd";
        chartContext.fillText(formatAxisValue(value, unit), width - 16, y);
    }

    chartContext.textAlign = "center";
    chartContext.textBaseline = "top";
    for (let i = 0; i <= 8; i++) {
        const x = left + (i / 8) * (right - left);
        chartContext.strokeStyle = "#171f29";
        chartContext.beginPath();
        chartContext.moveTo(x, top);
        chartContext.lineTo(x, bottom);
        chartContext.stroke();
    }
}

function drawTerminalHeader(model, title, asset, values) {
    const { left, top } = model.bounds;
    const first = values[0];
    const last = values[values.length - 1];
    const percent = first === 0 ? 0 : ((last - first) / first) * 100;
    const sign = percent >= 0 ? "+" : "";
    chartContext.textAlign = "left";
    chartContext.textBaseline = "alphabetic";
    chartContext.fillStyle = "#e8edf4";
    chartContext.font = "700 16px system-ui";
    chartContext.fillText(title, left, 30);
    chartContext.fillStyle = percent >= 0 ? "#35d07f" : "#ff5b63";
    chartContext.font = "700 14px system-ui";
    chartContext.fillText(`${sign}${percent.toFixed(2)}%`, left + Math.min(360, title.length * 8.5 + 18), 30);
    chartContext.fillStyle = "#6f7b8b";
    chartContext.font = "12px system-ui";
    chartContext.fillText(`${asset.symbol} · ${isIntradayRange() ? "Intraday path" : "Stored daily observations"}`, left, top - 10);
}

function drawAreaSeries(model, values, color) {
    const points = seriesPositions(model, values);
    const gradient = chartContext.createLinearGradient(0, model.bounds.top, 0, model.bounds.bottom);
    gradient.addColorStop(0, withAlpha(color, 0.42));
    gradient.addColorStop(1, withAlpha(color, 0.04));

    chartContext.beginPath();
    points.forEach((point, index) => {
        if (index === 0) {
            chartContext.moveTo(point.x, point.y);
        } else {
            chartContext.lineTo(point.x, point.y);
        }
    });
    chartContext.lineTo(points[points.length - 1].x, model.bounds.bottom);
    chartContext.lineTo(points[0].x, model.bounds.bottom);
    chartContext.closePath();
    chartContext.fillStyle = gradient;
    chartContext.fill();

    drawLineSeries(model, values, color, false);
}

function drawLineSeries(model, values, color, dashed) {
    const points = seriesPositions(model, values);
    chartContext.strokeStyle = color;
    chartContext.lineWidth = 2.7;
    chartContext.lineJoin = "round";
    chartContext.lineCap = "round";
    chartContext.setLineDash(dashed ? [7, 6] : []);
    chartContext.beginPath();
    points.forEach((point, index) => {
        if (index === 0) {
            chartContext.moveTo(point.x, point.y);
        } else {
            chartContext.lineTo(point.x, point.y);
        }
    });
    chartContext.stroke();
    chartContext.setLineDash([]);

    const last = points[points.length - 1];
    chartContext.fillStyle = color;
    chartContext.beginPath();
    chartContext.arc(last.x, last.y, 4.5, 0, Math.PI * 2);
    chartContext.fill();
}

function drawHighLowMarkers(model, values, currency) {
    if (!values || values.length < 2) {
        return;
    }

    const positions = seriesPositions(model, values);
    const highValue = Math.max(...values);
    const lowValue = Math.min(...values);
    const highIndex = values.indexOf(highValue);
    const lowIndex = values.indexOf(lowValue);

    drawMarkerLabel(model, positions[highIndex], "High", formatPrice(highValue, currency), "#35d07f", "above");
    if (lowIndex !== highIndex) {
        drawMarkerLabel(model, positions[lowIndex], "Low", formatPrice(lowValue, currency), "#ff5b63", "below");
    }
}

function drawMarkerLabel(model, position, label, value, color, placement) {
    if (!position) {
        return;
    }

    const yOffset = placement === "above" ? -34 : 16;
    const labelText = `${label} ${value}`;
    chartContext.font = "800 11px system-ui";
    const labelWidth = chartContext.measureText(labelText).width + 16;
    const x = Math.max(model.bounds.left + 4, Math.min(position.x - labelWidth / 2, model.bounds.right - labelWidth - 4));
    const y = Math.max(model.bounds.top + 6, Math.min(position.y + yOffset, model.bounds.bottom - 30));

    chartContext.fillStyle = "#05070a";
    chartContext.strokeStyle = withAlpha(color, 0.88);
    chartContext.lineWidth = 1.4;
    roundedRect(chartContext, x, y, labelWidth, 24, 5);
    chartContext.fill();
    chartContext.stroke();
    chartContext.fillStyle = color;
    chartContext.textAlign = "left";
    chartContext.textBaseline = "middle";
    chartContext.fillText(labelText, x + 8, y + 12);

    chartContext.fillStyle = color;
    chartContext.beginPath();
    chartContext.arc(position.x, position.y, 4, 0, Math.PI * 2);
    chartContext.fill();
}

function seriesPositions(model, values) {
    const { left, right, top, bottom } = model.bounds;
    return values.map((value, index) => ({
        x: left + (index / (values.length - 1 || 1)) * (right - left),
        y: bottom - ((value - model.min) / model.range) * (bottom - top)
    }));
}

function drawLatestPriceTag(model, value, currency, color) {
    const { right } = model.bounds;
    const y = yForValue(model, value);
    const label = formatPrice(value, currency);
    chartContext.strokeStyle = withAlpha(color, 0.75);
    chartContext.setLineDash([2, 4]);
    chartContext.beginPath();
    chartContext.moveTo(model.bounds.left, y);
    chartContext.lineTo(right, y);
    chartContext.stroke();
    chartContext.setLineDash([]);

    chartContext.font = "700 12px system-ui";
    const width = chartContext.measureText(label).width + 16;
    roundedRect(chartContext, right + 8, y - 13, width, 26, 4);
    chartContext.fillStyle = color;
    chartContext.fill();
    chartContext.fillStyle = "#05070a";
    chartContext.textAlign = "left";
    chartContext.textBaseline = "middle";
    chartContext.fillText(label, right + 16, y);
}

function drawComparisonLegend(model, primaryAsset, secondaryAsset, primaryColor, secondaryColor) {
    const x = model.bounds.left;
    const y = model.bounds.top + 16;
    drawLegendItem(x, y, primaryAsset.symbol, primaryColor, false);
    drawLegendItem(x + 110, y, secondaryAsset.symbol, secondaryColor, true);
}

function drawLegendItem(x, y, label, color, dashed) {
    chartContext.strokeStyle = color;
    chartContext.lineWidth = 3;
    chartContext.setLineDash(dashed ? [6, 5] : []);
    chartContext.beginPath();
    chartContext.moveTo(x, y);
    chartContext.lineTo(x + 28, y);
    chartContext.stroke();
    chartContext.setLineDash([]);
    chartContext.fillStyle = "#d7dde6";
    chartContext.font = "700 12px system-ui";
    chartContext.textAlign = "left";
    chartContext.textBaseline = "middle";
    chartContext.fillText(label, x + 36, y);
}

function chartColor(category, alternate = false) {
    if (alternate) {
        return "#56a6ff";
    }
    if (category === "CRYPTOCURRENCY") {
        return "#b987ff";
    }
    if (category === "CURRENCY") {
        return "#38bdf8";
    }
    return "#d3a72f";
}

function clearChart() {
    const size = prepareChartCanvas();
    chartContext.clearRect(0, 0, size.width, size.height);
    lastChartRender = null;
}

function drawEmptyChart(message) {
    const size = prepareChartCanvas();
    chartContext.fillStyle = "#05070a";
    chartContext.fillRect(0, 0, size.width, size.height);
    chartContext.fillStyle = "#8792a2";
    chartContext.font = "14px system-ui";
    chartContext.fillText(message, 28, 42);
}

function yForValue(model, value) {
    return model.bounds.bottom - ((value - model.min) / model.range) * (model.bounds.bottom - model.bounds.top);
}

function formatAxisValue(value, unit) {
    if (unit === "%") {
        return `${value.toFixed(1)}`;
    }
    if (Math.abs(value) >= 100) {
        return value.toLocaleString(undefined, { maximumFractionDigits: 0 });
    }
    if (Math.abs(value) >= 1) {
        return value.toLocaleString(undefined, { maximumFractionDigits: 2 });
    }
    return value.toLocaleString(undefined, { maximumFractionDigits: 4 });
}

function withAlpha(hex, alpha) {
    const value = hex.replace("#", "");
    const r = parseInt(value.slice(0, 2), 16);
    const g = parseInt(value.slice(2, 4), 16);
    const b = parseInt(value.slice(4, 6), 16);
    return `rgba(${r}, ${g}, ${b}, ${alpha})`;
}

function roundedRect(context, x, y, width, height, radius) {
    context.beginPath();
    context.moveTo(x + radius, y);
    context.lineTo(x + width - radius, y);
    context.quadraticCurveTo(x + width, y, x + width, y + radius);
    context.lineTo(x + width, y + height - radius);
    context.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
    context.lineTo(x + radius, y + height);
    context.quadraticCurveTo(x, y + height, x, y + height - radius);
    context.lineTo(x, y + radius);
    context.quadraticCurveTo(x, y, x + radius, y);
    context.closePath();
}

function handleChartHover(event) {
    if (!lastChartRender || lastChartRender.series.length === 0) {
        return;
    }

    const rect = priceChart.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const { left, right } = lastChartRender.model.bounds;
    if (x < left || x > right) {
        clearChartHover();
        return;
    }

    const pointCount = lastChartRender.series[0].values.length;
    const ratio = (x - left) / (right - left);
    const nextIndex = Math.max(0, Math.min(pointCount - 1, Math.round(ratio * (pointCount - 1))));
    if (nextIndex !== chartHoverIndex) {
        chartHoverIndex = nextIndex;
        renderCurrentChart();
    }
}

function clearChartHover() {
    if (chartHoverIndex === null) {
        chartTooltip.hidden = true;
        return;
    }
    chartHoverIndex = null;
    chartTooltip.hidden = true;
    if (currentAsset) {
        renderCurrentChart();
    }
}

function drawHoverLayer() {
    if (chartHoverIndex === null || !lastChartRender) {
        return;
    }

    const { model, series } = lastChartRender;
    const firstSeries = series[0];
    const firstPosition = seriesPositions(model, firstSeries.values)[chartHoverIndex];
    if (!firstPosition) {
        return;
    }

    chartContext.strokeStyle = "#6f7b8b";
    chartContext.lineWidth = 1;
    chartContext.setLineDash([4, 4]);
    chartContext.beginPath();
    chartContext.moveTo(firstPosition.x, model.bounds.top);
    chartContext.lineTo(firstPosition.x, model.bounds.bottom);
    chartContext.stroke();
    chartContext.setLineDash([]);

    const tooltipRows = [];
    series.forEach((item) => {
        const positions = seriesPositions(model, item.values);
        const position = positions[chartHoverIndex];
        if (!position) {
            return;
        }
        chartContext.fillStyle = "#05070a";
        chartContext.strokeStyle = item.color;
        chartContext.lineWidth = 2;
        chartContext.beginPath();
        chartContext.arc(position.x, position.y, 5, 0, Math.PI * 2);
        chartContext.fill();
        chartContext.stroke();

        const point = item.points[chartHoverIndex];
        const previousPoint = item.points[Math.max(0, chartHoverIndex - 1)];
        const firstPoint = item.points[0];
        const pointPrice = Number(point.price);
        const previousPrice = Number(previousPoint.price);
        const firstPrice = Number(firstPoint.price);
        const stepMove = pointPrice - previousPrice;
        const windowMove = pointPrice - firstPrice;
        const stepPercent = previousPrice === 0 ? 0 : (stepMove / previousPrice) * 100;
        const windowPercent = firstPrice === 0 ? 0 : (windowMove / firstPrice) * 100;
        const displayValue = lastChartRender.normalized
            ? `${item.values[chartHoverIndex].toFixed(2)} index · ${formatPrice(pointPrice, item.quoteCurrency)}`
            : formatPrice(pointPrice, item.quoteCurrency);
        tooltipRows.push(`
            <div class="tooltip-row">
                <strong>${item.asset.symbol}</strong>
                <span>${displayValue}</span>
            </div>
            <div class="tooltip-row tooltip-subrow">
                <small>Point change</small>
                <span class="${stepMove >= 0 ? "change-up" : "change-down"}">${formatSignedPrice(stepMove, item.quoteCurrency)} · ${formatSigned(stepPercent)}%</span>
            </div>
            <div class="tooltip-row tooltip-subrow">
                <small>Window change</small>
                <span class="${windowMove >= 0 ? "change-up" : "change-down"}">${formatSignedPrice(windowMove, item.quoteCurrency)} · ${formatSigned(windowPercent)}%</span>
            </div>
        `);
    });

    const point = firstSeries.points[chartHoverIndex];
    chartTooltip.innerHTML = `
        <small>${formatChartPointLabel(point)}</small>
        ${tooltipRows.join("")}
    `;
    chartTooltip.hidden = false;
    chartTooltip.style.left = `${Math.min(firstPosition.x + 14, model.size.width - 260)}px`;
    chartTooltip.style.top = `${Math.max(firstPosition.y - 78, 12)}px`;
}

function formatChartPointLabel(point) {
    const rawValue = point.timestamp ?? point.observationDate ?? point.label;
    if (!rawValue) {
        return "Selected point";
    }
    const parsedDate = new Date(rawValue);
    if (!Number.isNaN(parsedDate.getTime())) {
        return parsedDate.toLocaleString(undefined, {
            month: "short",
            day: "numeric",
            year: "numeric",
            hour: isIntradayRange() ? "2-digit" : undefined,
            minute: isIntradayRange() ? "2-digit" : undefined
        });
    }
    return rawValue;
}

function renderList(element, items) {
    element.innerHTML = "";
    items.forEach((text) => {
        const item = document.createElement("li");
        item.textContent = text;
        element.appendChild(item);
    });
}

function renderRippleMap(data) {
    mapTitle.textContent = `${data.title}. ${data.disclaimer}`;
    rippleMap.innerHTML = "";

    data.edges.forEach((edge) => {
        const row = document.createElement("div");
        row.className = "ripple-edge";
        row.innerHTML = `
            <div class="node">${edge.from}</div>
            <div class="arrow">-></div>
            <div class="node">${edge.to}</div>
            <div class="relationship">
                ${edge.relationship}<br>
                Direction: ${edge.direction}. Strength: ${edge.strength}. Confidence: ${edge.confidence}.
            </div>
        `;
        rippleMap.appendChild(row);
    });
}

start().catch((error) => {
    snapshot.textContent = error.message;
});
