package com.RippleLens.relationship;

import java.util.List;

public record RelationshipReportResponse(
        String symbol,
        String assetName,
        int windowDays,
        int observationsUsed,
        String topRelationship,
        String summary,
        List<RelationshipItemResponse> relationships,
        List<String> insights,
        String disclaimer) {
}
