package com.RippleLens.security;

import java.time.Duration;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.RippleLens.service.CurrentUserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestSecurityInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;
    private final SecurityAuditService auditService;
    private final CurrentUserService currentUserService;

    public RequestSecurityInterceptor(
            RateLimitService rateLimitService,
            SecurityAuditService auditService,
            CurrentUserService currentUserService) {
        this.rateLimitService = rateLimitService;
        this.auditService = auditService;
        this.currentUserService = currentUserService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        enforceRequestSize(request);
        String method = request.getMethod();
        String path = request.getRequestURI();
        String ip = auditService.fingerprint(request.getRemoteAddr());

        if (HttpMethod.POST.matches(method) && path.equals("/api/auth/login")) {
            check(request, "login_ip", ip, 12, Duration.ofMinutes(15));
        } else if (HttpMethod.POST.matches(method) && path.equals("/api/auth/register")) {
            check(request, "register_ip", ip, 5, Duration.ofHours(1));
        } else if (HttpMethod.POST.matches(method) && path.equals("/api/auth/forgot-password")) {
            check(request, "forgot_password_ip", ip, 5, Duration.ofHours(1));
        } else if (HttpMethod.POST.matches(method) && path.equals("/api/auth/reset-password")) {
            check(request, "reset_password_ip", ip, 10, Duration.ofHours(1));
        } else if (HttpMethod.POST.matches(method) && path.equals("/api/live/refresh")) {
            check(request, "live_refresh", ip, 2, Duration.ofMinutes(5));
        } else if (HttpMethod.GET.matches(method) && path.equals("/api/live/stream")) {
            check(request, "live_stream", ip, 10, Duration.ofMinutes(5));
        } else if (HttpMethod.GET.matches(method) && path.equals("/api/live/health")) {
            check(request, "provider_health", ip, 10, Duration.ofMinutes(5));
        } else if (HttpMethod.GET.matches(method) && isLiveProviderRead(path)) {
            check(request, "live_provider_read", ip, 30, Duration.ofMinutes(1));
        } else if (HttpMethod.GET.matches(method) && isAnalysisRead(path)) {
            check(request, "analysis_read", ip, 30, Duration.ofMinutes(5));
        } else if (isSensitiveWrite(method, path)) {
            String subject = auditService.fingerprint(currentUserService.sessionKey());
            check(request, "personal_write", subject, 120, Duration.ofMinutes(1));
        }
        return true;
    }

    private boolean isAnalysisRead(String path) {
        return path.startsWith("/api/insights/")
                || path.equals("/api/intelligence/macro")
                || path.startsWith("/api/intelligence/macro/")
                || path.startsWith("/api/intelligence/news/")
                || path.startsWith("/api/intelligence/timeline/")
                || path.startsWith("/api/relationships/");
    }

    private boolean isLiveProviderRead(String path) {
        return path.equals("/api/live/metals")
                || path.equals("/api/live/cryptos")
                || path.equals("/api/live/currencies")
                || path.equals("/api/live/ticks");
    }

    private void enforceRequestSize(HttpServletRequest request) {
        if (!HttpMethod.POST.matches(request.getMethod()) && !HttpMethod.PATCH.matches(request.getMethod())
                && !HttpMethod.PUT.matches(request.getMethod())) {
            return;
        }
        long contentLength = request.getContentLengthLong();
        if (contentLength > RequestSizeLimitFilter.MAX_REQUEST_BYTES) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONTENT_TOO_LARGE, "Request is too large.");
        }
    }

    private boolean isSensitiveWrite(String method, String path) {
        boolean write = HttpMethod.POST.matches(method) || HttpMethod.PATCH.matches(method) || HttpMethod.DELETE.matches(method);
        return write && (path.startsWith("/api/alerts")
                || path.startsWith("/api/watchlist")
                || path.startsWith("/api/portfolio")
                || path.startsWith("/api/workspace"));
    }

    private void check(HttpServletRequest request, String policy, String subject, int attempts, Duration window) {
        try {
            rateLimitService.check(policy, subject, attempts, window);
        } catch (RateLimitExceededException error) {
            auditService.record(request, "rate_limit", "blocked_" + policy, subject);
            throw error;
        }
    }
}
