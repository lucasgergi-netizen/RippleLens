package com.RippleLens.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.RippleLens.auth.AuthRequest;
import com.RippleLens.auth.AuthUserResponse;
import com.RippleLens.auth.ForgotPasswordRequest;
import com.RippleLens.auth.MessageResponse;
import com.RippleLens.auth.RegisterRequest;
import com.RippleLens.auth.ResetPasswordRequest;
import com.RippleLens.service.AuthService;
import com.RippleLens.service.PasswordResetService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.web.csrf.CsrfToken;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/me")
    public AuthUserResponse currentUser(CsrfToken csrfToken) {
        csrfToken.getToken();
        return authService.currentUser();
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MessageResponse register(@Valid @RequestBody RegisterRequest request, HttpServletRequest servletRequest) {
        return authService.register(request, servletRequest);
    }

    @PostMapping("/login")
    public AuthUserResponse login(
            @Valid @RequestBody AuthRequest request,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        return authService.login(request, servletRequest, servletResponse);
    }

    @PostMapping("/logout")
    public AuthUserResponse logout(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        return authService.logout(servletRequest, servletResponse);
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MessageResponse forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest servletRequest) {
        return passwordResetService.requestReset(request, servletRequest);
    }

    @PostMapping("/reset-password")
    public MessageResponse resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletRequest servletRequest) {
        return passwordResetService.resetPassword(request, servletRequest);
    }
}
