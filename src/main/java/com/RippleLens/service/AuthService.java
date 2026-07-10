package com.RippleLens.service;

import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.RippleLens.auth.AuthRequest;
import com.RippleLens.auth.AuthUserResponse;
import com.RippleLens.auth.RegisterRequest;
import com.RippleLens.model.AppUser;
import com.RippleLens.repository.AppUserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CurrentUserService currentUserService;

    public AuthService(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            CurrentUserService currentUserService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.currentUserService = currentUserService;
    }

    public AuthUserResponse currentUser() {
        return currentUserService.currentEmail()
                .flatMap(userRepository::findByEmailIgnoreCase)
                .map(this::toResponse)
                .orElse(new AuthUserResponse(false, "Guest", null, "Guest mode"));
    }

    @Transactional
    public AuthUserResponse register(RegisterRequest request, HttpServletRequest servletRequest) {
        String email = normalizeEmail(request.email());
        String fullName = normalizeFullName(request.fullName());
        validatePassword(request.password());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account already exists for this email.");
        }

        AppUser user = userRepository.save(new AppUser(fullName, email, passwordEncoder.encode(request.password())));
        authenticate(email, request.password(), servletRequest);
        return toResponse(user);
    }

    public AuthUserResponse login(AuthRequest request, HttpServletRequest servletRequest) {
        String email = normalizeEmail(request.email());
        validatePassword(request.password());
        authenticate(email, request.password(), servletRequest);
        AppUser user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password."));
        return toResponse(user);
    }

    public AuthUserResponse logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return new AuthUserResponse(false, "Guest", null, "Guest mode");
    }

    private void authenticate(String email, String password, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            request.getSession(true).setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    context);
        } catch (RuntimeException error) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }
    }

    private AuthUserResponse toResponse(AppUser user) {
        return new AuthUserResponse(true, user.getFullName(), user.getEmail(), "Signed in");
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid email is required.");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required.");
        }
        return fullName.trim();
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters.");
        }
    }
}
