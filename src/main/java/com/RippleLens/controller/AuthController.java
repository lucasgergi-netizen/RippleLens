package com.RippleLens.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RippleLens.auth.AuthRequest;
import com.RippleLens.auth.AuthUserResponse;
import com.RippleLens.auth.RegisterRequest;
import com.RippleLens.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public AuthUserResponse currentUser() {
        return authService.currentUser();
    }

    @PostMapping("/register")
    public AuthUserResponse register(@RequestBody RegisterRequest request, HttpServletRequest servletRequest) {
        return authService.register(request, servletRequest);
    }

    @PostMapping("/login")
    public AuthUserResponse login(@RequestBody AuthRequest request, HttpServletRequest servletRequest) {
        return authService.login(request, servletRequest);
    }

    @PostMapping("/logout")
    public AuthUserResponse logout(HttpServletRequest servletRequest) {
        return authService.logout(servletRequest);
    }
}
