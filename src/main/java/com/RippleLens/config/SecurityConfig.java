package com.RippleLens.config;

import java.util.List;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.RippleLens.repository.AppUserRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CookieCsrfTokenRepository csrfTokenRepository,
            CorsConfigurationSource corsConfigurationSource,
            SessionRegistry sessionRegistry,
            @Value("${ripplelens.security.require-https:false}") boolean requireHttps) throws Exception {
        http
                .csrf(csrf -> csrf.csrfTokenRepository(csrfTokenRepository))
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/index.html", "/app.js", "/styles.css", "/favicon.ico", "/healthz").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/auth/me",
                                "/api/watchlist", "/api/alerts", "/api/portfolio", "/api/workspace",
                                "/api/assets", "/api/assets/category/{category}", "/api/assets/{symbol}",
                                "/api/assets/{symbol}/prices", "/api/assets/{symbol}/prices/hourly",
                                "/api/assets/{symbol}/trust",
                                "/api/commodities", "/api/commodities/{symbol}",
                                "/api/insights/{symbol}",
                                "/api/intelligence/macro", "/api/intelligence/macro/{symbol}",
                                "/api/intelligence/events/{symbol}", "/api/intelligence/news/{symbol}",
                                "/api/intelligence/timeline/{symbol}",
                                "/api/live/metals", "/api/live/cryptos", "/api/live/currencies",
                                "/api/live/health", "/api/live/stream", "/api/live/ticks",
                                "/api/metals/{symbol}/ripple-map", "/api/relationships/{symbol}").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/auth/register", "/api/auth/login", "/api/auth/logout",
                                "/api/auth/forgot-password", "/api/auth/reset-password",
                                "/api/watchlist", "/api/alerts", "/api/portfolio/holdings",
                                "/api/workspace/recent").permitAll()
                        .requestMatchers(HttpMethod.PATCH,
                                "/api/alerts/{id}", "/api/alerts/{id}/enabled",
                                "/api/portfolio/holdings/{id}", "/api/workspace/preferences").permitAll()
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/watchlist/{id}", "/api/alerts/{id}",
                                "/api/portfolio/holdings/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/live/refresh").hasRole("USER")
                        .anyRequest().denyAll())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation(sessionFixation -> sessionFixation.migrateSession())
                        .maximumSessions(3)
                        .maxSessionsPreventsLogin(false)
                        .sessionRegistry(sessionRegistry))
                .headers(headers -> headers
                        .contentTypeOptions(contentTypeOptions -> {})
                        .frameOptions(frameOptions -> frameOptions.deny())
                        .referrerPolicy(referrer -> referrer.policy(
                                ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .preload(false)
                                .maxAgeInSeconds(31536000))
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; "
                                        + "base-uri 'none'; object-src 'none'; frame-ancestors 'none'; frame-src 'none'; "
                                        + "media-src 'none'; worker-src 'none'; manifest-src 'self'; form-action 'self'; "
                                        + "script-src 'self' https://www.googletagmanager.com; script-src-attr 'none'; "
                                        + "style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:; "
                                        + "connect-src 'self' https://www.google-analytics.com https://region1.google-analytics.com; "
                                        + "upgrade-insecure-requests"))
                        .addHeaderWriter(new StaticHeadersWriter("Permissions-Policy",
                                "camera=(), microphone=(), geolocation=(), payment=(), usb=()"))
                        .addHeaderWriter(new StaticHeadersWriter("Cross-Origin-Opener-Policy", "same-origin"))
                        .addHeaderWriter(new StaticHeadersWriter("Cross-Origin-Resource-Policy", "same-origin"))
                        .addHeaderWriter(new StaticHeadersWriter("X-Permitted-Cross-Domain-Policies", "none")));
        if (requireHttps) {
            http.redirectToHttps(Customizer.withDefaults());
        }
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(AppUserRepository userRepository) {
        return username -> userRepository.findByEmailIgnoreCase(username)
                .map(user -> User.withUsername(user.getEmail())
                        .password(user.getPasswordHash())
                        .roles(user.getRole())
                        .accountLocked(user.getLockedUntil() != null && java.time.Instant.now().isBefore(user.getLockedUntil()))
                        .build())
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
                        "Unknown user"));
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(List.of(provider));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CookieCsrfTokenRepository csrfTokenRepository(
            @Value("${ripplelens.security.require-https:false}") boolean requireHttps) {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieName(requireHttps ? "__Host-XSRF-TOKEN" : "XSRF-TOKEN");
        repository.setCookieCustomizer(cookie -> cookie.sameSite("Lax").secure(requireHttps).path("/"));
        return repository;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${ripplelens.security.cors.allowed-origins:}") String configuredOrigins) {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> origins = Arrays.stream(configuredOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList();
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Content-Type", "X-XSRF-TOKEN", "X-Request-ID"));
        configuration.setExposedHeaders(List.of("X-Request-ID", "Retry-After"));
        configuration.setAllowCredentials(!origins.isEmpty());
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy(SessionRegistry sessionRegistry) {
        ConcurrentSessionControlAuthenticationStrategy concurrent =
                new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
        concurrent.setMaximumSessions(3);
        concurrent.setExceptionIfMaximumExceeded(false);
        SessionFixationProtectionStrategy fixation = new SessionFixationProtectionStrategy();
        RegisterSessionAuthenticationStrategy registration = new RegisterSessionAuthenticationStrategy(sessionRegistry);
        return new CompositeSessionAuthenticationStrategy(List.of(concurrent, fixation, registration));
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
