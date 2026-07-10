# Deploying RippleLens

RippleLens is a Spring Boot web app. It must be deployed as a Render **Web Service**, not a Static Site.

## Recommended Free Host

Use **Render Free Web Service**.

Why it fits:

- It can run the Spring Boot backend and frontend together.
- It supports Docker builds.
- The included H2 database is enough for a demo deployment.
- The free tier is fine for showing the project publicly.

Free-tier notes:

- The app can sleep after inactivity, so first load can take 50 seconds or more.
- The H2 database is stored inside the service filesystem and can reset on redeploy/restart.
- For real users and permanent accounts, use PostgreSQL later.

## Required Render Environment Variables

Set these in Render:

```text
SPRING_PROFILES_ACTIVE=prod
H2_CONSOLE_ENABLED=false
```

Optional live intelligence keys:

```text
OPENAI_API_KEY=your_openai_key
NEWS_API_KEY=your_newsapi_key
METALS_DEV_API_KEY=your_metals_dev_key
```

Do not commit `config/ripplelens-secrets.properties`. It is local only.

## Deploy

1. Push this project to GitHub.
2. Go to `https://render.com`.
3. Click **New**.
4. Click **Blueprint**.
5. Choose the `lucasgergi-netizen/RippleLens` GitHub repo.
6. Render should detect `render.yaml`.
7. Add the optional secret values if you want AI/news/metals history online.
8. Deploy.
9. Open `https://ripplelens.onrender.com`.

## Test URLs

```text
https://ripplelens.onrender.com/
https://ripplelens.onrender.com/healthz
https://ripplelens.onrender.com/api/assets
```
