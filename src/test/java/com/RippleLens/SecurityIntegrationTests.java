package com.RippleLens;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.RippleLens.model.AppUser;
import com.RippleLens.model.PasswordResetToken;
import com.RippleLens.repository.AppUserRepository;
import com.RippleLens.repository.PasswordResetTokenRepository;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTests {

    private static final String EXISTING_PASSWORD = "Existing!Market2026";

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Test
    void sendsSecurityHeadersAndCsrfCookie() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("Referrer-Policy", "strict-origin-when-cross-origin"))
                .andExpect(header().string("Permissions-Policy", "camera=(), microphone=(), geolocation=(), payment=(), usb=()"))
                .andExpect(header().string("Content-Security-Policy", org.hamcrest.Matchers.containsString("object-src 'none'")))
                .andExpect(header().string("Content-Security-Policy", org.hamcrest.Matchers.containsString("script-src-attr 'none'")));

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")))
                .andExpect(cookie().exists("XSRF-TOKEN"));
    }

    @Test
    void loadsAnalyticsOnlyAfterRecoveryRouteProtectionIsInitialized() throws Exception {
        String html;
        try (var input = Objects.requireNonNull(
                getClass().getResourceAsStream("/static/index.html"),
                "static/index.html must be packaged")) {
            html = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        String javascript = mockMvc.perform(get("/app.js"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(html).contains("/app.js").doesNotContain("www.googletagmanager.com/gtag/js");
        assertThat(javascript).contains("if (!recoveryRouteRequested)")
                .contains("initializeAnalytics();");
        assertThat(javascript.indexOf("consumeResetTokenFromRoute()"))
                .isLessThan(javascript.indexOf("initializeAnalytics();"));
    }

    @Test
    void rejectsStateChangesWithoutCsrfAndCrossOriginPreflights() throws Exception {
        mockMvc.perform(post("/api/watchlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"symbol\":\"XAU\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(options("/api/watchlist")
                        .header("Origin", "https://attacker.example")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));

        mockMvc.perform(options("/api/watchlist")
                        .header("Origin", "https://trusted.security.example")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://trusted.security.example"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));

        mockMvc.perform(get("/api/auth/unlisted-endpoint"))
                .andExpect(status().isForbidden());
    }

    @Test
    void registrationAndLoginDoNotRevealWhetherAnEmailExists() throws Exception {
        String email = "enumeration.security@example.com";
        userRepository.findByEmailIgnoreCase(email).ifPresent(userRepository::delete);
        String body = registerBody("Enumeration Tester", email, "Distinct!Security2026");

        String firstResponse = mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .with(fromIp("198.51.100.10"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted())
                .andReturn().getResponse().getContentAsString();

        String duplicateResponse = mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .with(fromIp("198.51.100.10"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted())
                .andReturn().getResponse().getContentAsString();

        assertThat(duplicateResponse).isEqualTo(firstResponse);

        String existingFailure = failedLogin(email, "Wrong!Password2026", "198.51.100.11");
        String missingFailure = failedLogin("missing.security@example.com", "Wrong!Password2026", "198.51.100.12");
        assertThat(missingFailure).isEqualTo(existingFailure);
    }

    @Test
    void recoveryDoesNotRevealWhetherAnEmailExists() throws Exception {
        String existingEmail = "forgot-enumeration.security@example.com";
        saveUser(existingEmail, "Forgot Enumeration", EXISTING_PASSWORD);

        String existingResponse = forgotPassword(existingEmail, "198.51.100.13");
        String missingResponse = forgotPassword("absent-forgot.security@example.com", "198.51.100.13");

        assertThat(missingResponse).isEqualTo(existingResponse);
    }

    @Test
    void hashesPasswordsWithBcryptWorkFactorTwelve() {
        String encoded = passwordEncoder.encode("Unique!Password2026");
        assertThat(encoded).matches("^\\$2[aby]\\$12\\$.+");
        assertThat(encoded).doesNotContain("Unique!Password2026");
    }

    @Test
    void temporarilyLocksAnAccountAfterRepeatedFailures() throws Exception {
        String email = "lockout.security@example.com";
        AppUser user = saveUser(email, "Lockout Tester", EXISTING_PASSWORD);

        for (int attempt = 0; attempt < 5; attempt++) {
            failedLogin(email, "Wrong!Password2026", "198.51.100.20");
        }

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .with(fromIp("198.51.100.20"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(email, EXISTING_PASSWORD)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid email or password."));

        AppUser locked = userRepository.findById(user.getId()).orElseThrow();
        assertThat(locked.getFailedLoginAttempts()).isEqualTo(5);
        assertThat(locked.getLockedUntil()).isAfter(Instant.now());
    }

    @Test
    void rateLimitsSensitiveRecoveryRequests() throws Exception {
        for (int request = 0; request < 5; request++) {
            mockMvc.perform(post("/api/auth/forgot-password")
                            .with(csrf())
                            .with(fromIp("198.51.100.30"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"rate" + request + "@example.com\"}"))
                    .andExpect(status().isAccepted())
                    .andExpect(jsonPath("$.message").value(
                            "If an account exists for that email, a password reset link will be sent."));
        }

        mockMvc.perform(post("/api/auth/forgot-password")
                        .with(csrf())
                        .with(fromIp("198.51.100.30"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"rate5@example.com\"}"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"))
                .andExpect(jsonPath("$.error").value("Too many requests. Try again later."));
    }

    @Test
    void resetTokensAreHashedShortLivedAndSingleUse() throws Exception {
        String email = "recovery.security@example.com";
        AppUser user = saveUser(email, "Recovery Tester", EXISTING_PASSWORD);
        mockMvc.perform(post("/api/auth/login")
                        .session(new MockHttpSession())
                        .with(csrf())
                        .with(fromIp("198.51.100.39"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(email, EXISTING_PASSWORD)))
                .andExpect(status().isOk());
        Object recoveryPrincipal = sessionRegistry.getAllPrincipals().stream()
                .filter(candidate -> candidate.toString().contains(email))
                .findFirst()
                .orElseThrow();
        String rawToken = "secure-reset-token-with-enough-entropy-for-test-2026";
        PasswordResetToken token = tokenRepository.save(new PasswordResetToken(
                user, sha256(rawToken), Instant.now().plus(15, ChronoUnit.MINUTES)));

        String resetBody = objectMapper.writeValueAsString(new ResetBody(rawToken, "Changed!Market2026"));
        mockMvc.perform(post("/api/auth/reset-password")
                        .with(csrf())
                        .with(fromIp("198.51.100.40"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password updated. Sign in with your new password."));

        assertThat(tokenRepository.findById(tokenId(token)).orElseThrow().getUsedAt()).isNotNull();
        assertThat(passwordEncoder.matches(
                "Changed!Market2026",
                userRepository.findByEmailIgnoreCase(email).orElseThrow().getPasswordHash())).isTrue();
        assertThat(sessionRegistry.getAllSessions(recoveryPrincipal, true))
                .allMatch(SessionInformation::isExpired);

        mockMvc.perform(post("/api/auth/reset-password")
                        .with(csrf())
                        .with(fromIp("198.51.100.40"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("The reset link is invalid or expired."));
    }

    @Test
    void expiredResetTokensAreConsumedAfterRejection() throws Exception {
        AppUser user = saveUser("expired-recovery.security@example.com", "Expired Recovery", EXISTING_PASSWORD);
        String rawToken = "expired-secure-reset-token-with-enough-entropy-2026";
        PasswordResetToken token = tokenRepository.save(new PasswordResetToken(
                user, sha256(rawToken), Instant.now().minus(1, ChronoUnit.MINUTES)));

        mockMvc.perform(post("/api/auth/reset-password")
                        .with(csrf())
                        .with(fromIp("198.51.100.43"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ResetBody(rawToken, "Unused!Password2026"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("The reset link is invalid or expired."));

        assertThat(tokenRepository.findById(tokenId(token)).orElseThrow().getUsedAt()).isNotNull();
    }

    @Test
    void resetTokenRemainsSingleUseDuringConcurrentRequests() throws Exception {
        String email = "concurrent-recovery.security@example.com";
        AppUser user = saveUser(email, "Concurrent Recovery", EXISTING_PASSWORD);
        String rawToken = "concurrent-secure-reset-token-with-enough-entropy-2026";
        tokenRepository.save(new PasswordResetToken(
                user, sha256(rawToken), Instant.now().plus(15, ChronoUnit.MINUTES)));

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        try {
            Future<Integer> first = executor.submit(() -> concurrentResetAttempt(
                    rawToken, "First!Concurrent2026", "198.51.100.41", ready, start));
            Future<Integer> second = executor.submit(() -> concurrentResetAttempt(
                    rawToken, "Second!Concurrent2026", "198.51.100.42", ready, start));

            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            start.countDown();
            assertThat(List.of(first.get(10, TimeUnit.SECONDS), second.get(10, TimeUnit.SECONDS)))
                    .containsExactlyInAnyOrder(200, 400);
        } finally {
            executor.shutdownNow();
        }

        String passwordHash = userRepository.findByEmailIgnoreCase(email).orElseThrow().getPasswordHash();
        assertThat(passwordEncoder.matches("First!Concurrent2026", passwordHash)
                || passwordEncoder.matches("Second!Concurrent2026", passwordHash)).isTrue();
    }

    @Test
    void isolatesGuestDataAndRejectsIdorDeletes() throws Exception {
        MockHttpSession owner = new MockHttpSession();
        MockHttpSession otherGuest = new MockHttpSession();

        MvcResult created = mockMvc.perform(post("/api/watchlist")
                        .session(owner)
                        .with(csrf())
                        .with(fromIp("198.51.100.50"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"symbol\":\"XAG\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        long itemId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/watchlist").session(owner))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("XAG"));
        mockMvc.perform(get("/api/watchlist").session(otherGuest))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        mockMvc.perform(delete("/api/watchlist/{id}", itemId)
                        .session(otherGuest)
                        .with(csrf())
                        .with(fromIp("198.51.100.51")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Watchlist item not found"));

        mockMvc.perform(delete("/api/watchlist/{id}", itemId)
                        .session(owner)
                        .with(csrf())
                        .with(fromIp("198.51.100.50")))
                .andExpect(status().isNoContent());
    }

    @Test
    void rejectsIdorForAlertsAndPortfolioHoldings() throws Exception {
        MockHttpSession owner = new MockHttpSession();
        MockHttpSession otherGuest = new MockHttpSession();

        MvcResult alertResult = mockMvc.perform(post("/api/alerts")
                        .session(owner)
                        .with(csrf())
                        .with(fromIp("198.51.100.52"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"symbol\":\"ETH\",\"alertType\":\"PRICE_ABOVE\",\"targetValue\":99999,"
                                + "\"notificationPreference\":\"IN_APP\",\"recurring\":false,"
                                + "\"customName\":\"Owner alert\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        long alertId = objectMapper.readTree(alertResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(patch("/api/alerts/{id}", alertId)
                        .session(otherGuest)
                        .with(csrf())
                        .with(fromIp("198.51.100.53"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"targetValue\":88888}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/alerts/{id}", alertId)
                        .session(otherGuest)
                        .with(csrf())
                        .with(fromIp("198.51.100.53")))
                .andExpect(status().isNotFound());

        MvcResult portfolioResult = mockMvc.perform(post("/api/portfolio/holdings")
                        .session(owner)
                        .with(csrf())
                        .with(fromIp("198.51.100.52"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"symbol\":\"BTC\",\"quantity\":1,\"averageCost\":100}"))
                .andExpect(status().isCreated())
                .andReturn();
        long holdingId = objectMapper.readTree(portfolioResult.getResponse().getContentAsString())
                .get("holdings").get(0).get("id").asLong();

        mockMvc.perform(patch("/api/portfolio/holdings/{id}", holdingId)
                        .session(otherGuest)
                        .with(csrf())
                        .with(fromIp("198.51.100.53"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":2}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/portfolio/holdings/{id}", holdingId)
                        .session(otherGuest)
                        .with(csrf())
                        .with(fromIp("198.51.100.53")))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/alerts/{id}", alertId)
                        .session(owner)
                        .with(csrf())
                        .with(fromIp("198.51.100.52")))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/portfolio/holdings/{id}", holdingId)
                        .session(owner)
                        .with(csrf())
                        .with(fromIp("198.51.100.52")))
                .andExpect(status().isOk());
    }

    @Test
    void rejectsInvalidSymbolsOversizedRequestsAndAnonymousRefreshes() throws Exception {
        mockMvc.perform(post("/api/watchlist")
                        .with(csrf())
                        .with(fromIp("198.51.100.60"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"symbol\":\"<script>\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .with(fromIp("198.51.100.63"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody("Weak Password", "weak-password.security@example.com", "aaaaaaaaaaaa")))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .with(fromIp("198.51.100.63"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody(
                                "Long Password",
                                "long-password.security@example.com",
                                "Aa1!" + "x".repeat(69))))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .with(fromIp("198.51.100.61"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(" ".repeat(65_537)))
                .andExpect(status().isPayloadTooLarge());

        mockMvc.perform(post("/api/live/refresh")
                        .with(csrf())
                        .with(fromIp("198.51.100.62")))
                .andExpect(status().isForbidden());
    }

    @Test
    void limitsUsersToThreeConcurrentSessions() throws Exception {
        String email = "sessions.security@example.com";
        saveUser(email, "Session Tester", EXISTING_PASSWORD);

        for (int login = 0; login < 4; login++) {
            mockMvc.perform(post("/api/auth/login")
                            .session(new MockHttpSession())
                            .with(csrf())
                            .with(fromIp("198.51.100." + (70 + login)))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginBody(email, EXISTING_PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.authenticated").value(true));
        }

        Object principal = sessionRegistry.getAllPrincipals().stream()
                .filter(candidate -> candidate.toString().contains(email))
                .findFirst()
                .orElseThrow();
        assertThat(sessionRegistry.getAllSessions(principal, false)).hasSize(3);
        assertThat(sessionRegistry.getAllSessions(principal, true))
                .filteredOn(SessionInformation::isExpired)
                .hasSize(1);
    }

    @Test
    void rotatesSessionOnLoginAndInvalidatesItOnLogout() throws Exception {
        String email = "fixation.security@example.com";
        saveUser(email, "Session Rotation", EXISTING_PASSWORD);
        MockHttpSession originalSession = new MockHttpSession();
        String originalSessionId = originalSession.getId();

        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .session(originalSession)
                        .with(csrf())
                        .with(fromIp("198.51.100.80"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(email, EXISTING_PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession authenticatedSession = (MockHttpSession) login.getRequest().getSession(false);
        assertThat(authenticatedSession).isNotNull();
        assertThat(authenticatedSession.getId()).isNotEqualTo(originalSessionId);

        mockMvc.perform(post("/api/auth/logout")
                        .session(authenticatedSession)
                        .with(csrf())
                        .with(fromIp("198.51.100.80")))
                .andExpect(status().isOk())
                .andExpect(header().string("Clear-Site-Data", "\"cache\", \"cookies\", \"storage\""));
        assertThat(authenticatedSession.isInvalid()).isTrue();
    }

    private AppUser saveUser(String email, String name, String password) {
        userRepository.findByEmailIgnoreCase(email).ifPresent(userRepository::delete);
        return userRepository.save(new AppUser(name, email, passwordEncoder.encode(password)));
    }

    private int concurrentResetAttempt(
            String token,
            String password,
            String ip,
            CountDownLatch ready,
            CountDownLatch start) throws Exception {
        ready.countDown();
        if (!start.await(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Concurrent reset test did not start in time.");
        }
        String body = objectMapper.writeValueAsString(new ResetBody(token, password));
        return mockMvc.perform(post("/api/auth/reset-password")
                        .with(csrf())
                        .with(fromIp(ip))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn().getResponse().getStatus();
    }

    private String failedLogin(String email, String password, String ip) throws Exception {
        return mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .with(fromIp(ip))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(email, password)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid email or password."))
                .andReturn().getResponse().getContentAsString();
    }

    private String forgotPassword(String email, String ip) throws Exception {
        return mockMvc.perform(post("/api/auth/forgot-password")
                        .with(csrf())
                        .with(fromIp(ip))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\"}"))
                .andExpect(status().isAccepted())
                .andReturn().getResponse().getContentAsString();
    }

    private String registerBody(String name, String email, String password) throws Exception {
        return objectMapper.writeValueAsString(new RegisterBody(name, email, password));
    }

    private String loginBody(String email, String password) throws Exception {
        return objectMapper.writeValueAsString(new LoginBody(email, password));
    }

    private RequestPostProcessor fromIp(String ip) {
        return request -> {
            request.setRemoteAddr(ip);
            return request;
        };
    }

    private String sha256(String token) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(token.getBytes(StandardCharsets.UTF_8)));
    }

    private long tokenId(PasswordResetToken token) throws Exception {
        var id = PasswordResetToken.class.getDeclaredField("id");
        id.setAccessible(true);
        return (long) id.get(token);
    }

    private record RegisterBody(String fullName, String email, String password) {
    }

    private record LoginBody(String email, String password) {
    }

    private record ResetBody(String token, String newPassword) {
    }
}
