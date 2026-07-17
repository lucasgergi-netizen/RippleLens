package com.RippleLens.service;

import java.util.Locale;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;

@Service
public class CurrentUserService {

    private static final String GUEST_SESSION_KEY = CurrentUserService.class.getName() + ".GUEST_KEY";

    private final HttpServletRequest request;

    public CurrentUserService(HttpServletRequest request) {
        this.request = request;
    }

    public String sessionKey() {
        return currentEmail()
                .map(email -> "user:" + email)
                .orElseGet(this::guestSessionKey);
    }

    private String guestSessionKey() {
        HttpSession session = request.getSession(true);
        Object existing = session.getAttribute(GUEST_SESSION_KEY);
        if (existing instanceof String value && !value.isBlank()) {
            return "guest:" + value;
        }
        String value = UUID.randomUUID().toString();
        session.setAttribute(GUEST_SESSION_KEY, value);
        return "guest:" + value;
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
