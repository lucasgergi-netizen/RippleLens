package com.RippleLens.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.RippleLens.insight.InsightContext;
import com.RippleLens.insight.InsightResponse;
import com.RippleLens.intelligence.EventSignalResponse;
import com.RippleLens.intelligence.MacroIndicatorResponse;
import com.RippleLens.intelligence.NewsItemResponse;
import com.RippleLens.model.Asset;
import com.RippleLens.model.AssetPricePoint;
import com.RippleLens.relationship.RelationshipItemResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class OpenAiAnalysisClient {

    private static final String RESPONSES_API_URL = "https://api.openai.com/v1/responses";

    private final String apiKey;
    private final String model;
    private final boolean enabled;
    private final Duration timeout;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OpenAiAnalysisClient(
            @Value("${openai.api-key:}") String apiKey,
            @Value("${openai.model:gpt-5.1}") String model,
            @Value("${openai.enabled:true}") boolean enabled,
            @Value("${openai.timeout-seconds:20}") int timeoutSeconds) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.model = model == null || model.isBlank() ? "gpt-5.1" : model.trim();
        this.enabled = enabled;
        this.timeout = Duration.ofSeconds(Math.max(5, timeoutSeconds));
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .build();
    }

    public Optional<InsightResponse> explain(
            Asset asset,
            List<AssetPricePoint> window,
            InsightResponse baseline,
            InsightContext context,
            int days) {
        if (!enabled || apiKey.isBlank()) {
            return Optional.empty();
        }

        try {
            String body = objectMapper.writeValueAsString(buildRequest(asset, window, baseline, context, days));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(RESPONSES_API_URL))
                    .timeout(timeout)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return Optional.empty();
            }

            String outputText = extractOutputText(objectMapper.readTree(response.body()));
            if (outputText.isBlank()) {
                return Optional.empty();
            }

            JsonNode aiInsight = objectMapper.readTree(outputText);
            String summary = cleanText(aiInsight.path("summary").asText(""));
            List<String> causes = readStringArray(aiInsight.path("possibleCauses"));
            List<String> consequences = readStringArray(aiInsight.path("possibleConsequences"));
            List<String> signals = readStringArray(aiInsight.path("supportingSignals"));
            String confidence = cleanText(aiInsight.path("confidence").asText(baseline.confidence()));

            if (summary.isBlank() || causes.isEmpty() || consequences.isEmpty()) {
                return Optional.empty();
            }

            List<String> combinedSignals = new ArrayList<>();
            combinedSignals.addAll(baseline.supportingSignals());
            combinedSignals.addAll(signals);
            combinedSignals.add("AI analysis engine: OpenAI Responses API using " + model + ".");

            return Optional.of(new InsightResponse(
                    baseline.symbol(),
                    baseline.assetName(),
                    baseline.category(),
                    baseline.latestPrice(),
                    baseline.selectedPeriodChangePercentage(),
                    baseline.movementDirection(),
                    confidence.isBlank() ? baseline.confidence() : confidence,
                    "AI-assisted economic explanation",
                    model,
                    summary,
                    causes,
                    consequences,
                    combinedSignals,
                    "Educational AI-assisted analysis only. RippleLens does not provide guaranteed forecasts or personal investment advice."));
        } catch (IOException | InterruptedException | RuntimeException error) {
            if (error instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return Optional.empty();
        }
    }

    private ObjectNode buildRequest(
            Asset asset,
            List<AssetPricePoint> window,
            InsightResponse baseline,
            InsightContext context,
            int days) {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("model", model);
        request.put("store", false);

        ObjectNode reasoning = request.putObject("reasoning");
        reasoning.put("effort", "low");

        ObjectNode text = request.putObject("text");
        text.put("verbosity", "low");
        text.set("format", insightSchema());

        ArrayNode input = request.putArray("input");
        ObjectNode system = input.addObject();
        system.put("role", "system");
        system.put("content", """
                You are the analysis engine for RippleLens, an educational finance and economics application.
                Explain plausible causes and consequences of asset price movement using only supplied data.
                Never make guaranteed predictions, personalized investment advice, or unsupported claims.
                Keep the tone professional, concise, and source-aware.
                """);

        ObjectNode user = input.addObject();
        user.put("role", "user");
        user.put("content", buildPrompt(asset, window, baseline, context, days));
        return request;
    }

    private ObjectNode insightSchema() {
        ObjectNode format = objectMapper.createObjectNode();
        format.put("type", "json_schema");
        format.put("name", "ripplelens_asset_insight");
        format.put("strict", true);

        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        schema.put("additionalProperties", false);

        ObjectNode properties = schema.putObject("properties");
        properties.set("summary", stringSchema("A concise paragraph explaining the asset move."));
        properties.set("confidence", enumSchema("low", "medium", "high"));
        properties.set("possibleCauses", stringArraySchema("Plausible causes grounded in the supplied data."));
        properties.set("possibleConsequences", stringArraySchema("Possible economic or market consequences."));
        properties.set("supportingSignals", stringArraySchema("Short source-aware signals used in the explanation."));

        ArrayNode required = schema.putArray("required");
        required.add("summary");
        required.add("confidence");
        required.add("possibleCauses");
        required.add("possibleConsequences");
        required.add("supportingSignals");

        format.set("schema", schema);
        return format;
    }

    private ObjectNode stringSchema(String description) {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "string");
        schema.put("description", description);
        return schema;
    }

    private ObjectNode enumSchema(String... values) {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "string");
        ArrayNode enumValues = schema.putArray("enum");
        for (String value : values) {
            enumValues.add(value);
        }
        return schema;
    }

    private ObjectNode stringArraySchema(String description) {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "array");
        schema.put("description", description);
        schema.put("minItems", 3);
        schema.put("maxItems", 5);
        ObjectNode items = schema.putObject("items");
        items.put("type", "string");
        return schema;
    }

    private String buildPrompt(
            Asset asset,
            List<AssetPricePoint> window,
            InsightResponse baseline,
            InsightContext context,
            int days) {
        StringBuilder history = new StringBuilder();
        int start = Math.max(0, window.size() - 8);
        for (int index = start; index < window.size(); index++) {
            AssetPricePoint point = window.get(index);
            history.append(point.getObservationDate())
                    .append(": ")
                    .append(formatPrice(point.getPrice()))
                    .append(" ")
                    .append(asset.getQuoteCurrency())
                    .append('\n');
        }
        String trustBlock = context.trustReport() == null
                ? "No trust report available."
                : """
                Trust score: %d/100
                Source mode: %s
                Freshness: %s
                Coverage: %s
                Latest source: %s
                """.formatted(
                        context.trustReport().dataTrustScore(),
                        context.trustReport().sourceMode(),
                        context.trustReport().freshnessStatus(),
                        context.trustReport().coverageSummary(),
                        context.trustReport().latestObservationSource());

        String macroBlock = context.macroContext() == null
                ? "No macro context available."
                : """
                Macro summary: %s
                Macro channels: %s
                Macro indicators: %s
                """.formatted(
                        context.macroContext().summary(),
                        String.join("; ", context.macroContext().pressureChannels()),
                        macroIndicators(context));

        String relationshipBlock = context.relationshipReport() == null
                ? "No relationship context available."
                : """
                Relationship summary: %s
                Top relationship: %s
                Relationship observations: %d
                Relationship details: %s
                """.formatted(
                        context.relationshipReport().summary(),
                        context.relationshipReport().topRelationship(),
                        context.relationshipReport().observationsUsed(),
                        relationshipDetails(context));

        String eventBlock = context.eventSignals().isEmpty()
                ? "No event-watch signals available."
                : eventSignals(context);

        String newsBlock = context.relatedNews().isEmpty()
                ? "No recent related news was retrieved."
                : newsSignals(context);

        return """
                Asset: %s (%s)
                Category: %s
                Latest price: %s %s per %s
                Daily change from data feed: %s%%
                Selected window: %d days
                Selected-period change: %.2f%%
                Direction: %s
                Stored observations in selected window: %d
                Asset description: %s

                Recent price points:
                %s
                Existing RippleLens rule-based summary:
                %s

                Rule-based possible causes:
                %s

                Rule-based possible consequences:
                %s

                Data trust context:
                %s

                Macro context:
                %s

                Relationship/correlation context:
                %s

                Event-watch signals:
                %s

                Recent related news headlines:
                %s

                Required behavior:
                - Improve the explanation using the supplied trust, macro, event, news, and relationship context.
                - Do not invent live news or facts not shown above.
                - Mention if the strongest evidence is only contextual rather than causal.
                - If the data is thin, say confidence is low.
                - Explain causes and consequences, not buy/sell instructions.
                - Keep the summary under 95 words.
                """.formatted(
                asset.getName(),
                asset.getSymbol(),
                asset.getCategory().name(),
                formatPrice(asset.getCurrentPrice()),
                asset.getQuoteCurrency(),
                asset.getUnit(),
                formatPrice(asset.getDailyChangePercentage()),
                days,
                baseline.selectedPeriodChangePercentage(),
                baseline.movementDirection(),
                window.size(),
                asset.getDescription(),
                history,
                baseline.summary(),
                String.join("; ", baseline.possibleCauses()),
                String.join("; ", baseline.possibleConsequences()),
                trustBlock,
                macroBlock,
                relationshipBlock,
                eventBlock,
                newsBlock);
    }

    private String macroIndicators(InsightContext context) {
        List<String> values = new ArrayList<>();
        for (MacroIndicatorResponse indicator : context.macroContext().indicators()) {
            values.add(indicator.name() + ": " + formatPrice(indicator.value()) + indicator.unit()
                    + " (" + indicator.country() + ", " + indicator.year() + ")");
            if (values.size() >= 4) {
                break;
            }
        }
        return values.isEmpty() ? "none" : String.join("; ", values);
    }

    private String relationshipDetails(InsightContext context) {
        List<String> values = new ArrayList<>();
        for (RelationshipItemResponse item : context.relationshipReport().relationships()) {
            values.add(item.symbol() + " " + item.strength() + " " + item.direction()
                    + " correlation " + item.correlation()
                    + ", unusual=" + item.unusualBehavior());
            if (values.size() >= 3) {
                break;
            }
        }
        return values.isEmpty() ? "none" : String.join("; ", values);
    }

    private String eventSignals(InsightContext context) {
        List<String> values = new ArrayList<>();
        for (EventSignalResponse signal : context.eventSignals()) {
            values.add(signal.title() + ": " + signal.description()
                    + " Confidence=" + signal.confidence());
            if (values.size() >= 3) {
                break;
            }
        }
        return String.join("\n", values);
    }

    private String newsSignals(InsightContext context) {
        List<String> values = new ArrayList<>();
        for (NewsItemResponse news : context.relatedNews()) {
            values.add(news.title()
                    + " | " + news.source()
                    + " | " + news.publishedAt()
                    + " | " + news.impactLevel()
                    + " | " + news.priceEffect()
                    + " | why: " + news.whyItMatters());
            if (values.size() >= 4) {
                break;
            }
        }
        return String.join("\n", values);
    }

    private String extractOutputText(JsonNode root) {
        String directOutput = root.path("output_text").asText("");
        if (!directOutput.isBlank()) {
            return directOutput;
        }

        StringBuilder output = new StringBuilder();
        JsonNode outputItems = root.path("output");
        if (!outputItems.isArray()) {
            return "";
        }

        for (JsonNode item : outputItems) {
            JsonNode contentItems = item.path("content");
            if (!contentItems.isArray()) {
                continue;
            }
            for (JsonNode content : contentItems) {
                String text = content.path("text").asText("");
                if (!text.isBlank()) {
                    output.append(text);
                }
            }
        }
        return output.toString();
    }

    private List<String> readStringArray(JsonNode node) {
        if (!node.isArray()) {
            return List.of();
        }

        List<String> values = new ArrayList<>();
        for (JsonNode item : node) {
            String value = cleanText(item.asText(""));
            if (!value.isBlank()) {
                values.add(value);
            }
        }
        return values;
    }

    private String cleanText(String value) {
        return value == null ? "" : value.replaceAll("\\s+", " ").trim();
    }

    private String formatPrice(BigDecimal value) {
        return value == null ? "unknown" : value.stripTrailingZeros().toPlainString();
    }
}
