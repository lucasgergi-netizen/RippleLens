# RippleLens Security Audit Report

Date: 2026-07-17  
Assessment type: white-box application, frontend, build, dependency, and deployment review  
Scope reviewed: 129 Java source files, 55 mapped controller endpoints, all static frontend files, configuration, Maven build, Docker/Render deployment, database access, migrations, and tests

## Executive Summary

The starting point for this hardening pass was the previously assessed 7.0/10 state. RippleLens now has strong application-level controls for authentication, account recovery, request abuse, authorization, browser security, secret handling, production configuration, migrations, and automated security analysis.

The evidence-based score after this pass is **8.8/10**. This is intentionally not 10/10. The local build and all 27 tests pass, OWASP Dependency-Check reports zero vulnerable dependencies, and SpotBugs with FindSecBugs reports zero findings. However, the new code has not yet been deployed, CI has not yet run on GitHub, PostgreSQL and email recovery were not exercised against live production services, the container could not be built or scanned locally because Docker is unavailable, and several controls still depend on hosting infrastructure.

## Weaknesses And Findings

| Severity | Weakness found | Resolution |
| --- | --- | --- |
| High | Authentication, registration, recovery, live-provider, analysis, SSE, and personal-write endpoints lacked comprehensive server-side abuse controls. | Fixed with bounded per-policy rate limits and `Retry-After` responses. |
| High | Login attempts did not have durable, race-safe temporary account lockout. | Fixed with five-attempt lockout, a 15-minute lock, pessimistic row locking, and transactions that commit controlled failures. |
| High | Secure account recovery was missing. | Fixed with 256-bit random reset tokens, SHA-256 hashes at rest, 15-minute expiry, single use, row locking, prior-token invalidation, and session invalidation after reset. |
| High | Login, registration, and recovery behavior could reveal whether an account existed. | Fixed with generic responses and BCrypt timing equalization. |
| High | User-owned objects were sometimes loaded by global ID before ownership checks, increasing IDOR risk. | Fixed with repository queries scoped by both ID and the authenticated/session owner key. |
| High | Production data could fall back to an ephemeral/local database and had no controlled schema migration path. | Fixed in code with mandatory PostgreSQL, TLS validation, Flyway migrations, and Hibernate `validate`; durable backups remain an infrastructure requirement. |
| High | The dependency scan identified pgJDBC CVE-2026-54291 and multiple embedded Tomcat 11.0.22 advisories. | Fixed by pinning pgJDBC 42.7.13 and Tomcat 11.0.24. The final report contains zero vulnerable dependencies. |
| Medium | Password policy did not sufficiently reject common, personal, low-diversity, or BCrypt-truncated input. | Fixed with 12-128 character validation, a 72-byte UTF-8 BCrypt ceiling, common/personal-password rejection, and BCrypt cost 12. |
| Medium | Concurrent sessions, session fixation, logout invalidation, and post-reset session revocation were incomplete. | Fixed with fixation migration, a maximum of three sessions, session registry integration, full logout invalidation, cookie clearing, and reset-time revocation. |
| Medium | Guest-owned data could be at risk if a shared identity were ever reused. | Fixed with a random per-HTTP-session owner key; tests prove separate guest sessions cannot read each other's data. |
| Medium | CSRF and route authorization needed an exact, maintainable policy. | Fixed with cookie-based CSRF protection for writes, an exact method/path allowlist, authenticated live refresh, and default-deny for unknown routes. |
| Medium | CORS could become overly broad through deployment configuration. | Fixed with exact configured origins, a restricted method/header list, credentials only for explicit origins, and production URL validation. |
| Medium | User input and request sizes needed consistent bounds. | Fixed with DTO validation, symbol/enum allowlists, numeric limits, per-user collection limits, 64 KiB request-body limits, and disabled multipart uploads. |
| Medium | Dynamic frontend HTML had XSS-sensitive rendering paths. | Fixed by encoding untrusted values, using safe DOM properties where practical, validating external HTTPS URLs, and preventing reset fragments from reaching selectors or analytics. |
| Medium | Error responses and provider-health details could expose internal implementation information. | Fixed with centralized generic errors, request IDs, hidden stack traces/database details, and generic provider failure labels. |
| Medium | Audit logs could become sensitive if raw identifiers or tokens were recorded. | Fixed with HMAC fingerprints for email, IP, and session subjects; passwords, tokens, credentials, and raw identifiers are excluded. |
| Medium | Production secrets and unsafe defaults were not all fail-closed. | Fixed with environment-only secrets and a production startup validator for PostgreSQL/TLS, credentials, migrations, cookies, HTTPS, origins, audit key, and recovery provider settings. |
| Medium | Automated dependency, static analysis, secret protection guidance, and update automation were incomplete. | Fixed with OWASP Dependency-Check, SpotBugs/FindSecBugs, CodeQL, dependency review, Dependabot, pinned actions, and documented GitHub secret-scanning settings. |
| Medium | Database schema changes depended on ORM behavior. | Fixed with versioned Flyway migrations and `ddl-auto=validate` in production. |
| Low | Spring Boot DevTools was unnecessary in this deployable application and caused an unrelated scanner CPE match. | Removed from the project. |
| Low | Jackson CVE-2026-54515 and Log4j API CVE-2026-49844 were present below the build's high-severity failure threshold. | Fixed rather than suppressed by pinning Jackson Databind 2.21.5 and Log4j 2.25.5. |
| Low | SSE connections had no explicit per-client/global capacity policy. | Fixed with three streams per client, 500 globally, a five-minute emitter timeout, and browser reconnect behavior. |
| Open | Rate limits and the concurrent-session registry are in process. | Acceptable for one Render instance; multiple instances require Redis, a shared session store, or equivalent gateway/WAF enforcement. |
| Open | External API response bodies do not have a uniform hard byte ceiling. | Add bounded HTTP response handling before treating hostile or compromised upstream providers as fully contained. |
| Open | CSP still permits inline styles and loads Google Analytics from an external script origin. | Remove inline styles/nonces and self-host or remove analytics for a stricter browser trust boundary. |
| Open | MFA, email verification, breached-password screening, and recovery-code support are absent. | Add before accounts hold valuable or regulated information. |
| Open | Production database durability, backups, point-in-time recovery, restore drills, and credential rotation are not locally verifiable. | Required hosting/operations work before a real production launch. |
| Open | No independent penetration test, live alerting/SIEM validation, incident drill, or disaster-recovery exercise has been performed. | Required assurance work for a genuine high-risk production claim. |

