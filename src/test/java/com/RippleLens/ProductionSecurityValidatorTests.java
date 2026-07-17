package com.RippleLens;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import com.RippleLens.config.ProductionSecurityValidator;

class ProductionSecurityValidatorTests {

    @Test
    void acceptsACompleteSecureProductionConfiguration() {
        MockEnvironment environment = secureEnvironment();

        assertThatCode(() -> new ProductionSecurityValidator(environment).run(null))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsDevelopmentDatabaseAndMissingSecretsInProduction() {
        MockEnvironment environment = secureEnvironment()
                .withProperty("spring.datasource.url", "jdbc:h2:mem:unsafe")
                .withProperty("spring.datasource.password", "")
                .withProperty("ripplelens.security.audit-key", "short")
                .withProperty("ripplelens.security.cors.allowed-origins", "*")
                .withProperty("ripplelens.security.application-base-url", "https://user@example.com/reset?unsafe=true")
                .withProperty("spring.flyway.baseline-on-migrate", "true");

        assertThatThrownBy(() -> new ProductionSecurityValidator(environment).run(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("must use PostgreSQL")
                .hasMessageContaining("SECURITY_AUDIT_KEY")
                .hasMessageContaining("CORS origins")
                .hasMessageContaining("APP_BASE_URL")
                .hasMessageContaining("baseline-on-migrate");
    }

    @Test
    void requiresEmailProviderSecretsWhenRecoveryDeliveryIsEnabled() {
        MockEnvironment environment = secureEnvironment()
                .withProperty("ripplelens.security.password-reset.delivery-enabled", "true")
                .withProperty("ripplelens.security.password-reset.provider", "resend")
                .withProperty("ripplelens.security.password-reset.from-address", "security@example.com")
                .withProperty("ripplelens.security.password-reset.resend-api-key", "");

        assertThatThrownBy(() -> new ProductionSecurityValidator(environment).run(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("resend-api-key");
    }

    @Test
    void rejectsEmbeddedDatabaseCredentialsAndAmbiguousTlsSettings() {
        MockEnvironment environment = secureEnvironment()
                .withProperty("spring.datasource.url",
                        "jdbc:postgresql://user:password@private-db:5432/ripplelens?sslmode=require&sslmode=disable");

        assertThatThrownBy(() -> new ProductionSecurityValidator(environment).run(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("one secure sslmode parameter")
                .hasMessageContaining("must not embed database credentials");
    }

    private MockEnvironment secureEnvironment() {
        return new MockEnvironment()
                .withProperty("spring.datasource.url",
                        "jdbc:postgresql://private-db:5432/ripplelens?sslmode=require")
                .withProperty("spring.datasource.username", "ripplelens_app")
                .withProperty("spring.datasource.password", "strong-database-password")
                .withProperty("spring.jpa.hibernate.ddl-auto", "validate")
                .withProperty("spring.flyway.enabled", "true")
                .withProperty("server.servlet.session.cookie.secure", "true")
                .withProperty("server.servlet.session.cookie.http-only", "true")
                .withProperty("server.servlet.session.cookie.name", "__Host-RIPPLELENS_SESSION")
                .withProperty("server.servlet.session.cookie.path", "/")
                .withProperty("server.servlet.session.cookie.same-site", "lax")
                .withProperty("ripplelens.security.require-https", "true")
                .withProperty("ripplelens.security.audit-key", "0123456789abcdef0123456789abcdef")
                .withProperty("ripplelens.security.application-base-url", "https://ripplelens.example")
                .withProperty("ripplelens.security.cors.allowed-origins", "https://ripplelens.example")
                .withProperty("ripplelens.security.password-reset.delivery-enabled", "false");
    }
}
