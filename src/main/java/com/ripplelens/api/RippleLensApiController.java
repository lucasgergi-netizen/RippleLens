package com.ripplelens.api;

import com.ripplelens.asset.Asset;
import com.ripplelens.asset.AssetRepository;
import com.ripplelens.asset.AssetType;
import com.ripplelens.insight.InsightResponse;
import com.ripplelens.insight.InsightService;
import com.ripplelens.insight.MoneyPressureResponse;
import com.ripplelens.marketdata.MarketDataService;
import com.ripplelens.ripple.RippleMapResponse;
import com.ripplelens.ripple.RippleMapService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RippleLensApiController {

    private final AssetRepository assetRepository;
    private final InsightService insightService;
    private final RippleMapService rippleMapService;
    private final MarketDataService marketDataService;

    public RippleLensApiController(AssetRepository assetRepository, InsightService insightService, RippleMapService rippleMapService, MarketDataService marketDataService) {
        this.assetRepository = assetRepository;
        this.insightService = insightService;
        this.rippleMapService = rippleMapService;
        this.marketDataService = marketDataService;
    }

    @GetMapping("/assets")
    public List<AssetResponse> assets() {
        return assetRepository.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/metals")
    public List<AssetResponse> metals() {
        return assetRepository.findByType(AssetType.METAL).stream().map(this::toResponse).toList();
    }

    @GetMapping("/currencies/top")
    public List<AssetResponse> topCurrencies() {
        return assetRepository.findByType(AssetType.FIAT_CURRENCY).stream().map(this::toResponse).toList();
    }

    @GetMapping("/cryptos/top")
    public List<AssetResponse> topCryptos() {
        return assetRepository.findByType(AssetType.CRYPTO).stream().map(this::toResponse).toList();
    }

    @GetMapping("/assets/{symbol}/prices")
    public List<PricePointResponse> prices(@PathVariable String symbol) {
        Asset asset = assetRepository.findBySymbolIgnoreCase(symbol)
                .orElseThrow(() -> new IllegalArgumentException("Unknown asset symbol: " + symbol));

        var series = marketDataService.pricesFor(asset);

        return series.prices().stream()
                .map(price -> new PricePointResponse(price.observationDate(), price.priceUsd(), series.dataSource(), series.live()))
                .toList();
    }

    @GetMapping("/assets/{symbol}/insight")
    public InsightResponse insight(@PathVariable String symbol) {
        return insightService.explainAsset(symbol);
    }

    @GetMapping("/metals/{symbol}/money-pressure")
    public MoneyPressureResponse moneyPressure(@PathVariable String symbol) {
        return insightService.moneyPressureForMetal(symbol);
    }

    @GetMapping("/metals/{symbol}/ripple-map")
    public RippleMapResponse rippleMap(@PathVariable String symbol) {
        return rippleMapService.mapForMetal(symbol);
    }

    private AssetResponse toResponse(Asset asset) {
        return new AssetResponse(asset.getId(), asset.getSymbol(), asset.getName(), asset.getType(), asset.getDescription());
    }
}
