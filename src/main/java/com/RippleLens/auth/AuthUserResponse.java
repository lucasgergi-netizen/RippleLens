package com.RippleLens.auth;

public record AuthUserResponse(
        boolean authenticated,
        String fullName,
        String email,
        String sessionMode) {
}
