package com.RippleLens.service;

import java.util.Locale;
import java.time.Duration;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.RippleLens.auth.AuthRequest;
import com.RippleLens.auth.AuthUserResponse;
import com.RippleLens.auth.MessageResponse;
import com.RippleLens.auth.RegisterRequest;
import com.RippleLens.model.AppUser;
import com.RippleLens.repository.AppUserRepository;
import com.RippleLens.security.RateLimitService;
import com.RippleLens.security.SecurityAuditService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Service
public class AuthService {

    private static final int LOCKOUT_THRESHOLD = 5;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(15);
    private static final String REGISTRATION_MESSAGE =
            "If this email can be registered, the account is ready. You can now sign in.";

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CurrentUserService currentUserService;
    private final PasswordPolicyService passwordPolicyService;
    private final RateLimitService rateLimitService;
    private final SecurityAuditService auditService;
    private final SessionAuthenticationStrategy sessionAuthenticationStrategy;
    private final SecurityContextRepository securityContextRepository;

    public AuthService(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            CurrentUserService currentUserService,
            PasswordPolicyService passwordPolicyService,
            RateLimitService rateLimitService,
            SecurityAuditService auditService,
            SessionAuthenticationStrategy sessionAuthenticationStrategy,
            SecurityContextRepository securityContextRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.currentUserService = currentUserService;
        this.passwordPolicyService = passwordPolicyService;
        this.rateLimitService = rateLimitService;
        this.auditService = auditService;
        this.sessionAuthenticationStrategy = sessionAuthenticationStrategy;
        this.securityContextRepository = securityContextRepository;
    }

    public AuthUserResponse currentUser() {
        return currentUserService.currentEmail()
                .flatMap(userRepository::findByEmailIgnoreCase)
                .map(this::toResponse)
                .orElse(new AuthUserResponse(false, "Guest", null, "Guest mode"));
    }

    @Transactional
    public MessageResponse register(RegisterRequest request, HttpServletRequest servletRequest) {
        String email = normalizeEmail(request.email());
        String fullName = normalizeFullName(request.fullName());
        passwordPolicyService.validateNewPassword(request.password(), email, fullName);
        rateLimitService.check("register_subject", auditService.fingerprint(email), 3, Duration.ofHours(1));
        var existing = userRepository.findByEmailIgnoreCase(email);
        if (existing.isPresent()) {
            passwordEncoder.matches(request.password(), existing.get().getPasswordHash());
            auditService.record(servletRequest, "registration", "accepted_existing", email);
            return new MessageResponse(REGISTRATION_MESSAGE);
        }

        userRepository.save(new AppUser(fullName, email, passwordEncoder.encode(request.password())));
        auditService.record(servletRequest, "registration", "created", email);
        return new MessageResponse(REGISTRATION_MESSAGE);
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public AuthUserResponse login(AuthRequest request, HttpServletRequest servletRequest, HttpServletResponse response) {
        String email = normalizeEmail(request.email());
        validatePassword(request.password());
        rateLimitService.check("login_subject", auditService.fingerprint(email), 8, Duration.ofMinutes(15));
        AppUser candidate = userRepository.findForLogin(email).orElse(null);
        if (candidate != null && isLocked(candidate)) {
            passwordEncoder.matches(request.password(), candidate.getPasswordHash());
            auditService.record(servletRequest, "login", "rejected", email);
            throw invalidCredentials();
        }
        if (candidate != null && candidate.getLockedUntil() != null) {
            candidate.setLockedUntil(null);
            candidate.setFailedLoginAttempts(0);
            userRepository.save(candidate);
        }
        try {
            authenticate(email, request.password(), servletRequest, response);
        } catch (org.springframework.security.core.AuthenticationException error) {
            recordFailedAttempt(candidate);
            auditService.record(servletRequest, "login", "rejected", email);
            throw invalidCredentials();
        }
        AppUser user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(this::invalidCredentials);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);
        auditService.record(servletRequest, "login", "success", email);
        return toResponse(user);
    }

    public AuthUserResponse logout(HttpServletRequest request, HttpServletResponse response) {
        currentUserService.currentEmail().ifPresent(email -> auditService.record(request, "logout", "success", email));
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.setHeader("Clear-Site-Data", "\"cache\", \"cookies\", \"storage\"");
        return new AuthUserResponse(false, "Guest", null, "Guest mode");
    }

    private void authenticate(String email, String password, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        sessionAuthenticationStrategy.onAuthentication(authentication, request, response);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }

    private AuthUserResponse toResponse(AppUser user) {
        return new AuthUserResponse(true, user.getFullName(), user.getEmail(), "Signed in");
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank() || email.length() > 254
                || !email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid email is required.");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required.");
        }
        String normalized = fullName.trim();
        if (normalized.length() > 120) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is too long.");
        }
        return normalized;
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 128) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be between 8 and 128 characters.");
        }
    }

    private boolean isLocked(AppUser user) {
        return user.getLockedUntil() != null && Instant.now().isBefore(user.getLockedUntil());
    }

    private void recordFailedAttempt(AppUser user) {
        if (user == null) {
            return;
        }
        int failures = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(failures);
        if (failures >= LOCKOUT_THRESHOLD) {
            user.setLockedUntil(Instant.now().plus(LOCKOUT_DURATION));
        }
        userRepository.save(user);
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
    }
}
