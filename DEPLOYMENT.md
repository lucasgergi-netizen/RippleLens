# Deploying RippleLens Securely

RippleLens is a Spring Boot application. On Render, choose a **Web Service** or deploy the included Blueprint. It is not a Static Site because the Java backend serves both the APIs and frontend.

## Important Free-Tier Limits

Render's free web service is suitable for a public demo, not a security-guaranteed production service. It sleeps after inactivity and its filesystem is temporary. The included Blueprint therefore uses PostgreSQL instead of the local H2 file.

Render's free PostgreSQL database currently expires after 30 days, is limited to 1 GB, and has no backups. For durable real accounts, upgrade the database or use another managed PostgreSQL provider with TLS and backups. A real production launch also needs monitoring, restore testing, and an incident owner.

The built-in rate limiter and concurrent-session registry are intentionally in-process and fit the single-instance free service. Before running multiple application instances, replace them with a shared Redis-backed limiter/session store or enforce equivalent limits at a trusted API gateway or WAF.

## First Secure Deployment

1. Push this repository to GitHub.
2. Open [Render](https://dashboard.render.com/) and choose **New > Blueprint**.
3. Select the RippleLens repository. Render reads `render.yaml` and proposes the `ripplelens` web service and `ripplelens-db` PostgreSQL database.
4. Confirm both resources use the same region. The Blueprint disables public database access and uses the private connection URL with `sslmode=require`.
5. In the web service's **Environment** page, add any optional API keys: `OPENAI_API_KEY`, `NEWS_API_KEY`, and `METALS_DEV_API_KEY`.
6. Leave `PASSWORD_RESET_DELIVERY_ENABLED=false` until email delivery is configured. The recovery endpoint intentionally returns a generic response but will not create usable reset links while delivery is disabled.
7. Deploy and wait for the health check to pass.
8. Open `https://ripplelens.onrender.com/healthz`, then the main site.

## Enable Password Recovery On Render Free

Render free web services block normal SMTP ports. The project supports the Resend HTTPS API instead:

1. Create a Resend account, verify a sending domain, and create an API key restricted to sending email.
2. In Render, set `RESEND_API_KEY` to the key.
3. Set `PASSWORD_RESET_FROM_ADDRESS` to a sender on the verified domain, such as `RippleLens <security@example.com>`.
4. Keep `PASSWORD_RESET_PROVIDER=resend` and change `PASSWORD_RESET_DELIVERY_ENABLED=true`.
5. Save and deploy. Test recovery with a non-administrator test account.

For another host that permits SMTP, set `PASSWORD_RESET_PROVIDER=smtp` and configure `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD`, and `PASSWORD_RESET_FROM_ADDRESS`. TLS and authentication are mandatory.

## Required Production Values

The application refuses to finish startup in the `prod` profile unless these controls are present:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://...?...sslmode=require
SPRING_DATASOURCE_USERNAME=restricted_application_user
SPRING_DATASOURCE_PASSWORD=secret
SECURITY_AUDIT_KEY=at_least_32_random_characters
APP_BASE_URL=https://your-exact-domain
CORS_ALLOWED_ORIGINS=https://your-exact-domain
```

The Render Blueprint supplies the database values and generates the audit key. `docker-entrypoint.sh` removes the credentials embedded in Render's private connection string, uses the separately injected username and password, and converts the remaining `postgresql://` address to the TLS-required JDBC format expected by Spring Boot.

## Database And Migrations

- Flyway runs `V1` and `V2` automatically on an empty PostgreSQL database.
- Hibernate uses `validate`; it never updates production tables automatically.
- `FLYWAY_BASELINE_ON_MIGRATE` defaults to `false` to avoid silently adopting the wrong non-empty schema.
- Before changing providers or plans, take a backup and prove that it can be restored.
- Use separate database credentials per environment and rotate them after suspected exposure.

## Verification

Run locally before deployment:

```bash
./mvnw clean verify
./mvnw -Psecurity-scan verify
```

After deployment, verify:

```text
https://ripplelens.onrender.com/
https://ripplelens.onrender.com/healthz
https://ripplelens.onrender.com/api/assets
```

Confirm that HTTP redirects to HTTPS, the session cookie is `Secure` and `HttpOnly`, state-changing requests without a CSRF token fail, another guest cannot read the first guest's data, and logs contain request IDs but no credentials or reset tokens.

## GitHub Repository Settings

Enable branch protection for `main` and require the CI, CodeQL, and Security Checks workflows. Also enable Dependabot alerts, private vulnerability reporting, secret scanning, and push protection where available. Do not approve a deployment while a high-severity finding is unresolved unless a documented mitigation has been reviewed.

Create a free NVD API key and add it as the repository Actions secret `NVD_API_KEY`. The dependency scan works without it, but the first vulnerability-database download is heavily rate-limited and can take a long time.
