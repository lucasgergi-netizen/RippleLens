package com.RippleLens.service;

import java.util.Locale;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private static final String GUEST_SESSION = "default";

    public String sessionKey() {
        return currentEmail()
                .map(email -> "user:" + email)
                .orElse(GUEST_SESSION);
    }

    public java.util.Optional<String> currentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(authentication.getName().toLowerCase(Locale.ROOT));
    }
}
