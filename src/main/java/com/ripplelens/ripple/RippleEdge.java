package com.ripplelens.ripple;

public record RippleEdge(
        String fromNodeId,
        String toNodeId,
        String relationship,
        String direction,
        String strength,
        String confidence
) {
}
