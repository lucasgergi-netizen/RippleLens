package com.RippleLens.live;

import java.time.Instant;

public record ProviderHealthResponse(
        String provider,
        String category,
        String status,
        long latencyMs,
        Instant checkedAt,
        String detail) {
}
