package com.RippleLens.ripple;

import java.util.List;

public record RippleMapResponse(
        String metalSymbol,
        String title,
        List<RippleEdge> edges,
        String disclaimer) {
}
