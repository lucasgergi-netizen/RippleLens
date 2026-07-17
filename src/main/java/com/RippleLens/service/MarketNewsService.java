package com.RippleLens.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.RippleLens.intelligence.NewsItemResponse;
import com.RippleLens.live.ProviderHealthResponse;
import com.RippleLens.model.Asset;
import com.RippleLens.model.AssetCategory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MarketNewsService {

    private static final String NEWS_API_EVERYTHING_URL = "https://newsapi.org/v2/everything";
    private static final int NEWS_LIMIT = 5;

    private final AssetService assetService;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public MarketNewsService(
            AssetService assetService,
            @Value("${newsapi.api-key:${NEWS_API_KEY:}}") String apiKey) {
        this.assetService = assetService;
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(6))
                .build();
    }

    public List<NewsItemResponse> getRelatedNews(String symbol) {
        if (!isConfigured()) {
            return List.of();
        }

        Asset asset = assetService.getAssetBySymbol(symbol);
        String url = NEWS_API_EVERYTHING_URL
                + "?q=" + encode(newsQueryFor(asset))
                + "&searchIn=title,description"
                + "&language=en"
                + "&sortBy=relevancy"
                + "&pageSize=" + NEWS_LIMIT
                + "&from=" + LocalDate.now().minusDays(7);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(9))
                    .header("Accept", "application/json")
                    .header("X-Api-Key", apiKey)
                    .header("User-Agent", "RippleLens student fintech app")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return List.of();
            }

            JsonNode root = objectMapper.readTree(response.body());
            if (!"ok".equalsIgnoreCase(root.path("status").asText("")) || !root.path("articles").isArray()) {
                return List.of();
            }

            List<NewsItemResponse> results = new ArrayList<>();
            Set<String> seen = new LinkedHashSet<>();
            for (JsonNode article : root.path("articles")) {
                String title = article.path("title").asText("");
                String articleUrl = article.path("url").asText("");
                if (title.isBlank() || articleUrl.isBlank() || "[Removed]".equalsIgnoreCase(title.trim())) {
                    continue;
                }
                String duplicateKey = articleUrl.toLowerCase();
                if (!seen.add(duplicateKey)) {
                    continue;
                }
                String description = article.path("description").asText("");
                NewsInterpretation interpretation = interpretNews(asset, title, description);
                results.add(new NewsItemResponse(
                        title,
                        description,
                        interpretation.impactLevel(),
                        interpretation.priceEffect(),
                        interpretation.whyItMatters(),
                        articleUrl,
                        article.path("source").path("name").asText("NewsAPI.org"),
                        article.path("publishedAt").asText(""),
                        ""));
                if (results.size() >= NEWS_LIMIT) {
                    break;
                }
            }
            return results;
        } catch (IOException | InterruptedException | RuntimeException error) {
            if (error instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return List.of();
        }
    }

    public ProviderHealthResponse healthCheck() {
        Instant checkedAt = Instant.now();
        if (!isConfigured()) {
            return new ProviderHealthResponse(
                    "NewsAPI.org",
                    "Related asset news",
                    "NOT_CONFIGURED",
                    0,
                    checkedAt,
                    "Add NEWS_API_KEY to enable 5 real related news headlines per asset.");
        }

        long started = System.nanoTime();
        String url = NEWS_API_EVERYTHING_URL
                + "?q=gold%20price"
                + "&searchIn=title,description"
                + "&language=en"
                + "&sortBy=relevancy"
                + "&pageSize=1"
                + "&from=" + LocalDate.now().minusDays(7);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(9))
                    .header("Accept", "application/json")
                    .header("X-Api-Key", apiKey)
                    .header("User-Agent", "RippleLens student fintech app")
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long latencyMs = elapsedMs(started);
            if (response.statusCode() == 401 || response.statusCode() == 403) {
                return new ProviderHealthResponse("NewsAPI.org", "Related asset news", "UNAVAILABLE", latencyMs,
                        checkedAt, "API key was rejected by NewsAPI.org.");
            }
            if (response.statusCode() == 429) {
                return new ProviderHealthResponse("NewsAPI.org", "Related asset news", "DEGRADED", latencyMs,
                        checkedAt, "Provider rate limit reached; watch signals will be shown as fallback.");
            }
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return new ProviderHealthResponse("NewsAPI.org", "Related asset news", "UNAVAILABLE", latencyMs,
                        checkedAt, "HTTP " + response.statusCode() + " from NewsAPI.org.");
            }

            JsonNode root = objectMapper.readTree(response.body());
            if (!"ok".equalsIgnoreCase(root.path("status").asText("")) || !root.path("articles").isArray()) {
                return new ProviderHealthResponse("NewsAPI.org", "Related asset news", "DEGRADED", latencyMs,
                        checkedAt, "Provider responded, but the payload shape was unexpected.");
            }
            return new ProviderHealthResponse("NewsAPI.org", "Related asset news", "AVAILABLE", latencyMs,
                    checkedAt, "Checks top related headlines from NewsAPI.org.");
        } catch (IOException | InterruptedException | RuntimeException error) {
            if (error instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return new ProviderHealthResponse("NewsAPI.org", "Related asset news", "UNAVAILABLE", elapsedMs(started),
                    checkedAt, "Connection to NewsAPI.org failed.");
        }
    }

    public boolean isConfigured() {
        return !apiKey.isBlank();
    }

    private String newsQueryFor(Asset asset) {
        String quotedName = "\"" + asset.getName() + "\"";
        return switch (asset.getCategory()) {
            case METAL -> "(" + quotedName + " OR " + asset.getSymbol() + ") AND (price OR metals OR commodities OR inflation OR dollar)";
            case CRYPTOCURRENCY -> "(" + quotedName + " OR " + asset.getSymbol() + ") AND (crypto OR market OR price OR ETF OR blockchain)";
            case CURRENCY -> "(" + quotedName + " OR " + asset.getSymbol() + ") AND (currency OR forex OR central bank OR inflation OR exchange rate)";
        };
    }

    private NewsInterpretation interpretNews(Asset asset, String title, String description) {
        String text = (title + " " + description).toLowerCase();
        int score = 0;
        if (containsAny(text, "fed", "federal reserve", "central bank", "ecb", "boj", "rate decision", "interest rate", "inflation", "cpi")) {
            score += 3;
        }
        if (containsAny(text, "etf", "sec", "regulation", "lawsuit", "approval", "ban", "exchange", "hack", "whale", "institutional")) {
            score += asset.getCategory() == AssetCategory.CRYPTOCURRENCY ? 3 : 1;
        }
        if (containsAny(text, "war", "sanction", "geopolitical", "tariff", "trade", "supply", "mine", "strike", "inventory")) {
            score += asset.getCategory() == AssetCategory.METAL ? 3 : 2;
        }
        if (containsAny(text, "dollar", "usd", "yield", "treasury", "jobs", "employment", "gdp", "recession")) {
            score += 2;
        }
        if (containsAny(text, asset.getName().toLowerCase(), asset.getSymbol().toLowerCase())) {
            score += 2;
        }

        String impactLevel = score >= 5 ? "High impact" : score >= 3 ? "Medium impact" : "Low impact";
        String priceEffect = priceEffectFor(asset, text);
        String whyItMatters = whyItMattersFor(asset, text, priceEffect);
        return new NewsInterpretation(impactLevel, priceEffect, whyItMatters);
    }

    private String priceEffectFor(Asset asset, String text) {
        if (containsAny(text, "falls", "fell", "drops", "dropped", "down", "selloff", "outflows", "ban", "hack", "lawsuit", "strong dollar", "higher yields")) {
            return "Could pressure price";
        }
        if (containsAny(text, "rises", "rose", "jumps", "surges", "up", "rally", "inflows", "approval", "demand", "safe haven", "weaker dollar")) {
            return "Could support price";
        }
        if (asset.getCategory() == AssetCategory.CURRENCY && containsAny(text, "rate hike", "higher rates", "hawkish")) {
            return "Could support price";
        }
        if (asset.getCategory() == AssetCategory.METAL && containsAny(text, "higher rates", "hawkish", "treasury yields rise")) {
            return "Could pressure price";
        }
        return "Unclear effect";
    }

    private String whyItMattersFor(Asset asset, String text, String priceEffect) {
        if (containsAny(text, "fed", "central bank", "interest rate", "inflation", "cpi", "yield")) {
            return switch (asset.getCategory()) {
                case METAL -> "Rates, inflation, and the dollar can change demand for metals used as stores of value or industrial inputs.";
                case CRYPTOCURRENCY -> "Liquidity and rate expectations can change appetite for speculative crypto assets.";
                case CURRENCY -> "Central-bank expectations directly affect currency strength and cross-border capital flows.";
            };
        }
        if (containsAny(text, "etf", "sec", "regulation", "approval", "lawsuit", "exchange", "hack")) {
            return asset.getCategory() == AssetCategory.CRYPTOCURRENCY
                    ? "Regulation, ETF flows, and exchange confidence can quickly change crypto demand."
                    : "Regulatory or market-structure news can change risk appetite around this asset.";
        }
        if (containsAny(text, "supply", "mine", "strike", "inventory", "sanction", "war", "tariff", "trade")) {
            return asset.getCategory() == AssetCategory.METAL
                    ? "Supply-chain and geopolitical news can change expected availability and input costs."
                    : "Trade and geopolitical news can affect risk appetite, demand, and currency flows.";
        }
        if (containsAny(text, "dollar", "usd")) {
            return "Dollar movement matters because many global assets are priced, settled, or compared in USD.";
        }
        return switch (priceEffect) {
            case "Could support price" -> "The headline appears connected to demand, confidence, or liquidity that may support this asset.";
            case "Could pressure price" -> "The headline appears connected to risk, supply, regulation, or macro pressure that may weigh on this asset.";
            default -> "The article is related, but the direct price effect is not clear from the headline alone.";
        };
    }

    private boolean containsAny(String text, String... terms) {
        for (String term : terms) {
            if (text.contains(term)) {
                return true;
            }
        }
        return false;
    }

    private record NewsInterpretation(String impactLevel, String priceEffect, String whyItMatters) {
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private long elapsedMs(long started) {
        return Math.max(1, Duration.ofNanos(System.nanoTime() - started).toMillis());
    }
}
