package com.RippleLens.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.RippleLens.ripple.RippleEdge;
import com.RippleLens.ripple.RippleMapResponse;

@Service
public class RippleMapService {

    public RippleMapResponse getRippleMap(String symbol) {
        String normalizedSymbol = symbol.toUpperCase();

        return switch (normalizedSymbol) {
            case "XAU", "GOLD" -> goldMap();
            case "XAG", "SILVER" -> silverMap();
            case "XPT", "PLATINUM" -> platinumMap();
            case "XPD", "PALLADIUM" -> palladiumMap();
            case "XCU", "COPPER" -> copperMap();
            case "BTC", "BITCOIN" -> bitcoinMap();
            case "ETH", "ETHEREUM" -> ethereumMap();
            case "SOL", "SOLANA" -> solanaMap();
            case "USD" -> usdMap();
            case "EUR" -> eurMap();
            case "GBP" -> gbpMap();
            case "AED" -> aedMap();
            default -> genericMap(normalizedSymbol);
        };
    }

    private RippleMapResponse goldMap() {
        return new RippleMapResponse(
                "XAU",
                "Gold Causal Ripple Map",
                List.of(
                        new RippleEdge("Interest-rate uncertainty", "Gold price pressure",
                                "Lower real yields can make gold more attractive compared with interest-paying assets.",
                                "upward pressure", "medium", "medium"),
                        new RippleEdge("US dollar weakness", "Gold price pressure",
                                "Gold is priced globally in dollars, so dollar weakness can lift the USD gold price.",
                                "upward pressure", "medium", "medium"),
                        new RippleEdge("Gold price pressure", "Jewelry affordability",
                                "Higher gold prices can make jewelry less affordable for consumers.",
                                "cost pressure", "medium", "medium"),
                        new RippleEdge("Gold price pressure", "Central-bank reserve attention",
                                "Large gold moves can increase attention on reserve diversification.",
                                "attention increase", "weak", "low")),
                "RippleLens explains possible causes and consequences. This is not financial advice.");
    }

    private RippleMapResponse silverMap() {
        return new RippleMapResponse(
                "XAG",
                "Silver Causal Ripple Map",
                List.of(
                        new RippleEdge("Solar panel demand", "Silver price pressure",
                                "Silver is used in solar technology, so clean-energy demand can support silver.",
                                "upward pressure", "medium", "medium"),
                        new RippleEdge("Electronics production", "Silver price pressure",
                                "Electronics demand can affect industrial silver consumption.",
                                "mixed pressure", "weak", "low"),
                        new RippleEdge("Silver price pressure", "Clean-energy input costs",
                                "Higher silver prices can raise solar and electronics input costs.",
                                "cost pressure", "medium", "medium")),
                "RippleLens explains possible causes and consequences. This is not financial advice.");
    }

    private RippleMapResponse platinumMap() {
        return new RippleMapResponse(
                "XPT",
                "Platinum Causal Ripple Map",
                List.of(
                        new RippleEdge("South Africa mining supply", "Platinum price pressure",
                                "Supply disruptions in a major producing country can tighten platinum availability.",
                                "upward pressure", "strong", "medium"),
                        new RippleEdge("Mining energy costs", "Platinum price pressure",
                                "Higher electricity costs can raise mining and refining costs.",
                                "upward pressure", "medium", "medium"),
                        new RippleEdge("Platinum price pressure", "Auto catalyst costs",
                                "Platinum price changes can affect vehicle component costs.",
                                "cost pressure", "medium", "medium")),
                "RippleLens explains possible causes and consequences. This is not financial advice.");
    }