## Security Controls Implemented

### Authentication And Recovery

- BCrypt cost 12 with bounded and strengthened password policy.
- Generic login, registration, and forgot/reset responses to prevent enumeration.
- Subject and IP rate limits plus five-failure temporary account lockout.
- Race-safe login updates using pessimistic database locking.
- Short-lived, single-use, hashed password-reset tokens with transactional consumption.
- Resend HTTPS and authenticated TLS SMTP delivery options; recovery is fail-disabled by default.
- Reset links are removed from the browser URL before third-party analytics can load.
- Successful password resets invalidate all existing sessions.

### Authorization, Requests, And Data

- Exact endpoint/method allowlist with default deny.
- Query-level ownership checks for watchlist, alerts, and portfolio holdings.
- Per-session guest isolation and per-user collection limits.
- CSRF enforcement on state changes and strict CORS origins.
- Input allowlists and validation for accounts, symbols, alerts, portfolio values, preferences, and text.
- 64 KiB request limit, including chunked bodies; multipart uploads disabled.
- No native SQL, raw JDBC, `EntityManager`, or dynamic sort construction was found. Repository queries remain parameterized JPQL/derived JPA queries.
- SSE capacity limits and rate limits for expensive/public provider endpoints.

### Browser, Session, And Error Security

- `Secure`, `HttpOnly`, host-prefixed production session cookie with `SameSite=Lax` and 30-minute expiry.
- Session fixation protection, concurrent-session limit, logout invalidation, and `Clear-Site-Data`.
- HTTPS redirect and one-year HSTS in production.
- CSP, frame denial, MIME sniffing protection, referrer policy, permissions policy, COOP, CORP, and cross-domain policy denial.
- Encoded frontend rendering and safe URL handling for untrusted provider/user data.
- Generic JSON errors with request IDs and no stack traces, class names, database details, tokens, or secrets.
- Structured, privacy-preserving security audit events.

