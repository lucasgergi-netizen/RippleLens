package com.RippleLens.live;

import java.time.Instant;
import java.util.List;

public record DataHealthSummaryResponse(
        Instant checkedAt,
        String overallStatus,
        int availableProviders,
        int providerCount,
        Instant lastIngestionRefreshAt,
        List<ProviderHealthResponse> providers) {
}
