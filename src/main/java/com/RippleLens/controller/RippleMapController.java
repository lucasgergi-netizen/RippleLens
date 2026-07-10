package com.RippleLens.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RippleLens.ripple.RippleMapResponse;
import com.RippleLens.service.RippleMapService;

@RestController
@RequestMapping("/api/metals")
public class RippleMapController {

    private final RippleMapService rippleMapService;

    public RippleMapController(RippleMapService rippleMapService) {
        this.rippleMapService = rippleMapService;
    }

    @GetMapping("/{symbol}/ripple-map")
    public RippleMapResponse getRippleMap(@PathVariable String symbol) {
        return rippleMapService.getRippleMap(symbol);
    }
}
