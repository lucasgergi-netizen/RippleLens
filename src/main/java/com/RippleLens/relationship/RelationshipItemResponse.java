package com.RippleLens.relationship;

public record RelationshipItemResponse(
        String symbol,
        String name,
        String category,
        double correlation,
        String strength,
        String direction,
        String relationshipType,
        int pairedObservations,
        double selectedAssetMovePercentage,
        double relatedAssetMovePercentage,
        boolean unusualBehavior,
        String explanation) {
}
