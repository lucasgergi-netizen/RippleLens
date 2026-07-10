# Deploying RippleLens

This guide deploys RippleLens as one Spring Boot web app. The backend, Thymeleaf page, CSS, and JavaScript are all served from the same application.

## Recommended Free Host

Use **Render Free Web Service** for this project.

Why Render fits this app:

- It can run a full Spring Boot backend, not only static HTML.
- It supports Docker builds, so the server can build the Maven app even if Maven is not installed locally.
- The current database is in-memory H2, so the app does not need a separate database service for the first public demo.
- The free tier is enough for a student/demo project.

Important free-tier limits:

- The app can sleep after inactivity, so the first request after a pause may take about a minute.
- The local filesystem is temporary.
- The H2 database resets when the app restarts.

## What This Project Uses

- Build tool: Maven (`pom.xml`)
- Java version: 21
- Framework: Spring Boot 3.3.5
- Frontend: Thymeleaf template plus static CSS and JavaScript in `src/main/resources`
- Database: H2 in-memory database
- Deployment packaging: Docker

## Deployment Files

- `Dockerfile` builds and runs the Spring Boot jar.
- `.dockerignore` keeps local files out of the Docker build.
- `render.yaml` defines the Render web service.
- `.env.example` lists optional local environment variables.
- `src/main/resources/application-prod.properties` disables the H2 console in production.

## Before Deploying

Make sure the project is committed and pushed to GitHub. Render deploys most easily from a GitHub repository.

```bash
git add .
git commit -m "Prepare RippleLens for deployment"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/ripplelens.git
git push -u origin main
```

If `origin` already exists, use this instead:

```bash
git remote -v
git push -u origin main
```

## Deploy On Render

1. Go to `https://render.com`.
2. Sign up or log in.
3. Connect your GitHub account.
4. Click **New**.
5. Click **Blueprint**.
6. Choose the GitHub repository that contains RippleLens.
7. Render will detect `render.yaml`.
8. Confirm the service name is `ripplelens`.
9. Confirm the plan is **Free**.
10. Click **Apply** or **Deploy**.
11. Wait for the Docker build to finish.
12. Open the public `.onrender.com` URL that Render gives you.

## Manual Render Setup

If you do not use the Blueprint flow:

1. Click **New**.
2. Click **Web Service**.
3. Choose the RippleLens GitHub repository.
4. Select **Docker** as the runtime.
5. Set the Dockerfile path to `./Dockerfile`.
6. Choose the **Free** instance type.
7. Set the health check path to `/`.
8. Add these environment variables:

```text
SPRING_PROFILES_ACTIVE=prod
H2_CONSOLE_ENABLED=false
```

9. Deploy the service.

## Check The Deployed App

After deployment, open these URLs using your Render domain:

```text
https://YOUR-SERVICE.onrender.com/
https://YOUR-SERVICE.onrender.com/api/metals
https://YOUR-SERVICE.onrender.com/api/currencies/top
https://YOUR-SERVICE.onrender.com/api/cryptos/top
https://YOUR-SERVICE.onrender.com/api/assets/XAU/prices
https://YOUR-SERVICE.onrender.com/api/assets/XAU/insight
https://YOUR-SERVICE.onrender.com/api/metals/XAU/money-pressure
```

The page should load `/styles.css` and `/app.js` automatically because Spring Boot serves files from `src/main/resources/static`.

## Environment Variables

These settings are optional unless you replace H2 with another database later.

```text
PORT=8080
SPRING_PROFILES_ACTIVE=prod
H2_CONSOLE_ENABLED=false
SPRING_DATASOURCE_URL=jdbc:h2:mem:ripplelens
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=
JPA_DDL_AUTO=create-drop
JPA_SHOW_SQL=false
```

For the current demo deployment, keep H2. If you later add real accounts, saved watchlists, or saved alerts, replace H2 with PostgreSQL so data survives restarts.

## Troubleshooting

If the deploy fails during build, open the Render logs and look for the first Maven error.

If the page opens but data does not load, test `/api/metals` first. If that works, the backend is running and the problem is likely a browser JavaScript error.

If the first page load is slow, wait about one minute. Free Render services can sleep after inactivity.
