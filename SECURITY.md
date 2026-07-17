# RippleLens Security

## Reporting A Vulnerability

Do not open a public issue for a suspected vulnerability. Use GitHub's private vulnerability reporting feature for this repository, or contact the repository owner privately. Include the affected URL or component, reproduction steps, impact, and any suggested fix. Do not include real passwords, API keys, reset tokens, database credentials, or personal data.

## Incident Response

1. Contain: disable the affected endpoint or take the service offline when active exploitation is possible.
2. Preserve: save Render and database audit logs, request IDs, deployment IDs, and timestamps. Do not copy secrets into tickets or chat.
3. Rotate: replace database credentials, API keys, email-provider keys, and `SECURITY_AUDIT_KEY` when exposure is suspected. Rotating the audit key intentionally changes future log fingerprints.
4. Invalidate: expire affected sessions and password-reset tokens. Force password resets if password hashes or account records might be exposed.
5. Patch: reproduce in a private environment, add a regression test, deploy through CI, and verify the fix in production.
6. Recover: restore from a known-good backup when integrity is uncertain, monitor for recurrence, and document scope and timeline.
7. Notify: follow applicable legal and contractual notification requirements for affected users and providers.

## Operational Rules

- Keep production secrets only in Render or another secret manager.
- Review CodeQL, Dependabot, dependency-review, OWASP Dependency-Check, and FindSecBugs results before deployment.
- Restrict database network access, use a dedicated application user, require TLS, rotate credentials, and test backups.
- Never log passwords, authorization headers, session IDs, database URLs, raw email addresses, or reset tokens.
- Apply critical security updates immediately and high-severity updates within seven days unless a documented mitigation exists.
