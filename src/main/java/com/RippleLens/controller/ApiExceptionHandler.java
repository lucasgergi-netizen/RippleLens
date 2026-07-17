package com.RippleLens.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import com.RippleLens.security.RateLimitExceededException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleStatus(ResponseStatusException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(exception.getStatusCode().value());
        if (status == null || status.is5xxServerError()) {
            log.error("API request failed request_id={} type={} status={}",
                    requestId(request), exception.getClass().getSimpleName(), exception.getStatusCode().value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError("Request failed."));
        }
        return ResponseEntity.status(status).body(new ApiError(exception.getReason() == null ? "Request failed." : exception.getReason()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiError> handleInvalidRequest(Exception exception) {
        return ResponseEntity.badRequest().body(new ApiError("Invalid request."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableRequest(HttpMessageNotReadableException exception) {
        if (hasCause(exception, com.RippleLens.security.RequestSizeExceededException.class)) {
            return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).body(new ApiError("Request is too large."));
        }
        return ResponseEntity.badRequest().body(new ApiError("Invalid request."));
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiError> handleRateLimit(RateLimitExceededException exception) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", Long.toString(exception.getRetryAfterSeconds()))
                .body(new ApiError("Too many requests. Try again later."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception exception, HttpServletRequest request) {
        log.error("Unhandled API error request_id={} type={}", requestId(request), exception.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError("Something went wrong."));
    }

    private String requestId(HttpServletRequest request) {
        Object requestId = request.getAttribute(com.RippleLens.security.RequestIdFilter.REQUEST_ID_ATTRIBUTE);
        return requestId instanceof String value ? value : "unavailable";
    }

    private boolean hasCause(Throwable error, Class<? extends Throwable> type) {
        Throwable current = error;
        while (current != null) {
            if (type.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    public record ApiError(String error) {
    }
}