### Database, Secrets, Build, And Deployment

- Production-only PostgreSQL with TLS, separate credentials, bounded HikariCP pool, and no URL-embedded credentials.
- Flyway V1/V2 migrations and Hibernate schema validation.
- Production startup fails closed when required security values are missing or malformed.
- H2 remains local/test-only and is excluded from the production Boot JAR; DevTools is removed.
- Non-root, multi-stage Docker image with digest-pinned Java/Maven base images.
- Render Blueprint connects through the private database network and disables public database access.
- Maven wrapper distribution SHA-256 verification.
- OWASP Dependency-Check fails at CVSS 7, FindSecBugs/SpotBugs fail the build, and CI checks JavaScript plus the full Maven build.
- CodeQL security-extended, pull-request dependency review, and weekly Dependabot updates for Maven, Actions, and Docker.
- GitHub Actions are pinned to immutable commit SHAs and checkout does not retain credentials.
- Security reporting, production checklist, backup expectations, and incident-response guidance are documented.

## Verification Evidence

| Check | Result |
| --- | --- |
| `./mvnw clean verify` | PASS, 27 tests, 0 failures, 0 errors, 0 skipped |
| `./mvnw -Psecurity-scan verify` | PASS |
| OWASP Dependency-Check 12.2.2 | PASS, 0 vulnerable dependencies after upgrades |
| SpotBugs 4.9.8.2 + FindSecBugs 1.7.1 | PASS, 0 bug instances, 0 analysis errors |
| JavaScript syntax | PASS with Node `--check` |
| Shell syntax | PASS with `sh -n` |
| Render/GitHub YAML parse | PASS |
| Git whitespace validation | PASS with `git diff --check` |
| Flyway migration validation | PASS on H2 test database, V1 through V2 |
| Production JAR inspection | PostgreSQL/Flyway present; H2 and DevTools absent |
| Local runtime smoke test | PASS: app startup and `/`, CSS, JavaScript, health, and asset API responses |
| Docker image build/scan | NOT RUN: Docker is not installed locally |
| PostgreSQL integration test | NOT RUN: no disposable/live PostgreSQL service available locally |
| Resend/SMTP delivery test | NOT RUN: no live provider credentials were used |
| GitHub CI/CodeQL | NOT RUN until changes are pushed |

Security regression coverage includes CSRF, CORS, production headers, generic enumeration responses, BCrypt settings, weak and over-72-byte passwords, login lockout, recovery limits, reset expiry/single-use/concurrent consumption, session invalidation, fixation/concurrency/logout, guest isolation, alert/watchlist/portfolio IDOR, request-size rejection, rate-limit capacity under concurrency, SSE capacity, production startup validation, and reset-token analytics isolation.

## Files Modified

Existing files changed:

