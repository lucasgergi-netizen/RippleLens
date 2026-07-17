package com.RippleLens.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@ConditionalOnExpression("${ripplelens.security.password-reset.delivery-enabled:false}"
        + " and '${ripplelens.security.password-reset.provider:none}' == 'resend'")
public class ResendPasswordResetDeliveryService implements PasswordResetDeliveryService {

    private static final URI ENDPOINT = URI.create("https://api.resend.com/emails");

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiKey;
    private final String fromAddress;

    public ResendPasswordResetDeliveryService(
            @Value("${ripplelens.security.password-reset.resend-api-key:}") String apiKey,
            @Value("${ripplelens.security.password-reset.from-address:}") String fromAddress) {
        this.apiKey = apiKey;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendResetLink(String email, String resetLink) {
        String body = jsonBody(email, resetLink);
        HttpRequest request = HttpRequest.newBuilder(ENDPOINT)
                .timeout(Duration.ofSeconds(10))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("User-Agent", "RippleLens/1.0")
                .header("Idempotency-Key", "password-reset-" + fingerprint(resetLink))
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();
        try {
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw deliveryFailure();
            }
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw deliveryFailure();
        } catch (java.io.IOException error) {
            throw deliveryFailure();
        }
    }

    private String jsonBody(String email, String resetLink) {
        String text = "A password reset was requested for your RippleLens account.\n\n"
                + resetLink
                + "\n\nThis link expires in 15 minutes and works only once. "
                + "If you did not request it, you can ignore this email.";
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "from", fromAddress,
                    "to", email,
                    "subject", "Reset your RippleLens password",
                    "text", text));
        } catch (JsonProcessingException error) {
            throw deliveryFailure();
        }
    }

    private String fingerprint(String resetLink) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(resetLink.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest).substring(0, 32);
        } catch (java.security.NoSuchAlgorithmException error) {
            throw deliveryFailure();
        }
    }

    private IllegalStateException deliveryFailure() {
        return new IllegalStateException("Password reset email delivery failed.");
    }
}
