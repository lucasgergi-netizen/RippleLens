package com.ripplelens.ripple;

import java.util.List;

public record RippleMapResponse(
        String metalSymbol,
        String title,
        List<RippleNode> nodes,
        List<RippleEdge> edges
) {
}
