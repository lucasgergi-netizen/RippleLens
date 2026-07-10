package com.RippleLens.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RippleLens.insight.InsightResponse;
import com.RippleLens.service.InsightService;

@RestController
@RequestMapping("/api/insights")
public class InsightController {

    private final InsightService insightService;

    public InsightController(InsightService insightService) {
        this.insightService = insightService;
    }

    @GetMapping("/{symbol}")
    public InsightResponse explain(@PathVariable String symbol, @RequestParam(defaultValue = "30") int days) {
        return insightService.explain(symbol, days);
    }
}
