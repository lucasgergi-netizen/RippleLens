package com.RippleLens.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.RippleLens.alert.AlertResponse;
import com.RippleLens.alert.CreateAlertRequest;
import com.RippleLens.alert.UpdateAlertRequest;
import com.RippleLens.model.AlertStatus;
import com.RippleLens.service.TrackingService;
import com.RippleLens.watchlist.CreateWatchlistItemRequest;
import com.RippleLens.watchlist.WatchlistItemResponse;
import jakarta.validation.Valid;

@RestController
public class TrackingController {

    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @GetMapping("/api/watchlist")
    public List<WatchlistItemResponse> getWatchlist() {
        return trackingService.getWatchlist();
    }

    @PostMapping("/api/watchlist")
    @ResponseStatus(HttpStatus.CREATED)
    public WatchlistItemResponse addWatchlistItem(@Valid @RequestBody CreateWatchlistItemRequest request) {
        return trackingService.addWatchlistItem(request);
    }

    @DeleteMapping("/api/watchlist/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeWatchlistItem(@PathVariable Long id) {
        trackingService.removeWatchlistItem(id);
    }

    @GetMapping("/api/alerts")
    public List<AlertResponse> getAlerts(
            @RequestParam(required = false) String symbol,
            @RequestParam(required = false) AlertStatus status) {
        return trackingService.getAlerts(symbol, status);
    }

    @PostMapping("/api/alerts")
    @ResponseStatus(HttpStatus.CREATED)
    public AlertResponse createAlert(@Valid @RequestBody CreateAlertRequest request) {
        return trackingService.createAlert(request);
    }

    @PatchMapping("/api/alerts/{id}")
    public AlertResponse updateAlert(@PathVariable Long id, @Valid @RequestBody UpdateAlertRequest request) {
        return trackingService.updateAlert(id, request);
    }

    @PatchMapping("/api/alerts/{id}/enabled")
    public AlertResponse setAlertEnabled(@PathVariable Long id, @RequestParam boolean enabled) {
        return trackingService.setAlertEnabled(id, enabled);
    }

    @DeleteMapping("/api/alerts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlert(@PathVariable Long id) {
        trackingService.deleteAlert(id);
    }
}
