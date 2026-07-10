package com.RippleLens.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.RippleLens.portfolio.CreatePortfolioHoldingRequest;
import com.RippleLens.portfolio.PortfolioResponse;
import com.RippleLens.portfolio.UpdatePortfolioHoldingRequest;
import com.RippleLens.service.PortfolioService;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public PortfolioResponse getPortfolio() {
        return portfolioService.getPortfolio();
    }

    @PostMapping("/holdings")
    @ResponseStatus(HttpStatus.CREATED)
    public PortfolioResponse saveHolding(@RequestBody CreatePortfolioHoldingRequest request) {
        return portfolioService.saveHolding(request);
    }

    @PatchMapping("/holdings/{id}")
    public PortfolioResponse updateHolding(
            @PathVariable Long id,
            @RequestBody UpdatePortfolioHoldingRequest request) {
        return portfolioService.updateHolding(id, request);
    }

    @DeleteMapping("/holdings/{id}")
    public PortfolioResponse deleteHolding(@PathVariable Long id) {
        return portfolioService.deleteHolding(id);
    }
}
