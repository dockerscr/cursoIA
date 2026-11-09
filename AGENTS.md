# Agents Instructions

## Product Overview
- AstroBookings Client is a Java web app for booking seats on rocket launches.
- It consumes the ActivityBookings REST API and stores no data of its own.

## Technical Implementation

### Tech Stack
- **Language**: Java 27
- **Framework**: Jakarta EE 11: Servlet 6.1, JSP 4.0, JSTL on WildFly 41
- **Build**: Maven, WAR packaging, `wildfly-maven-plugin`
- **Database**: None; the API is the only source of truth
- **Security**: JWT in HttpSession, Bearer header, CSRF token on POST
- **Testing**: JUnit 5 + Mockito + AssertJ for unit tests (stub `ApiClient`, no live network); Playwright for E2E acceptance tests in `tests/`
- **Logging**: SLF4J over JUL; never log tokens or passwords

### Development workflow

```bash
# Requires JDK 27, Maven 3.9+ and a running WildFly 41
mvn clean package   # build the WAR
mvn test            # run unit tests
mvn wildfly:deploy  # deploy to a running server
mvn wildfly:undeploy
npm install         # install Playwright once
npx playwright test # E2E acceptance tests (needs the app deployed)
```

- The API is already running at `http://localhost:3000`; do not start it.
- Override `ASTRO_API_BASE_URL` only for other environments.
- Open the app at `http://localhost:8080/astrobookings-client/launches`.
- E2E tests expect the app at `http://localhost:8080/astrobookings-client` plus a
  fixture-backed instance at `http://localhost:8180/astrobookings-client`; the
  fixture API in `tests/support/fixture-api.mjs` is started by Playwright.

### Folder structure
```text
.
├── AGENTS.md                # This file with instructions for AI agents
├── README.md                # Course briefing
├── PRD.md                   # Product requirements
├── ADD.md                   # Architecture design
├── pom.xml                  # Maven build
├── playwright.config.ts     # E2E test configuration
├── specs/                   # Feature specs and issue JSON files
├── tests/                   # Playwright E2E acceptance tests and fixture API
└── src
    ├── main/java/com/astrobookings/client/  # web, service, integration
    ├── main/resources/      # Configuration
    ├── main/webapp/         # JSP views, web.xml, assets
    └── test/java/           # JUnit 5 tests
```

### Architecture Rules
- Keep layers: web -> service -> integration; never skip or reverse.
- Servlets parse input, call one service, set attributes, forward or redirect.
- Services must not import `jakarta.servlet`; keep them unit-testable.
- All API calls go through `ApiClient`; never build `HttpClient` elsewhere.
- JSPs render only: no scriptlets, no API calls, no business logic.
- Use PRG (POST, redirect, GET) with a flash message for every form.
- Throw `ApiException` on API errors; redirect 401 to `/login` with `returnUrl`.
- Show capacity from the API; let the API reject overbooking.
- Escape output with `c:out` or EL; validate all input server-side.
- Add or update a JUnit test with each service or validator change.
- Update PRD statuses and the changelog when a feature is done.

### Routes
- `GET /launches`, `GET /launches/{id}`: public catalog and detail.
- `GET|POST /register`, `GET|POST /login`, `POST /logout`: authentication.
- `GET /bookings`, `POST /bookings`: my bookings and seat booking.
- `/bookings` requires a session; every other route is public.
- API contract, payloads and diagrams: see `ADD.md` and `PRD.md`.

## Environment
- Code and documentation must be in English.
- Chat responses must be in the language of the user prompt.
- Repository remote: https://github.com/dockerscr/cursoIA.git
- Maven is not installed globally; use the Maven Wrapper (`.\mvnw.cmd` on Windows, `./mvnw` on git bash).
- Windows environment; use PowerShell or git bash.
- Mind the available **agent skills** when performing tasks.
- Use the OpenCode agents in `.opencode/`; keep specs in `specs/`.
- Replace {placeholders} in templates with real values.
- Never commit secrets; read configuration from environment variables.
- Default git branch is `main`.

### Naming Conventions

Use slugs with hyphens for any identifiers or non code file names.

| Spec        | GitHub Label  | Git Branch    | Commit  |
|-------------|---------------|---------------|---------|
| feat-<slug> | enhancement   | feat/<slug>   | feat:   |
| bug-<slug>  | bug           | fix/<slug>    | fix:    |
| chore-<slug>| chore         | chore/<slug>  | chore:  |
