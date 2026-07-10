package com.RippleLens.intelligence;

import java.util.List;

public record MacroContextResponse(
        String symbol,
        String assetName,
        String category,
        String summary,
        List<MacroIndicatorResponse> indicators,
        List<String> pressureChannels,
        String disclaimer) {
}
