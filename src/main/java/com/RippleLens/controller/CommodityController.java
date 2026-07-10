package com.RippleLens.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RippleLens.model.Commodity;
import com.RippleLens.service.CommodityService;

@RestController
@RequestMapping("/api/commodities")
public class CommodityController {

    private final CommodityService commodityService;

    public CommodityController(CommodityService commodityService) {
        this.commodityService = commodityService;
    }

    @GetMapping
    public List<Commodity> getAllCommodities() {
        return commodityService.getAllCommodities();
    }

    @GetMapping("/{symbol}")
    public Commodity getCommodityBySymbol(@PathVariable String symbol) {
        return commodityService.getCommodityBySymbol(symbol);
    }
}
