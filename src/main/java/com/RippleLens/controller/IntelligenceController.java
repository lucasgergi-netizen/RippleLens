package com.RippleLens.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RippleLens.intelligence.EventSignalResponse;
import com.RippleLens.intelligence.MacroContextResponse;
import com.RippleLens.intelligence.MacroIndicatorResponse;
import com.RippleLens.intelligence.MovementTimelineResponse;
import com.RippleLens.intelligence.NewsItemResponse;
import com.RippleLens.service.EventIntelligenceService;
import com.RippleLens.service.MacroContextService;
import com.RippleLens.service.MarketNewsService;
import com.RippleLens.service.MovementTimelineService;

@RestController
@RequestMapping("/api/intelligence")
public class IntelligenceController {

    private final MacroContextService macroContextService;
    private final EventIntelligenceService eventIntelligenceService;
    private final MarketNewsService marketNewsService;
    private final MovementTimelineService movementTimelineService;

    public IntelligenceController(
            MacroContextService macroContextService,
            EventIntelligenceService eventIntelligenceService,
            MarketNewsService marketNewsService,
            MovementTimelineService movementTimelineService) {
        this.macroContextService = macroContextService;
        this.eventIntelligenceService = eventIntelligenceService;
        this.marketNewsService = marketNewsService;
        this.movementTimelineService = movementTimelineService;
    }

    @GetMapping("/macro")
    public List<MacroIndicatorResponse> getMacroIndicators() {
        return macroContextService.getIndicators();
    }

    @GetMapping("/macro/{symbol}")
    public MacroContextResponse getMacroContext(@PathVariable String symbol) {
        return macroContextService.explainMacroContext(symbol);
    }

    @GetMapping("/events/{symbol}")
    public List<EventSignalResponse> getEventSignals(@PathVariable String symbol) {
        return eventIntelligenceService.getSignals(symbol);
    }

    @GetMapping("/news/{symbol}")
    public List<NewsItemResponse> getRelatedNews(@PathVariable String symbol) {
        return marketNewsService.getRelatedNews(symbol);
    }

    @GetMapping("/timeline/{symbol}")
    public MovementTimelineResponse getMovementTimeline(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "30") int days) {
        return movementTimelineService.buildTimeline(symbol, days);
    }
}
