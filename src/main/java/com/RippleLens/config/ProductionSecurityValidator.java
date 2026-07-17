package com.RippleLens.config;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class ProductionSecurityValidator implements ApplicationRunner {

    private final Environment environment;

    public ProductionSecurityValidator(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<String> failures = new ArrayList<>();
        String databaseUrl = property("spring.datasource.url");
        require(databaseUrl.startsWith("jdbc:postgresql://"), failures, "spring.datasource.url must use PostgreSQL");
        require(hasSecureSslMode(databaseUrl), failures, "spring.datasource.url must contain one secure sslmode parameter");
        require(!databaseUrl.contains("@"), failures,
                "spring.datasource.url must not embed database credentials");
        requirePresent("spring.datasource.username", failures);
        requirePresent("spring.datasource.password", failures);
        require("validate".equals(property("spring.jpa.hibernate.ddl-auto")), failures,
                "spring.jpa.hibernate.ddl-auto must be validate");
        require("true".equals(property("spring.flyway.enabled")), failures, "Flyway must be enabled");
        require(!Boolean.parseBoolean(property("spring.flyway.baseline-on-migrate")), failures,
                "Flyway baseline-on-migrate must remain disabled");
        require("true".equals(property("server.servlet.session.cookie.secure")), failures,
                "the session cookie must be Secure");
        require("true".equals(property("server.servlet.session.cookie.http-only")), failures,
                "the session cookie must be HttpOnly");
        require(property("server.servlet.session.cookie.name").startsWith("__Host-"), failures,
                "the session cookie must use the __Host- prefix");
        require("/".equals(property("server.servlet.session.cookie.path")), failures,
                "the session cookie path must be /");
        String sameSite = property("server.servlet.session.cookie.same-site").toLowerCase(java.util.Locale.ROOT);
        require(sameSite.equals("lax") || sameSite.equals("strict"), failures,
                "the session cookie SameSite setting must be Lax or Strict");
        require("true".equals(property("ripplelens.security.require-https")), failures, "HTTPS enforcement must be enabled");
        require(property("ripplelens.security.audit-key").length() >= 32, failures,
                "SECURITY_AUDIT_KEY must contain at least 32 characters");
        require(isExplicitHttpsOrigin(property("ripplelens.security.application-base-url")), failures,
                "APP_BASE_URL must be an explicit HTTPS origin");
        validateCorsOrigins(failures);
        if (Boolean.parseBoolean(property("ripplelens.security.password-reset.delivery-enabled"))) {
            String provider = property("ripplelens.security.password-reset.provider");
            require(provider.equals("smtp") || provider.equals("resend"), failures,
                    "password reset provider must be smtp or resend");
            requirePresent("ripplelens.security.password-reset.from-address", failures);
            require(!containsLineBreak(property("ripplelens.security.password-reset.from-address")), failures,
                    "password reset sender must not contain line breaks");
            if (provider.equals("smtp")) {
                requirePresent("spring.mail.host", failures);
                require(!property("spring.mail.host").equalsIgnoreCase("localhost"), failures,
                        "spring.mail.host cannot be localhost in production");
                requirePresent("spring.mail.username", failures);
                requirePresent("spring.mail.password", failures);
            }
            if (provider.equals("resend")) {
                requirePresent("ripplelens.security.password-reset.resend-api-key", failures);
            }
        }
        if (!failures.isEmpty()) {
            throw new IllegalStateException("Production security configuration is incomplete: " + String.join("; ", failures));
        }
    }

    private void validateCorsOrigins(List<String> failures) {
        Arrays.stream(property("ripplelens.security.cors.allowed-origins").split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .forEach(origin -> require(isExplicitHttpsOrigin(origin), failures,
                        "CORS origins must be explicit HTTPS origins"));
    }

    private boolean isExplicitHttpsOrigin(String value) {
        try {
            URI uri = URI.create(value);
            String path = uri.getPath();
            return "https".equalsIgnoreCase(uri.getScheme())
                    && uri.getHost() != null
                    && !uri.getHost().isBlank()
                    && uri.getUserInfo() == null
                    && uri.getQuery() == null
                    && uri.getFragment() == null
                    && (path == null || path.isBlank());
        } catch (IllegalArgumentException error) {
            return false;
        }
    }

    private boolean containsLineBreak(String value) {
        return value.contains("\r") || value.contains("\n");
    }

    private boolean hasSecureSslMode(String databaseUrl) {
        int queryStart = databaseUrl.indexOf('?');
        if (queryStart < 0 || queryStart == databaseUrl.length() - 1) {
            return false;
        }
        List<String> sslModes = Arrays.stream(databaseUrl.substring(queryStart + 1).split("&"))
                .map(parameter -> parameter.split("=", 2))
                .filter(parts -> parts.length == 2 && parts[0].equalsIgnoreCase("sslmode"))
                .map(parts -> parts[1].toLowerCase(java.util.Locale.ROOT))
                .toList();
        return sslModes.size() == 1
                && (sslModes.get(0).equals("require") || sslModes.get(0).equals("verify-full"));
    }

    private void requirePresent(String name, List<String> failures) {
        require(!property(name).isBlank(), failures, name + " is required");
    }

    private void require(boolean condition, List<String> failures, String message) {
        if (!condition) {
            failures.add(message);
        }
    }

    private String property(String name) {
        return environment.getProperty(name, "").trim();
    }
}
