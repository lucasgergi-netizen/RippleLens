package com.ripplelens.ripple;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RippleMapService {

    public RippleMapResponse mapForMetal(String symbol) {
        return switch (symbol.toUpperCase()) {
            case "XAU" -> goldMap();
            case "XAG" -> silverMap();
            case "XPT" -> platinumMap();
            case "XPD" -> palladiumMap();
            default -> genericMap(symbol.toUpperCase());
        };
    }

    private RippleMapResponse goldMap() {
        return new RippleMapResponse(
                "XAU",
                "Gold Causal Ripple Map",
                List.of(
                        new RippleNode("rates", "Interest-rate uncertainty", "macro"),
                        new RippleNode("usd", "US dollar confidence", "currency"),
                        new RippleNode("xau", "Gold price pressure", "metal"),
                        new RippleNode("jewelry", "Jewelry affordability", "consumer"),
                        new RippleNode("reserves", "Central-bank reserve attention", "government")
                ),
                List.of(
                        new RippleEdge("rates", "xau", "lower real yields can support gold", "upward pressure", "medium", "medium"),
                        new RippleEdge("usd", "xau", "weaker currency confidence can increase safe-haven demand", "upward pressure", "medium", "low"),
                        new RippleEdge("xau", "jewelry", "higher gold prices can reduce consumer affordability", "downward pressure", "medium", "medium"),
                        new RippleEdge("xau", "reserves", "higher gold attention can affect reserve-allocation debates", "attention increase", "weak", "low")
                )
        );
    }

    private RippleMapResponse silverMap() {
        return new RippleMapResponse(
                "XAG",
                "Silver Causal Ripple Map",
                List.of(
                        new RippleNode("solar", "Solar panel demand", "industry"),
                        new RippleNode("electronics", "Electronics production", "industry"),
                        new RippleNode("inflation", "Inflation expectations", "macro"),
                        new RippleNode("xag", "Silver price pressure", "metal"),
                        new RippleNode("cleanenergy", "Clean-energy input costs", "consumer")
                ),
                List.of(
                        new RippleEdge("solar", "xag", "solar demand can increase industrial demand for silver", "upward pressure", "medium", "medium"),
                        new RippleEdge("electronics", "xag", "electronics cycles can change industrial silver demand", "mixed pressure", "weak", "low"),
                        new RippleEdge("inflation", "xag", "inflation hedging can support precious metals", "upward pressure", "weak", "low"),
                        new RippleEdge("xag", "cleanenergy", "higher silver prices can raise solar input costs", "cost increase", "medium", "medium")
                )
        );
    }

    private RippleMapResponse platinumMap() {
        return new RippleMapResponse(
                "XPT",
                "Platinum Causal Ripple Map",
                List.of(
                        new RippleNode("southafrica", "South Africa mining supply", "country"),
                        new RippleNode("energy", "Mining energy costs", "macro"),
                        new RippleNode("auto", "Auto catalyst demand", "industry"),
                        new RippleNode("hydrogen", "Hydrogen technology demand", "industry"),
                        new RippleNode("xpt", "Platinum price pressure", "metal")
                ),
                List.of(
                        new RippleEdge("southafrica", "xpt", "supply disruption can tighten platinum availability", "upward pressure", "strong", "medium"),
                        new RippleEdge("energy", "xpt", "higher electricity costs can raise mining costs", "upward pressure", "medium", "medium"),
                        new RippleEdge("auto", "xpt", "vehicle production changes can affect catalyst demand", "mixed pressure", "medium", "low"),
                        new RippleEdge("hydrogen", "xpt", "hydrogen technology may increase future platinum attention", "attention increase", "weak", "low")
                )
        );
    }

    private RippleMapResponse palladiumMap() {
        return new RippleMapResponse(
                "XPD",
                "Palladium Causal Ripple Map",
                List.of(
                        new RippleNode("russia", "Russia supply risk", "country"),
                        new RippleNode("auto", "Gasoline vehicle production", "industry"),
                        new RippleNode("ev", "Electric vehicle adoption", "industry"),
                        new RippleNode("xpd", "Palladium price pressure", "metal"),
                        new RippleNode("substitution", "Metal substitution decisions", "business")
                ),
                List.of(
                        new RippleEdge("russia", "xpd", "geopolitical supply risk can support palladium prices", "upward pressure", "strong", "medium"),
                        new RippleEdge("auto", "xpd", "gasoline vehicle demand supports palladium catalyst demand", "upward pressure", "medium", "medium"),
                        new RippleEdge("ev", "xpd", "EV adoption can reduce long-term catalyst demand", "downward pressure", "medium", "medium"),
                        new RippleEdge("xpd", "substitution", "high palladium prices can push firms toward alternatives", "attention increase", "medium", "medium")
                )
        );
    }

    private RippleMapResponse genericMap(String symbol) {
        return new RippleMapResponse(
                symbol,
                symbol + " Causal Ripple Map",
                List.of(
                        new RippleNode("macro", "Macro conditions", "macro"),
                        new RippleNode("asset", symbol + " price pressure", "asset"),
                        new RippleNode("users", "Affected users and businesses", "impact")
                ),
                List.of(
                        new RippleEdge("macro", "asset", "economic signals may affect price pressure", "mixed pressure", "weak", "low"),
                        new RippleEdge("asset", "users", "price changes may affect costs and risk decisions", "mixed impact", "weak", "low")
                )
        );
    }
}
