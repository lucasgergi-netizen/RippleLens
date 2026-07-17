package com.RippleLens.security;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HexFormat;
import java.util.Locale;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class SecurityAuditService {

    private static final Logger audit = LoggerFactory.getLogger("SECURITY_AUDIT");
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final byte[] auditKey;

    public SecurityAuditService(@Value("${ripplelens.security.audit-key:local-development-audit-key-change-me}") String auditKey) {
        this.auditKey = auditKey.getBytes(StandardCharsets.UTF_8);
    }

    public void record(HttpServletRequest request, String event, String outcome, String subject) {
        audit.info("security_event event={} outcome={} subject={} ip={} request_id={}",
                safeLabel(event),
                safeLabel(outcome),
                fingerprint(subject),
                fingerprint(request.getRemoteAddr()),
                requestId(request));
    }

    public String fingerprint(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(auditKey, HMAC_ALGORITHM));
            byte[] digest = mac.doFinal(String.valueOf(value).toLowerCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest, 0, 12);
        } catch (GeneralSecurityException error) {
            throw new IllegalStateException("Security fingerprinting is unavailable.", error);
        }
    }

    private String requestId(HttpServletRequest request) {
        Object requestId = request.getAttribute(RequestIdFilter.REQUEST_ID_ATTRIBUTE);
        return requestId instanceof String value ? value : UUID.randomUUID().toString();
    }

    private String safeLabel(String value) {
        String normalized = String.valueOf(value).replaceAll("[^A-Za-z0-9_.-]", "_");
        return normalized.substring(0, Math.min(64, normalized.length()));
    }
}