    private RippleMapResponse palladiumMap() {
        return new RippleMapResponse(
                "XPD",
                "Palladium Causal Ripple Map",
                List.of(
                        new RippleEdge("Russia supply risk", "Palladium price pressure",
                                "Geopolitical supply risk can support palladium prices.",
                                "upward pressure", "strong", "medium"),
                        new RippleEdge("Gasoline vehicle production", "Palladium price pressure",
                                "Gasoline vehicle demand supports palladium catalyst demand.",
                                "upward pressure", "medium", "medium"),
                        new RippleEdge("Electric vehicle adoption", "Palladium demand",
                                "EV adoption can reduce long-term demand for catalytic converter metals.",
                                "downward pressure", "medium", "medium")),
                "RippleLens explains possible causes and consequences. This is not financial advice.");
    }

    private RippleMapResponse copperMap() {
        return new RippleMapResponse(
                "XCU",
                "Copper Causal Ripple Map",
                List.of(
                        new RippleEdge("Construction demand", "Copper price pressure",
                                "Construction and infrastructure activity can increase demand for copper wiring and pipes.",
                                "upward pressure", "medium", "medium"),
                        new RippleEdge("Electrification demand", "Copper price pressure",
                                "Electric grids, EVs, and renewable infrastructure can raise long-term copper demand.",
                                "upward pressure", "strong", "medium"),
                        new RippleEdge("Copper price pressure", "Industrial input costs",
                                "Higher copper prices can raise costs for electrical equipment and construction.",
                                "cost pressure", "medium", "medium")),
                "RippleLens explains possible causes and consequences. This is not financial advice.");
    }

    private RippleMapResponse bitcoinMap() {
        return new RippleMapResponse(
                "BTC",
                "Bitcoin Causal Ripple Map",
                List.of(
                        new RippleEdge("Global liquidity expectations", "Bitcoin price pressure",
                                "Easier liquidity conditions can increase demand for speculative and scarce digital assets.",
                                "upward pressure", "medium", "low"),
                        new RippleEdge("Crypto risk appetite", "Bitcoin price pressure",
                                "When investors accept more risk, bitcoin can attract more speculative flows.",
                                "upward pressure", "strong", "medium"),
                        new RippleEdge("Bitcoin price pressure", "Gold comparison narrative",
                                "Large bitcoin moves can revive comparisons between digital scarcity and gold.",
                                "attention increase", "medium", "low")),
                "RippleLens explains possible causes and consequences. This is not financial advice.");
    }

    private RippleMapResponse ethereumMap() {
        return new RippleMapResponse(
                "ETH",
                "Ethereum Causal Ripple Map",
                List.of(
                        new RippleEdge("Smart-contract activity", "Ethereum price pressure",
                                "Higher network and application activity can support demand for Ethereum.",
                                "upward pressure", "medium", "medium"),
                        new RippleEdge("Crypto liquidity", "Ethereum price pressure",
                                "Broad crypto liquidity can influence Ethereum alongside other major crypto assets.",
                                "mixed pressure", "medium", "low"),
                        new RippleEdge("Ethereum price pressure", "DeFi and app sentiment",
                                "Ethereum moves can affect confidence in decentralized finance and application ecosystems.",
                                "sentiment impact", "medium", "low")),
                "RippleLens explains possible causes and consequences. This is not financial advice.");
    }

    private RippleMapResponse solanaMap() {
        return new RippleMapResponse(
                "SOL",
                "Solana Causal Ripple Map",
                List.of(
                        new RippleEdge("High-beta crypto sentiment", "Solana price pressure",
                                "Solana can react strongly when speculative crypto risk appetite changes.",
                                "mixed pressure", "strong", "medium"),
                        new RippleEdge("Network activity", "Solana price pressure",
                                "Application usage and network reliability narratives can affect Solana demand.",
                                "mixed pressure", "medium", "low"),
                        new RippleEdge("Solana price pressure", "Speculative market sentiment",
                                "Large Solana moves can signal broader appetite for high-risk crypto assets.",
                                "sentiment impact", "medium", "low")),
                "RippleLens explains possible causes and consequences. This is not financial advice.");
    }

