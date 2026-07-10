package com.RippleLens.intelligence;

import java.util.List;

public record EventSignalResponse(
        String symbol,
        String title,
        String description,
        List<String> affectedAreas,
        String confidence,
        String sourceType) {
}