- `.dockerignore`
- `.env.example`
- `.mvn/wrapper/maven-wrapper.properties`
- `DEPLOYMENT.md`
- `Dockerfile`
- `README.md`
- `pom.xml`
- `render.yaml`
- `src/main/java/com/RippleLens/alert/CreateAlertRequest.java`
- `src/main/java/com/RippleLens/alert/UpdateAlertRequest.java`
- `src/main/java/com/RippleLens/auth/AuthRequest.java`
- `src/main/java/com/RippleLens/auth/RegisterRequest.java`
- `src/main/java/com/RippleLens/config/SecurityConfig.java`
- `src/main/java/com/RippleLens/controller/AuthController.java`
- `src/main/java/com/RippleLens/controller/LiveMarketController.java`
- `src/main/java/com/RippleLens/controller/PortfolioController.java`
- `src/main/java/com/RippleLens/controller/TrackingController.java`
- `src/main/java/com/RippleLens/controller/WorkspaceController.java`
- `src/main/java/com/RippleLens/model/AppUser.java`
- `src/main/java/com/RippleLens/portfolio/CreatePortfolioHoldingRequest.java`
- `src/main/java/com/RippleLens/portfolio/UpdatePortfolioHoldingRequest.java`
- `src/main/java/com/RippleLens/repository/AppUserRepository.java`
- `src/main/java/com/RippleLens/repository/AssetAlertRepository.java`
- `src/main/java/com/RippleLens/repository/PortfolioHoldingRepository.java`
- `src/main/java/com/RippleLens/repository/WatchlistItemRepository.java`
- `src/main/java/com/RippleLens/service/AssetService.java`
- `src/main/java/com/RippleLens/service/AuthService.java`
- `src/main/java/com/RippleLens/service/CommodityService.java`
- `src/main/java/com/RippleLens/service/CurrentUserService.java`
- `src/main/java/com/RippleLens/service/DataProviderHealthService.java`
- `src/main/java/com/RippleLens/service/InsightService.java`
- `src/main/java/com/RippleLens/service/LivePriceProviderService.java`
- `src/main/java/com/RippleLens/service/LivePriceStreamService.java`
- `src/main/java/com/RippleLens/service/MarketNewsService.java`
- `src/main/java/com/RippleLens/service/MetalsDevClient.java`
- `src/main/java/com/RippleLens/service/PortfolioService.java`
- `src/main/java/com/RippleLens/service/RelationshipEngineService.java`
- `src/main/java/com/RippleLens/service/TrackingService.java`
- `src/main/java/com/RippleLens/service/WorkspaceService.java`
- `src/main/java/com/RippleLens/watchlist/CreateWatchlistItemRequest.java`
- `src/main/java/com/RippleLens/workspace/RecentAssetRequest.java`
- `src/main/java/com/RippleLens/workspace/UpdateUserPreferencesRequest.java`
- `src/main/resources/application-prod.properties`
- `src/main/resources/application.properties`
- `src/main/resources/static/app.js`
- `src/main/resources/static/index.html`
- `src/test/resources/application.properties`

Files created:

- `.github/dependabot.yml`
- `.github/workflows/ci.yml`
- `.github/workflows/codeql.yml`
- `.github/workflows/security.yml`
- `SECURITY.md`
- `SECURITY_AUDIT_REPORT.md`
- `config/spotbugs-exclude.xml`
- `docker-entrypoint.sh`
- `src/main/java/com/RippleLens/auth/ForgotPasswordRequest.java`
- `src/main/java/com/RippleLens/auth/MessageResponse.java`
- `src/main/java/com/RippleLens/auth/ResetPasswordRequest.java`
- `src/main/java/com/RippleLens/config/AsyncConfig.java`
- `src/main/java/com/RippleLens/config/ProductionSecurityValidator.java`
- `src/main/java/com/RippleLens/config/WebSecurityConfig.java`
- `src/main/java/com/RippleLens/controller/ApiExceptionHandler.java`
- `src/main/java/com/RippleLens/model/PasswordResetToken.java`
- `src/main/java/com/RippleLens/repository/PasswordResetTokenRepository.java`
- `src/main/java/com/RippleLens/security/InputPolicy.java`
- `src/main/java/com/RippleLens/security/RateLimitExceededException.java`
- `src/main/java/com/RippleLens/security/RateLimitService.java`
- `src/main/java/com/RippleLens/security/RequestIdFilter.java`
- `src/main/java/com/RippleLens/security/RequestSecurityInterceptor.java`
- `src/main/java/com/RippleLens/security/RequestSizeExceededException.java`
- `src/main/java/com/RippleLens/security/RequestSizeLimitFilter.java`
- `src/main/java/com/RippleLens/security/SecurityAuditService.java`
- `src/main/java/com/RippleLens/service/NoOpPasswordResetDeliveryService.java`
- `src/main/java/com/RippleLens/service/PasswordPolicyService.java`
- `src/main/java/com/RippleLens/service/PasswordResetDeliveryService.java`
- `src/main/java/com/RippleLens/service/PasswordResetEventListener.java`
- `src/main/java/com/RippleLens/service/PasswordResetRequestedEvent.java`
- `src/main/java/com/RippleLens/service/PasswordResetService.java`
- `src/main/java/com/RippleLens/service/ResendPasswordResetDeliveryService.java`
- `src/main/java/com/RippleLens/service/SecurityMaintenanceService.java`
- `src/main/java/com/RippleLens/service/SmtpPasswordResetDeliveryService.java`
- `src/main/resources/db/migration/V1__baseline_schema.sql`
- `src/main/resources/db/migration/V2__account_security_and_recovery.sql`
- `src/test/java/com/RippleLens/ProductionSecurityValidatorTests.java`
- `src/test/java/com/RippleLens/SecurityIntegrationTests.java`
- `src/test/java/com/RippleLens/security/RateLimitServiceTests.java`
- `src/test/java/com/RippleLens/service/LivePriceStreamServiceTests.java`

