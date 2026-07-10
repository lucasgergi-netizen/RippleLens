package com.RippleLens.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.RippleLens.insight.InsightContext;
import com.RippleLens.insight.InsightResponse;
import com.RippleLens.intelligence.MacroContextResponse;
import com.RippleLens.model.Asset;
import com.RippleLens.model.AssetCategory;
import com.RippleLens.model.AssetPricePoint;
import com.RippleLens.relationship.RelationshipItemResponse;
import com.RippleLens.relationship.RelationshipReportResponse;
import com.RippleLens.trust.AssetTrustReportResponse;

@Service
public class InsightService {

    private final AssetService assetService;
    private final OpenAiAnalysisClient openAiAnalysisClient;
    private final MacroContextService macroContextService;
    private final EventIntelligenceService eventIntelligenceService;
    private final MarketNewsService marketNewsService;
    private final RelationshipEngineService relationshipEngineService;

    public InsightService(
            AssetService assetService,
            OpenAiAnalysisClient openAiAnalysisClient,
            MacroContextService macroContextService,
            EventIntelligenceService eventIntelligenceService,
            MarketNewsService marketNewsService,
            RelationshipEngineService relationshipEngineService) {
        this.assetService = assetService;
        this.openAiAnalysisClient = openAiAnalysisClient;
        this.macroContextService = macroContextService;
        this.eventIntelligenceService = eventIntelligenceService;
        this.marketNewsService = marketNewsService;
        this.relationshipEngineService = relationshipEngineService;
    }

    public InsightResponse explain(String symbol, int days) {
        Asset asset = assetService.getAssetBySymbol(symbol);
        List<AssetPricePoint> history = assetService.getPriceHistory(symbol);
        List<AssetPricePoint> window = history.size() > days ? history.subList(history.size() - days, history.size()) : history;
        double change = calculateChange(window);
        String direction = change >= 0 ? "increased" : "decreased";
        String confidence = confidenceFor(asset, change, window.size());
        List<String> causes = causesFor(asset);
        List<String> consequences = consequencesFor(asset);
        List<String> signals = supportingSignalsFor(asset, change, window.size());
        InsightContext context = buildContext(asset, days);
        causes = enrichCauses(causes, context);
        consequences = enrichConsequences(consequences, context);
        signals = enrichSignals(signals, context);
        String magnitude = magnitudeFor(asset, change);
        String pressureChannel = pressureChannelFor(asset, change);
        String contextSentence = contextSentence(context);

        String summary = "%s %s %.2f%% over the selected %d-day window, a %s move for this asset type. The strongest first hypothesis is %s. %s RippleLens would investigate %s as likely drivers and watch for %s. Confidence: %s because this is an explainable signal review, not a certain forecast."
                .formatted(
                        asset.getName(),
                        direction,
                        Math.abs(change),
                        days,
                        magnitude,
                        pressureChannel,
                        contextSentence,
                        joinFirstSignals(causes),
                        consequences.isEmpty() ? "secondary market effects" : consequences.get(0),
                        confidence);

        InsightResponse baseline = new InsightResponse(
                asset.getSymbol(),
                asset.getName(),
                asset.getCategory().name(),
                asset.getCurrentPrice(),
                round(change),
                direction,
                confidence,
                "Context-aware economic explanation",
                "RippleLens explainable context engine v2",
                summary,
                causes,
                consequences,
                signals,
                "Educational analysis only. RippleLens does not provide guaranteed forecasts or personal investment advice.");

        return openAiAnalysisClient.explain(asset, window, baseline, context, days).orElse(baseline);
    }

    private InsightContext buildContext(Asset asset, int days) {
        return new InsightContext(
                safe(() -> assetService.getTrustReport(asset.getSymbol())),
                safe(() -> macroContextService.explainMacroContext(asset.getSymbol())),
                safeList(() -> eventIntelligenceService.getSignals(asset.getSymbol())),
                safeList(() -> marketNewsService.getRelatedNews(asset.getSymbol())),
                safe(() -> relationshipEngineService.analyze(asset.getSymbol(), Math.max(7, days))));
    }

    private <T> T safe(ContextSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (RuntimeException error) {
            return null;
        }
    }

    private <T> List<T> safeList(ContextSupplier<List<T>> supplier) {
        List<T> values = safe(supplier);
        return values == null ? List.of() : values;
    }

    private List<String> enrichCauses(List<String> causes, InsightContext context) {
        List<String> enriched = new ArrayList<>(causes);
        if (context.macroContext() != null) {
            context.macroContext().pressureChannels().stream()
                    .limit(2)
                    .map(channel -> "macro channel: " + channel)
                    .forEach(enriched::add);
        }
        if (context.relationshipReport() != null && !context.relationshipReport().relationships().isEmpty()) {
            RelationshipItemResponse top = context.relationshipReport().relationships().get(0);
            enriched.add("relationship signal: " + top.symbol() + " " + top.strength() + " " + top.direction() + " correlation");
        }
        context.eventSignals().stream()
                .findFirst()
                .map(signal -> "event watch: " + signal.title())
                .ifPresent(enriched::add);
        return distinctLimited(enriched, 5);
    }

