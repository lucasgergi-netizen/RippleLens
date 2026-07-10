package com.ripplelens.api;

import com.ripplelens.asset.AssetType;

public record AssetResponse(
        Long id,
        String symbol,
        String name,
        AssetType type,
        String description
) {
}
