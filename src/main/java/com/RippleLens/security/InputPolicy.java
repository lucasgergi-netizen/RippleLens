package com.RippleLens.security;

import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class InputPolicy {

    private InputPolicy() {
    }

    public static String assetSymbol(String symbol) {
        if (symbol == null || !symbol.trim().matches("^[A-Za-z0-9_]{1,32}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid asset symbol.");
        }
        return symbol.trim().toUpperCase(Locale.ROOT);
    }
}
