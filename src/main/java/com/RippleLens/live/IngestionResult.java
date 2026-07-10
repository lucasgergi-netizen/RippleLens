package com.RippleLens.live;

import java.time.Instant;
import java.util.List;

public record IngestionResult(
        Instant updatedAt,
        int metalAssetsUpdated,
        int cryptoAssetsUpdated,
        int currencyAssetsUpdated,
        List<String> messages) {
}
