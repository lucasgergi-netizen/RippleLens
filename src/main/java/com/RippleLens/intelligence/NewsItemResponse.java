package com.RippleLens.intelligence;

public record NewsItemResponse(
        String title,
        String description,
        String impactLevel,
        String priceEffect,
        String whyItMatters,
        String url,
        String source,
        String publishedAt,
        String sourceCountry) {
}