## Remaining Production Requirements

1. Push these changes and require CI, CodeQL, and Security Checks on the protected `main` branch.
2. Enable GitHub Dependabot alerts, secret scanning, push protection, and private vulnerability reporting where available.
3. Deploy using the Blueprint, then verify headers, cookies, HTTPS redirects, CSRF, authorization, guest isolation, and audit-log redaction against the live URL.
4. Replace the expiring free Render database before real users depend on it. Use a durable managed PostgreSQL plan with private networking, TLS, restricted credentials, automated backups/PITR, retention, monitoring, and tested restores.
5. Configure Resend or authenticated TLS SMTP, then test expiry, one-time use, delivery privacy, and post-reset session invalidation with a test account.
6. Build and scan the Docker image, generate/store an SBOM, and establish a patch SLA for base-image findings.
7. Add shared rate limiting/session storage before scaling above one application instance.
8. Add MFA, verified email ownership, breached-password screening, and recovery codes before the account system protects meaningful value.
9. Add production monitoring and alerting for authentication abuse, rate-limit spikes, recovery anomalies, database health, and deployment failures; run an incident and restore exercise.
10. Commission an independent penetration test after deployment and remediate its findings.

Only after those operational controls are implemented, exercised, and independently tested would a score near 10/10 be defensible. Security also requires continuous patching and monitoring, so 10/10 should never be treated as a permanent state.

## Primary References

- [Spring Security CSRF protection](https://docs.spring.io/spring-security/reference/7.0/servlet/exploits/csrf.html)
- [Spring Security session management](https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html)
- [Spring Security HTTPS and security headers](https://docs.spring.io/spring-security/reference/servlet/exploits/http.html)
- [Flyway migration concepts](https://documentation.red-gate.com/flyway/flyway-concepts/migrations)
- [OWASP Dependency-Check](https://github.com/dependency-check/DependencyCheck)
- [GitHub security features](https://docs.github.com/en/code-security/getting-started/github-security-features?apiVersion=2022-11-28)
- [GitHub CodeQL for compiled Java](https://docs.github.com/en/code-security/how-tos/find-and-fix-code-vulnerabilities/manage-your-configuration/codeql-for-compiled-languages)
- [Apache Tomcat 11 security advisories](https://tomcat.apache.org/security-11)
- [pgJDBC security advisories](https://jdbc.postgresql.org/security/)
- [Render free service limits](https://render.com/docs/free)
- [Render PostgreSQL connectivity](https://render.com/docs/postgresql-creating-connecting)
