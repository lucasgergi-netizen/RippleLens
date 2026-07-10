package com.RippleLens.ripple;

public record RippleEdge(
        String from,
        String to,
        String relationship,
        String direction,
        String strength,
        String confidence) {
}
