package com.RippleLens.intelligence;

import java.util.List;

public record MovementTimelineResponse(
        String symbol,
        String assetName,
        int windowDays,
        String summary,
        List<MovementTimelineItemResponse> items,
        String disclaimer) {
}
