package com.RippleLens.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeploymentHealthController {

    @GetMapping("/healthz")
    public Map<String, String> health() {
        return Map.of(
                "status", "ok",
                "app", "RippleLens");
    }
}
