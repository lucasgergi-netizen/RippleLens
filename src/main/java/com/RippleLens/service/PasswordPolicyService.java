package com.RippleLens.service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PasswordPolicyService {

    private static final Set<String> BLOCKED_PASSWORDS = Set.of(
            "password1234", "password123!", "qwerty123456", "123456789012", "letmein123456");

    public void validateNewPassword(String password, String email, String fullName) {
        if (password == null || password.length() < 12 || password.length() > 128
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw invalid();
        }
        String normalized = password.toLowerCase(Locale.ROOT);
        String emailPrefix = email == null ? "" : email.split("@", 2)[0].toLowerCase(Locale.ROOT);
        String compactName = fullName == null ? "" : fullName.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
        if (BLOCKED_PASSWORDS.contains(normalized)
                || normalized.chars().distinct().count() < 4
                || (!emailPrefix.isBlank() && emailPrefix.length() >= 4 && normalized.contains(emailPrefix))
                || (!compactName.isBlank() && compactName.length() >= 4 && normalized.contains(compactName))) {
            throw invalid();
        }
    }

    private ResponseStatusException invalid() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Use a password of at least 12 characters and at most 72 UTF-8 bytes that is not based on your name or email.");
    }
}
