package com.RippleLens.insight;

import java.util.List;

import com.RippleLens.intelligence.EventSignalResponse;
import com.RippleLens.intelligence.MacroContextResponse;
import com.RippleLens.intelligence.NewsItemResponse;
import com.RippleLens.relationship.RelationshipReportResponse;
import com.RippleLens.trust.AssetTrustReportResponse;

public record InsightContext(
        AssetTrustReportResponse trustReport,
        MacroContextResponse macroContext,
        List<EventSignalResponse> eventSignals,
        List<NewsItemResponse> relatedNews,
        RelationshipReportResponse relationshipReport) {
}
