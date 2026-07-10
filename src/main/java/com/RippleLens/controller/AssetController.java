package com.RippleLens.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RippleLens.model.Asset;
import com.RippleLens.model.AssetCategory;
import com.RippleLens.model.AssetPricePoint;
import com.RippleLens.price.PriceSeriesPointResponse;
import com.RippleLens.service.AssetService;
import com.RippleLens.trust.AssetTrustReportResponse;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping
    public List<Asset> getAllAssets() {
        return assetService.getAllAssets();
    }

    @GetMapping("/category/{category}")
    public List<Asset> getAssetsByCategory(@PathVariable AssetCategory category) {
        return assetService.getAssetsByCategory(category);
    }

    @GetMapping("/{symbol}")
    public Asset getAssetBySymbol(@PathVariable String symbol) {
        return assetService.getAssetBySymbol(symbol);
    }

    @GetMapping("/{symbol}/prices")
    public List<AssetPricePoint> getPriceHistory(@PathVariable String symbol) {
        return assetService.getPriceHistory(symbol);
    }

    @GetMapping("/{symbol}/prices/hourly")
    public List<PriceSeriesPointResponse> getHourlyPriceHistory(@PathVariable String symbol) {
        return assetService.getHourlyPriceHistory(symbol);
    }

    @GetMapping("/{symbol}/trust")
    public AssetTrustReportResponse getTrustReport(@PathVariable String symbol) {
        return assetService.getTrustReport(symbol);
    }
}
