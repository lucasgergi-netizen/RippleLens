package com.RippleLens.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.RippleLens.model.Commodity;
import com.RippleLens.repository.CommodityRepository;
import com.RippleLens.security.InputPolicy;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
        return commodityRepository.findBySymbolIgnoreCase(InputPolicy.assetSymbol(symbol))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown commodity symbol."));
    }
}