    private List<String> enrichConsequences(List<String> consequences, InsightContext context) {
        List<String> enriched = new ArrayList<>(consequences);
        context.eventSignals().stream()
                .flatMap(signal -> signal.affectedAreas().stream())
                .limit(2)
                .map(area -> area + " may need monitoring")
                .forEach(enriched::add);
        if (context.relationshipReport() != null && context.relationshipReport().relationships().stream().anyMatch(RelationshipItemResponse::unusualBehavior)) {
            enriched.add("related assets may be breaking from their recent relationship pattern");
        }
        if (!context.relatedNews().isEmpty()) {
            enriched.add("recent headlines may affect how traders interpret the move");
        }
        return distinctLimited(enriched, 5);
    }

    private List<String> enrichSignals(List<String> signals, InsightContext context) {
        List<String> enriched = new ArrayList<>(signals);
        AssetTrustReportResponse trust = context.trustReport();
        if (trust != null) {
            enriched.add("Data trust: " + trust.dataTrustScore() + "/100, " + trust.sourceMode() + ", " + trust.freshnessStatus() + ".");
            enriched.add("Data coverage: " + trust.coverageSummary());
        }
        MacroContextResponse macro = context.macroContext();
        if (macro != null) {
            enriched.add("Macro context: " + macro.summary());
        }
        RelationshipReportResponse relationship = context.relationshipReport();
        if (relationship != null) {
            enriched.add("Relationship context: " + relationship.topRelationship() + ".");
        }
        if (!context.relatedNews().isEmpty()) {
            enriched.add("Related news available: " + context.relatedNews().size() + " recent headline(s).");
        }
        return distinctLimited(enriched, 8);
    }

    private String contextSentence(InsightContext context) {
        List<String> contextSignals = new ArrayList<>();
        if (context.trustReport() != null) {
            contextSignals.add("data trust is " + context.trustReport().confidenceLevel());
        }
        if (context.relationshipReport() != null && !context.relationshipReport().relationships().isEmpty()) {
            contextSignals.add("the strongest relationship is " + context.relationshipReport().topRelationship());
        }
        if (context.macroContext() != null && !context.macroContext().pressureChannels().isEmpty()) {
            contextSignals.add("macro watchpoint: " + context.macroContext().pressureChannels().get(0));
        }
        if (contextSignals.isEmpty()) {
            return "Context signals are limited.";
        }
        return "Context check: " + String.join("; ", contextSignals) + ".";
    }

