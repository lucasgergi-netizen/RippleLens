package com.RippleLens.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RequestSizeLimitFilter extends OncePerRequestFilter {

    public static final long MAX_REQUEST_BYTES = 64 * 1024;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        if (!hasRequestBody(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        if (request.getContentLengthLong() > MAX_REQUEST_BYTES) {
            response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"Request is too large.\"}");
            return;
        }
        filterChain.doFilter(new LimitedRequest(request), response);
    }

    private boolean hasRequestBody(HttpServletRequest request) {
        return HttpMethod.POST.matches(request.getMethod())
                || HttpMethod.PATCH.matches(request.getMethod())
                || HttpMethod.PUT.matches(request.getMethod());
    }

    private static final class LimitedRequest extends HttpServletRequestWrapper {

        private ServletInputStream inputStream;

        private LimitedRequest(HttpServletRequest request) {
            super(request);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (inputStream == null) {
                inputStream = new LimitedServletInputStream(super.getInputStream());
            }
            return inputStream;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            String encoding = getCharacterEncoding();
            return new BufferedReader(new InputStreamReader(
                    getInputStream(), encoding == null ? StandardCharsets.UTF_8 : java.nio.charset.Charset.forName(encoding)));
        }
    }

    private static final class LimitedServletInputStream extends ServletInputStream {

        private final ServletInputStream delegate;
        private long bytesRead;

        private LimitedServletInputStream(ServletInputStream delegate) {
            this.delegate = delegate;
        }

        @Override
        public int read() throws IOException {
            int value = delegate.read();
            if (value >= 0) {
                count(1);
            }
            return value;
        }

        @Override
        public int read(byte[] bytes, int offset, int length) throws IOException {
            int count = delegate.read(bytes, offset, length);
            if (count > 0) {
                count(count);
            }
            return count;
        }

        @Override
        public boolean isFinished() {
            return delegate.isFinished();
        }

        @Override
        public boolean isReady() {
            return delegate.isReady();
        }

        @Override
        public void setReadListener(jakarta.servlet.ReadListener readListener) {
            delegate.setReadListener(readListener);
        }

        private void count(int count) throws RequestSizeExceededException {
            bytesRead += count;
            if (bytesRead > MAX_REQUEST_BYTES) {
                throw new RequestSizeExceededException();
            }
        }
    }
}