    private RippleMapResponse usdMap() {
        return new RippleMapResponse(
                "USD",
                "US Dollar Causal Ripple Map",
                List.of(
                        new RippleEdge("US interest-rate expectations", "US dollar strength",
                                "Higher expected US rates can support demand for dollars.",
                                "upward pressure", "strong", "medium"),
                        new RippleEdge("US dollar strength", "USD metal prices",
                                "A stronger dollar can pressure commodities priced globally in USD.",
                                "downward pressure", "medium", "medium"),
                        new RippleEdge("US dollar strength", "Emerging-market pressure",
                                "A stronger dollar can increase pressure on borrowers and importers with dollar exposure.",
                                "cost pressure", "medium", "medium")),
                "RippleLens explains possible causes and consequences. This is not financial advice.");
    }

    private RippleMapResponse eurMap() {
        return new RippleMapResponse(
                "EUR",
                "Euro Causal Ripple Map",
                List.of(
                        new RippleEdge("ECB rate expectations", "Euro exchange value",
                                "Expected European Central Bank policy can influence euro strength or weakness.",
                                "mixed pressure", "medium", "medium"),
                        new RippleEdge("Energy and growth outlook", "Euro exchange value",
                                "European growth and energy conditions can affect currency confidence.",
                                "mixed pressure", "medium", "low"),
                        new RippleEdge("Euro exchange value", "Metals priced in euros",
                                "Currency changes can make the same metal move look different for euro-based users.",
                                "purchasing-power impact", "medium", "medium")),
                "RippleLens explains possible causes and consequences. This is not financial advice.");
    }

    private RippleMapResponse gbpMap() {
        return new RippleMapResponse(
                "GBP",
                "British Pound Causal Ripple Map",
                List.of(
                        new RippleEdge("Bank of England expectations", "Pound exchange value",
                                "Expected UK monetary policy can influence sterling strength or weakness.",
                                "mixed pressure", "medium", "medium"),
                        new RippleEdge("UK inflation and growth", "Pound exchange value",
                                "Inflation and growth surprises can change currency confidence.",
                                "mixed pressure", "medium", "low"),
                        new RippleEdge("Pound exchange value", "UK import costs",
                                "Pound weakness can increase import costs, including commodities priced in USD.",
                                "cost pressure", "medium", "medium")),
                "RippleLens explains possible causes and consequences. This is not financial advice.");
    }

    private RippleMapResponse aedMap() {
        return new RippleMapResponse(
                "AED",
                "UAE Dirham Causal Ripple Map",
                List.of(
                        new RippleEdge("US dollar peg", "UAE dirham exchange value",
                                "The dirham is closely linked to the US dollar, so US dollar strength strongly shapes AED value versus other currencies.",
                                "stability anchor", "strong", "medium"),
                        new RippleEdge("Oil and Gulf trade flows", "UAE liquidity",
                                "Regional trade, tourism, energy, and capital flows can affect liquidity conditions around the dirham.",
                                "mixed pressure", "medium", "low"),
                        new RippleEdge("UAE dirham exchange value", "Remittances and import purchasing power",
                                "AED movements against non-dollar currencies can affect remittance value and imported goods affordability.",
                                "purchasing-power impact", "medium", "medium")),
                "RippleLens explains possible causes and consequences. This is not financial advice.");
    }

    private RippleMapResponse genericMap(String symbol) {
        return new RippleMapResponse(
                symbol,
                symbol + " Causal Ripple Map",
                List.of(
                        new RippleEdge("Macroeconomic conditions", symbol + " price pressure",
                                "Inflation, rates, currencies, and global events may affect price pressure.",
                                "mixed pressure", "weak", "low"),
                        new RippleEdge(symbol + " price pressure", "Consumers and businesses",
                                "Price changes may affect costs, risk decisions, and affordability.",
                                "mixed impact", "weak", "low")),
                "RippleLens explains possible causes and consequences. This is not financial advice.");
    }
}
