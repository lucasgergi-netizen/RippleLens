package com.RippleLens.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.RippleLens.live.CryptoPriceResponse;
import com.RippleLens.live.CurrencyRateResponse;
import com.RippleLens.live.DataHealthSummaryResponse;
import com.RippleLens.live.IngestionResult;
import com.RippleLens.live.LivePriceTickResponse;
import com.RippleLens.live.MetalPriceResponse;
import com.RippleLens.service.CoinGeckoClient;
import com.RippleLens.service.DataProviderHealthService;
import com.RippleLens.service.FrankfurterClient;
import com.RippleLens.service.GoldApiMetalsClient;
import com.RippleLens.service.LivePriceProviderService;
import com.RippleLens.service.LivePriceStreamService;
import com.RippleLens.service.MarketDataIngestionService;
import com.RippleLens.security.RateLimitExceededException;
import com.RippleLens.security.SecurityAuditService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/live")
public class LiveMarketController {

    private final CoinGeckoClient coinGeckoClient;
    private final FrankfurterClient frankfurterClient;
    private final GoldApiMetalsClient goldApiMetalsClient;
    private final DataProviderHealthService dataProviderHealthService;
    private final MarketDataIngestionService marketDataIngestionService;
    private final LivePriceProviderService livePriceProviderService;
    private final LivePriceStreamService livePriceStreamService;
    private final SecurityAuditService securityAuditService;

    public LiveMarketController(
            CoinGeckoClient coinGeckoClient,
            FrankfurterClient frankfurterClient,
            GoldApiMetalsClient goldApiMetalsClient,
            DataProviderHealthService dataProviderHealthService,
            MarketDataIngestionService marketDataIngestionService,
            LivePriceProviderService livePriceProviderService,
            LivePriceStreamService livePriceStreamService,
            SecurityAuditService securityAuditService) {
        this.coinGeckoClient = coinGeckoClient;
        this.frankfurterClient = frankfurterClient;
        this.goldApiMetalsClient = goldApiMetalsClient;
        this.dataProviderHealthService = dataProviderHealthService;
        this.marketDataIngestionService = marketDataIngestionService;
        this.livePriceProviderService = livePriceProviderService;
        this.livePriceStreamService = livePriceStreamService;
        this.securityAuditService = securityAuditService;
    }

    @GetMapping("/metals")
    public List<MetalPriceResponse> getLiveMetals() {
        return goldApiMetalsClient.getLiveMetalPrices();
    }

    @GetMapping("/cryptos")
    public List<CryptoPriceResponse> getLiveCryptos() {
        return coinGeckoClient.getLiveCryptoPrices();
    }

    @GetMapping("/currencies")
    public List<CurrencyRateResponse> getLiveCurrencies() {
        return frankfurterClient.getLiveCurrencyRates();
    }

    @PostMapping("/refresh")
    public IngestionResult refreshLiveData() {
        return marketDataIngestionService.refreshLiveData();
    }

    @GetMapping("/health")
    public DataHealthSummaryResponse getDataHealth() {
        return dataProviderHealthService.currentHealth();
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamLivePrices(HttpServletRequest request) {
        String clientKey = securityAuditService.fingerprint(request.getRemoteAddr());
        try {
            return livePriceStreamService.createEmitter(clientKey);
        } catch (RateLimitExceededException error) {
            securityAuditService.record(request, "rate_limit", "blocked_live_stream_capacity", clientKey);
            throw error;
        }
    }

    @GetMapping("/ticks")
    public List<LivePriceTickResponse> getLiveTicks() {
        return livePriceProviderService.getRealPriceBatch();
    }
}
