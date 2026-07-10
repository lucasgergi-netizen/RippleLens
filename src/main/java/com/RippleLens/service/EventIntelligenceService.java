package com.RippleLens.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.RippleLens.intelligence.EventSignalResponse;
import com.RippleLens.model.Asset;
import com.RippleLens.model.AssetCategory;

@Service
public class EventIntelligenceService {

    private final AssetService assetService;

    public EventIntelligenceService(AssetService assetService) {
        this.assetService = assetService;
    }

    public List<EventSignalResponse> getSignals(String symbol) {
        Asset asset = assetService.getAssetBySymbol(symbol);
        List<EventSignalResponse> signals = new ArrayList<>();

        if (asset.getCategory() == AssetCategory.METAL) {
            signals.add(new EventSignalResponse(
                    asset.getSymbol(),
                    "Supply-chain disruption watch",
                    "Mining strikes, export restrictions, sanctions, shipping stress, or energy shortages can move metal supply expectations.",
                    List.of("supply", "industrial users", "input costs"),
                    "medium",
                    "Rule-based event intelligence"));
            signals.add(new EventSignalResponse(
                    asset.getSymbol(),
                    "Inflation and dollar narrative watch",
                    "Large changes in inflation expectations or dollar strength can change how investors interpret metal prices.",
                    List.of("USD price", "central banks", "consumer affordability"),
                    "medium",
                    "Rule-based event intelligence"));
        } else if (asset.getCategory() == AssetCategory.CRYPTOCURRENCY) {
            signals.add(new EventSignalResponse(
                    asset.getSymbol(),
                    "Regulatory and market-structure watch",
                    "Exchange rules, stablecoin regulation, ETF flows, network incidents, or enforcement actions can quickly change crypto sentiment.",
                    List.of("liquidity", "risk appetite", "market access"),
                    "medium",
                    "Rule-based event intelligence"));
            signals.add(new EventSignalResponse(
                    asset.getSymbol(),
                    "Liquidity stress watch",
                    "Sharp moves in rates, dollar funding, or stablecoin flows can affect speculative crypto demand.",
                    List.of("funding", "trading volume", "high-beta assets"),
                    "medium",
                    "Rule-based event intelligence"));
        } else {
            signals.add(new EventSignalResponse(
                    asset.getSymbol(),
                    "Central-bank decision watch",
                    "Rate decisions, inflation surprises, and policy guidance can shift currency expectations.",
                    List.of("exchange value", "import costs", "capital flows"),
                    "medium",
                    "Rule-based event intelligence"));
            signals.add(new EventSignalResponse(
                    asset.getSymbol(),
                    "Trade and geopolitics watch",
                    "Trade restrictions, energy shocks, sanctions, or regional tensions can change demand for safe or trade-linked currencies.",
                    List.of("trade", "remittances", "commodity purchasing power"),
                    "low",
                    "Rule-based event intelligence"));
        }

        return signals;
    }
}