    private List<String> distinctLimited(List<String> values, int maxItems) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (value == null || value.isBlank() || result.contains(value)) {
                continue;
            }
            result.add(value);
            if (result.size() >= maxItems) {
                break;
            }
        }
        return result;
    }

    @FunctionalInterface
    private interface ContextSupplier<T> {
        T get();
    }

    private double calculateChange(List<AssetPricePoint> points) {
        if (points.size() < 2) {
            return 0;
        }
        BigDecimal first = points.get(0).getPrice();
        BigDecimal last = points.get(points.size() - 1).getPrice();
        if (BigDecimal.ZERO.compareTo(first) == 0) {
            return 0;
        }
        return last.subtract(first)
                .divide(first, 8, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    private String confidenceFor(Asset asset, double change, int observations) {
        if (observations < 7) {
            return "low";
        }
        if (asset.getCategory() == AssetCategory.CRYPTOCURRENCY && Math.abs(change) > 8) {
            return "medium";
        }
        if (asset.getCategory() == AssetCategory.METAL && Math.abs(change) > 3) {
            return "medium";
        }
        if (asset.getCategory() == AssetCategory.CURRENCY && Math.abs(change) > 1) {
            return "medium";
        }
        return "low";
    }

    private String magnitudeFor(Asset asset, double change) {
        double absolute = Math.abs(change);
        return switch (asset.getCategory()) {
            case METAL -> absolute >= 4 ? "large" : absolute >= 1.5 ? "meaningful" : "modest";
            case CRYPTOCURRENCY -> absolute >= 10 ? "large" : absolute >= 4 ? "meaningful" : "modest";
            case CURRENCY -> absolute >= 1.5 ? "large" : absolute >= 0.5 ? "meaningful" : "modest";
        };
    }

    private String pressureChannelFor(Asset asset, double change) {
        boolean rising = change >= 0;
        return switch (asset.getCategory()) {
            case METAL -> rising
                    ? "higher demand, inflation concern, supply pressure, or weaker USD conditions"
                    : "softer demand, stronger USD conditions, lower inflation fear, or easing supply stress";
            case CRYPTOCURRENCY -> rising
                    ? "improving speculative liquidity and risk appetite"
                    : "weaker risk appetite, tighter liquidity, or regulatory/exchange uncertainty";
            case CURRENCY -> rising
                    ? "relative monetary strength, capital inflows, or stronger macro expectations"
                    : "relative monetary weakness, capital outflows, or softer macro expectations";
        };
    }

    private List<String> causesFor(Asset asset) {
        return switch (asset.getSymbol()) {
            case "XAU" -> List.of("real interest-rate expectations", "US dollar movement", "safe-haven demand", "inflation uncertainty");
            case "XAG" -> List.of("industrial demand", "solar technology demand", "gold sympathy movement", "inflation expectations");
            case "XCU" -> List.of("construction demand", "manufacturing activity", "electrification demand", "China growth expectations");
            case "XPT" -> List.of("South Africa mining supply", "auto catalyst demand", "hydrogen technology expectations", "energy costs");
            case "XPD" -> List.of("Russia supply risk", "gasoline vehicle production", "EV substitution pressure", "catalyst metal substitution");
            case "BTC" -> List.of("crypto risk appetite", "liquidity expectations", "digital scarcity narrative", "institutional demand");
            case "ETH" -> List.of("smart-contract activity", "crypto liquidity", "network upgrade expectations", "risk appetite");
            case "USDT", "USDC" -> List.of("stablecoin liquidity demand", "crypto settlement flows", "dollar funding conditions", "exchange activity");
            case "BNB" -> List.of("exchange ecosystem activity", "crypto trading volumes", "blockchain fee demand", "risk appetite");
            case "XRP" -> List.of("payments adoption narrative", "regulatory developments", "crypto liquidity", "cross-border settlement demand");
            case "SOL" -> List.of("high-beta crypto sentiment", "network activity", "speculative liquidity", "risk appetite");
            case "TRX" -> List.of("stablecoin transfer activity", "retail crypto flows", "network usage", "crypto liquidity");
            case "FIGR_HELOC" -> List.of("tokenized credit demand", "real-world asset narratives", "yield appetite", "crypto liquidity");
            case "HYPE" -> List.of("decentralized trading activity", "derivatives volume", "speculative liquidity", "risk appetite");
            case "USD" -> List.of("US rate expectations", "reserve currency demand", "risk-off flows", "US macro data");
            case "EUR" -> List.of("ECB rate expectations", "European growth data", "energy outlook", "USD cross-currency movement");
            case "JPY" -> List.of("Bank of Japan policy", "carry-trade flows", "safe-haven demand", "US-Japan rate gap");
            case "GBP" -> List.of("Bank of England expectations", "UK inflation", "UK growth data", "USD comparison");
            case "CNY" -> List.of("China growth expectations", "export competitiveness", "policy guidance", "commodity demand signals");
            case "CAD" -> List.of("oil and commodity prices", "Bank of Canada expectations", "US growth", "risk appetite");
            case "AUD" -> List.of("China demand expectations", "commodity prices", "Reserve Bank of Australia policy", "risk appetite");
            case "CHF" -> List.of("safe-haven flows", "Swiss National Bank policy", "European risk conditions", "inflation expectations");
            case "HKD" -> List.of("USD peg conditions", "Hong Kong liquidity", "regional capital flows", "US rate expectations");
            case "SGD" -> List.of("trade conditions", "inflation management", "regional growth", "safe-currency demand");
            case "SEK" -> List.of("European growth", "Riksbank policy", "export demand", "risk appetite");
            case "AED" -> List.of("US dollar peg conditions", "oil and Gulf trade flows", "UAE liquidity", "regional remittance demand");
            default -> causesForCategory(asset);
        };
    }

    private List<String> consequencesFor(Asset asset) {
        return switch (asset.getSymbol()) {
            case "XAU" -> List.of("jewelry affordability may change", "central-bank reserve attention may increase", "inflation-hedging narratives may strengthen");
            case "XAG" -> List.of("solar input costs may move", "electronics costs may be affected", "industrial users may adjust inventories");
            case "XCU" -> List.of("construction input costs may move", "electrical equipment costs may change", "industrial cycle expectations may shift");
            case "XPT" -> List.of("vehicle component costs may change", "manufacturers may review sourcing", "hydrogen technology cost narratives may shift");
            case "XPD" -> List.of("auto catalyst costs may shift", "manufacturers may substitute metals", "long-term demand may face EV pressure");
            case "BTC" -> List.of("risk sentiment may shift", "gold-versus-bitcoin comparisons may intensify", "speculative flows may affect other crypto assets");
            case "ETH" -> List.of("DeFi sentiment may change", "crypto market confidence may shift", "high-beta asset flows may move");
            case "USDT", "USDC" -> List.of("crypto liquidity conditions may change", "exchange settlement flows may shift", "dollar demand inside crypto may be visible");
            case "BNB" -> List.of("exchange ecosystem confidence may shift", "trading activity narratives may change", "related blockchain assets may react");
            case "XRP" -> List.of("payments-sector narratives may shift", "regulatory attention may increase", "cross-border settlement sentiment may change");
            case "SOL" -> List.of("speculative crypto flows may move", "risk appetite signals may strengthen", "network confidence narratives may shift");
            case "TRX" -> List.of("stablecoin transfer costs may matter more", "retail crypto flows may shift", "network activity narratives may change");
            case "FIGR_HELOC" -> List.of("tokenized credit narratives may change", "real-world asset sentiment may shift", "yield-seeking flows may be affected");
            case "HYPE" -> List.of("decentralized trading sentiment may shift", "high-beta crypto flows may move", "derivatives risk appetite may be repriced");
            case "USD" -> List.of("USD metal prices may move", "global import costs may shift", "emerging-market pressure may change");
            case "EUR" -> List.of("metals priced in euros may diverge from USD charts", "European purchasing power may shift", "import cost pressure may change");
            case "JPY" -> List.of("carry trades may adjust", "Japanese import costs may shift", "safe-haven currency comparisons may change");
            case "GBP" -> List.of("UK import costs may change", "GBP metal prices may diverge from USD prices", "consumer purchasing power may shift");
            case "CNY" -> List.of("commodity demand expectations may shift", "global trade pricing may change", "regional currency pressure may spread");
            case "CAD" -> List.of("commodity-linked FX narratives may shift", "Canadian import costs may move", "North American trade signals may change");
            case "AUD" -> List.of("commodity producer sentiment may shift", "Asia-Pacific trade signals may change", "risk appetite readings may move");
            case "CHF" -> List.of("safe-haven demand may be visible", "European purchasing power may shift", "currency intervention attention may rise");
            case "HKD" -> List.of("regional liquidity readings may change", "USD peg attention may increase", "financial hub conditions may be watched");
            case "SGD" -> List.of("regional import costs may shift", "trade competitiveness may change", "Asian currency signals may strengthen");
            case "SEK" -> List.of("European export competitiveness may shift", "imported inflation pressure may change", "cyclical currency sentiment may move");
            case "AED" -> List.of("Gulf import purchasing power may shift", "remittance values may change", "energy-trade settlement signals may be watched");
            default -> consequencesForCategory(asset);
        };
    }

    private List<String> causesForCategory(Asset asset) {
        return switch (asset.getCategory()) {
            case METAL -> List.of("supply and demand", "US dollar movement", "global growth expectations", "inventory conditions");
            case CRYPTOCURRENCY -> List.of("crypto liquidity", "risk appetite", "network activity", "regulatory developments");
            case CURRENCY -> List.of("central-bank policy", "inflation expectations", "growth data", "cross-border capital flows");
        };
    }

    private List<String> consequencesForCategory(Asset asset) {
        return switch (asset.getCategory()) {
            case METAL -> List.of("input costs may shift", "consumer affordability may change", "supply-chain decisions may adjust");
            case CRYPTOCURRENCY -> List.of("market sentiment may shift", "speculative flows may move", "related crypto assets may react");
            case CURRENCY -> List.of("import costs may change", "purchasing power may shift", "commodity prices in local currency may diverge");
        };
    }

    private List<String> supportingSignalsFor(Asset asset, double change, int observations) {
        List<String> signals = new ArrayList<>();
        signals.add(observations + " stored observations available for this asset.");
        signals.add("Selected-period movement: " + round(change) + "%.");
        signals.add("Asset category: " + asset.getCategory().name().toLowerCase() + ".");
        if (asset.getCategory() == AssetCategory.CRYPTOCURRENCY) {
            signals.add("Top crypto prices are requested from Binance Spot first, then CoinGecko as fallback.");
        }
        if (asset.getCategory() == AssetCategory.CURRENCY) {
            if ("AED".equals(asset.getSymbol())) {
                signals.add("AED rates are requested from Frankfurter v2 first, then ExchangeRate API as fallback.");
            } else {
                signals.add("Major FX rates are requested from Frankfurter when the app refreshes data.");
            }
        }
        if (asset.getCategory() == AssetCategory.METAL) {
            signals.add("Metals use Gold API for latest quotes and Metals.Dev for historical charts when the API key is configured.");
        }
        return signals;
    }

    private String joinFirstSignals(List<String> values) {
        if (values.isEmpty()) {
            return "macro and market signals";
        }
        if (values.size() == 1) {
            return values.get(0);
        }
        return values.get(0) + " and " + values.get(1);
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
