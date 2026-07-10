package com.RippleLens.trust;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record AssetTrustReportResponse(
        String symbol,
        String assetName,
        String dataQuality,
        int dataTrustScore,
        String confidenceLevel,
        String sourceMode,
        boolean usesRealApi,
        int storedObservations,
        LocalDate firstObservationDate,
        LocalDate latestObservationDate,
        LocalDateTime lastUpdateTime,
        String latestObservationSource,
        List<String> sources,
        int realApiObservations,
        int fallbackObservations,
        int modeledObservations,
        long dataAgeDays,
        String freshnessStatus,
        String coverageSummary,
        List<String> caveats,
        String recommendation) {
}
