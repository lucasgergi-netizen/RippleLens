package com.RippleLens.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.RippleLens.relationship.RelationshipReportResponse;
import com.RippleLens.service.RelationshipEngineService;

@RestController
@RequestMapping("/api/relationships")
public class RelationshipController {

    private final RelationshipEngineService relationshipEngineService;

    public RelationshipController(RelationshipEngineService relationshipEngineService) {
        this.relationshipEngineService = relationshipEngineService;
    }

    @GetMapping("/{symbol}")
    public RelationshipReportResponse analyze(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "30") int days) {
        return relationshipEngineService.analyze(symbol, days);
    }
}
