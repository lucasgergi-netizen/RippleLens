package com.RippleLens.service;

public record PasswordResetRequestedEvent(String email, String resetLink) {

    @Override
    public String toString() {
        return "PasswordResetRequestedEvent[redacted]";
    }
}
