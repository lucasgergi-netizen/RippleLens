package com.RippleLens.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.RippleLens.auth.ForgotPasswordRequest;
import com.RippleLens.auth.MessageResponse;
import com.RippleLens.auth.ResetPasswordRequest;
import com.RippleLens.model.AppUser;
import com.RippleLens.model.PasswordResetToken;
import com.RippleLens.repository.AppUserRepository;
import com.RippleLens.repository.PasswordResetTokenRepository;
import com.RippleLens.security.RateLimitService;
import com.RippleLens.security.SecurityAuditService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class PasswordResetService {

    private static final Duration TOKEN_TTL = Duration.ofMinutes(15);
    private static final String GENERIC_REQUEST_MESSAGE =
            "If an account exists for that email, a password reset link will be sent.";

    private final AppUserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordPolicyService passwordPolicyService;
    private final RateLimitService rateLimitService;
    private final SecurityAuditService auditService;
    private final SessionRegistry sessionRegistry;
    private final ApplicationEventPublisher eventPublisher;
    private final SecureRandom secureRandom = new SecureRandom();
    private final boolean deliveryEnabled;
    private final String applicationBaseUrl;
    private final String enumerationDefenseHash;

    public PasswordResetService(
            AppUserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            PasswordPolicyService passwordPolicyService,
            RateLimitService rateLimitService,
            SecurityAuditService auditService,
            SessionRegistry sessionRegistry,
            ApplicationEventPublisher eventPublisher,
            @Value("${ripplelens.security.password-reset.delivery-enabled:false}") boolean deliveryEnabled,
            @Value("${ripplelens.security.application-base-url:http://localhost:8080}") String applicationBaseUrl) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordPolicyService = passwordPolicyService;
        this.rateLimitService = rateLimitService;
        this.auditService = auditService;
        this.sessionRegistry = sessionRegistry;
        this.eventPublisher = eventPublisher;
        this.deliveryEnabled = deliveryEnabled;
        this.applicationBaseUrl = applicationBaseUrl.replaceAll("/+$", "");
        this.enumerationDefenseHash = passwordEncoder.encode(randomToken());
    }

    @Transactional
    public MessageResponse requestReset(ForgotPasswordRequest request, HttpServletRequest servletRequest) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String subject = auditService.fingerprint(email);
        rateLimitService.check("forgot_password_subject", subject, 3, Duration.ofHours(1));
        passwordEncoder.matches(randomToken(), enumerationDefenseHash);

        if (deliveryEnabled) {
            userRepository.findByEmailIgnoreCase(email).ifPresent(user -> issueToken(user));
        }
        auditService.record(servletRequest, "password_reset_requested", "accepted", email);
        return new MessageResponse(GENERIC_REQUEST_MESSAGE);
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public MessageResponse resetPassword(ResetPasswordRequest request, HttpServletRequest servletRequest) {
        String tokenHash = hashToken(request.token());
        rateLimitService.check("reset_password_token", auditService.fingerprint(tokenHash), 5, Duration.ofMinutes(15));
        PasswordResetToken token = tokenRepository.findByTokenHashAndUsedAtIsNull(tokenHash)
                .orElseThrow(this::invalidToken);
        Instant now = Instant.now();
        if (!now.isBefore(token.getExpiresAt())) {
            token.markUsed(now);
            tokenRepository.save(token);
            throw invalidToken();
        }

        AppUser user = token.getUser();
        passwordPolicyService.validateNewPassword(request.newPassword(), user.getEmail(), user.getFullName());
        user.changePassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        tokenRepository.findByUserAndUsedAtIsNull(user).forEach(active -> active.markUsed(now));
        tokenRepository.flush();
        invalidateSessions(user.getEmail());
        HttpSession session = servletRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        auditService.record(servletRequest, "password_reset_completed", "success", user.getEmail());
        return new MessageResponse("Password updated. Sign in with your new password.");
    }

    private void issueToken(AppUser user) {
        Instant now = Instant.now();
        tokenRepository.findByUserAndUsedAtIsNull(user).forEach(token -> token.markUsed(now));
        String rawToken = randomToken();
        tokenRepository.save(new PasswordResetToken(user, hashToken(rawToken), now.plus(TOKEN_TTL)));
        eventPublisher.publishEvent(new PasswordResetRequestedEvent(
                user.getEmail(), applicationBaseUrl + "/#reset=" + rawToken));
    }

    private void invalidateSessions(String email) {
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            if (principal instanceof UserDetails userDetails && userDetails.getUsername().equalsIgnoreCase(email)) {
                for (SessionInformation session : sessionRegistry.getAllSessions(principal, false)) {
                    session.expireNow();
                }
            }
        }
    }

    private String randomToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException error) {
            throw new IllegalStateException("Password reset hashing is unavailable.", error);
        }
    }

    private ResponseStatusException invalidToken() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "The reset link is invalid or expired.");
    }
}
