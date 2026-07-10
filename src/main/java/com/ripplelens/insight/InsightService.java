package com.ripplelens.insight;

import com.ripplelens.asset.Asset;
import com.ripplelens.asset.AssetRepository;
import com.ripplelens.marketdata.MarketDataService;
import com.ripplelens.marketdata.MarketPricePoint;
import com.ripplelens.marketdata.MarketPriceSeries;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class InsightService {

    private final AssetRepository assetRepository;
    private final MarketDataService marketDataService;

    public InsightService(AssetRepository assetRepository, MarketDataService marketDataService) {
        this.assetRepository = assetRepository;
        this.marketDataService = marketDataService;
    }

    public InsightResponse explainAsset(String symbol) {
        Asset asset = assetRepository.findBySymbolIgnoreCase(symbol)
                .orElseThrow(() -> new IllegalArgumentException("Unknown asset symbol: " + symbol));

        MarketPriceSeries series = marketDataService.pricesFor(asset);
        List<MarketPricePoint> prices = series.prices();
        if (prices.isEmpty()) {
            throw new IllegalStateException("No price data for asset symbol: " + symbol);
        }

        BigDecimal first = prices.getFirst().priceUsd();
        BigDecimal latest = prices.getLast().priceUsd();
        double changePercent = latest.subtract(first)
                .divide(first, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();

        List<String> causes = possibleCauses(asset.getSymbol(), changePercent);
        List<String> consequences = possibleConsequences(asset.getSymbol(), changePercent);

        return new InsightResponse(
                asset.getSymbol(),
                asset.getName(),
                latest.setScale(4, RoundingMode.HALF_UP),
                round(changePercent),
                confidence(changePercent),
                series.dataSource(),
                series.live(),
                causes,
                consequences,
                "RippleLens explains possible causes using data signals. It is not financial advice."
        );
    }

    public MoneyPressureResponse moneyPressureForMetal(String symbol) {
        InsightResponse insight = explainAsset(symbol);
        double metalMomentum = Math.min(100, Math.abs(insight.thirtyDayChangePercent()) * 8);
        double cryptoRisk = insight.thirtyDayChangePercent() > 0 ? 42 : 64;
        double currencyStress = insight.thirtyDayChangePercent() > 2 ? 71 : 48;

        String interpretation = insight.thirtyDayChangePercent() > 2
                ? "The metal is rising strongly enough that RippleLens would compare it against USD weakness, inflation pressure, real yields, and geopolitical news."
                : "The metal move is moderate. RippleLens would look for whether this is a normal market fluctuation or a hidden currency/inflation signal.";

        return new MoneyPressureResponse(
                insight.symbol(),
                interpretation,
                round(metalMomentum),
                round(cryptoRisk),
                round(currencyStress),
                insight.confidence()
        );
    }

    private List<String> possibleCauses(String symbol, double changePercent) {
        if ("XAU".equals(symbol)) {
            return List.of("possible safe-haven demand", "inflation expectations", "weaker currency confidence", "interest-rate uncertainty");
        }
        if ("XAG".equals(symbol)) {
            return List.of("industrial demand", "solar technology demand", "gold sympathy move", "inflation expectations");
        }
        if ("XPT".equals(symbol)) {
            return List.of("auto industry demand", "hydrogen technology expectations", "South Africa supply risk", "energy cost pressure");
        }
        if ("XPD".equals(symbol)) {
            return List.of("catalytic converter demand", "Russia supply risk", "EV substitution pressure", "auto production changes");
        }
        if (changePercent > 3) {
            return List.of("risk appetite increased", "liquidity improved", "market momentum strengthened");
        }
        return List.of("normal market movement", "currency effects", "short-term investor positioning");
    }

    private List<String> possibleConsequences(String symbol, double changePercent) {
        if ("XAU".equals(symbol)) {
            return List.of("jewelry demand may weaken", "central-bank reserve debates may intensify", "investors may reassess inflation protection");
        }
        if ("XAG".equals(symbol)) {
            return List.of("solar panel input costs may change", "electronics costs may be affected", "retail precious-metal demand may shift");
        }
        if ("XPT".equals(symbol) || "XPD".equals(symbol)) {
            return List.of("vehicle component costs may change", "auto manufacturers may adjust sourcing", "substitution between metals may become more attractive");
        }
        return List.of("portfolio risk appetite may change", "market liquidity signals may shift");
    }

    private String confidence(double changePercent) {
        if (Math.abs(changePercent) > 5) {
            return "medium";
        }
        return "low";
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
