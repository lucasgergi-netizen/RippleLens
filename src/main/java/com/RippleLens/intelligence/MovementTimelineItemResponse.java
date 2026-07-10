package com.RippleLens.intelligence;

public record MovementTimelineItemResponse(
        String type,
        String label,
        String title,
        String description,
        String timestamp,
        String impactLevel,
        String priceEffect,
        String source,
        String url) {
}
