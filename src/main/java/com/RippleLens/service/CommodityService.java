package com.RippleLens.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.RippleLens.model.Commodity;
import com.RippleLens.repository.CommodityRepository;

@Service
public class CommodityService {

    private final CommodityRepository commodityRepository;

    public CommodityService(
            CommodityRepository commodityRepository) {

        this.commodityRepository = commodityRepository;
    }

    public List<Commodity> getAllCommodities() {
        return commodityRepository.findAll();
    }

    public Commodity getCommodityBySymbol(String symbol) {
        return commodityRepository.findBySymbolIgnoreCase(symbol)
                .orElseThrow(() -> new IllegalArgumentException("Unknown commodity symbol: " + symbol));
    }
}
