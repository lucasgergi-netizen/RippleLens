package com.RippleLens.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.RippleLens.intelligence.MacroContextResponse;
import com.RippleLens.intelligence.MacroIndicatorResponse;
import com.RippleLens.model.Asset;
import com.RippleLens.model.AssetCategory;

@Service
public class MacroContextService {

    private final AssetService assetService;
    private final WorldBankMacroClient worldBankMacroClient;

    public MacroContextService(AssetService assetService, WorldBankMacroClient worldBankMacroClient) {
        this.assetService = assetService;
        this.worldBankMacroClient = worldBankMacroClient;
    }

    public List<MacroIndicatorResponse> getIndicators() {
        return worldBankMacroClient.getCoreIndicators();
    }

    public MacroContextResponse explainMacroContext(String symbol) {
        Asset asset = assetService.getAssetBySymbol(symbol);
        List<MacroIndicatorResponse> indicators = getIndicators();
        return new MacroContextResponse(
                asset.getSymbol(),
                asset.getName(),
                asset.getCategory().name(),
                summaryFor(asset),
                indicators,
                pressureChannelsFor(asset),
                "Macro indicators are broad context signals, not guaranteed explanations or investment advice.");
    }

    private String summaryFor(Asset asset) {
        return switch (asset.getCategory()) {
            case METAL -> asset.getName()
                    + " can react to inflation, dollar strength, real-rate expectations, industrial demand, and global growth signals.";
            case CRYPTOCURRENCY -> asset.getName()
                    + " can react to liquidity conditions, risk appetite, dollar conditions, and technology/regulatory narratives.";
            case CURRENCY -> asset.getName()
                    + " can react to relative growth, inflation, trade flows, central-bank expectations, and dollar pressure.";
        };
    }

    private List<String> pressureChannelsFor(Asset asset) {
        if (asset.getCategory() == AssetCategory.METAL) {
            return switch (asset.getSymbol()) {
                case "XAU" -> List.of("US inflation and real-rate expectations", "US dollar strength", "safe-haven demand");
                case "XCU" -> List.of("China GDP growth", "world GDP growth", "construction and electrification demand");
                case "XAG" -> List.of("industrial production", "solar technology demand", "gold sympathy movement");
                default -> List.of("supply concentration", "industrial demand", "energy and mining costs");
            };
        }
        if (asset.getCategory() == AssetCategory.CRYPTOCURRENCY) {
            return List.of("global liquidity", "risk appetite", "US dollar conditions", "regulatory or technology events");
        }
        return switch (asset.getSymbol()) {
            case "AED", "HKD" -> List.of("US dollar peg pressure", "regional liquidity", "trade and remittance flows");
            case "CNY" -> List.of("China growth", "trade policy", "commodity import demand");
            case "AUD", "CAD" -> List.of("commodity prices", "China and US growth", "risk appetite");
            default -> List.of("relative inflation", "central-bank expectations", "growth surprises", "capital flows");
        };
    }
}
