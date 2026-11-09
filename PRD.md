# AstroBookings Client Product Requirements Document

A Java web application (Servlets + JSP) that consumes the AstroBookings REST API, letting customers browse rocket launches, book seats, and review their own bookings.

## Vision and Scope

AstroBookings is a fictional space travel company used for training and demos. The existing backend exposes launches and bookings only as a JSON API; this project delivers the customer-facing web channel so the system can be demonstrated end to end.

Target users are customers (space tourists) who want to find a launch, reserve seats, and check their reservations, plus students and instructors evaluating a spec-driven development workflow. The product solves the lack of a usable interface over the API and exercises the full client lifecycle: catalog, authentication, booking, and booking history.

### Scope

In scope:

- Public catalog of launches with details and search/sort.
- Launch detail page and seat booking flow with capacity validation.
- Customer registration, login, logout, and JWT-based session.
- "My bookings" view for the authenticated customer.
- Friendly error handling for API failures and overbooking.

Out of scope:

- Payments and billing.
- Launch administration (create, edit, delete, publish) — the client is read-only for launches.
- Database or client-side persistence; the API is the single source of truth.
- Production-grade security (OAuth, password recovery, email verification) and i18n.
- Native mobile application.

## Functional Requirements

### FR1 Browse the launch catalog
- **Description**: Display the launches from `GET /activities` on the home page, showing name, date, location, price, duration, and seat capacity for each entry.
- **Priority**: High
- **Status**: Implemented

### FR2 View launch details
- **Description**: Provide a dedicated page per launch using `GET /activities/:id` with full details and a call-to-action to book seats.
- **Priority**: High
- **Status**: NotStarted

### FR3 Search and sort launches
- **Description**: Let visitors filter the catalog by text and sort by date or price using the API query parameters `q`, `_sort`, and `_order`.
- **Priority**: Medium
- **Status**: NotStarted

### FR4 Register a customer account
- **Description**: Provide a registration form that creates an account via `POST /users` with email, username, password, and accepted terms, reporting validation errors inline.
- **Priority**: High
- **Status**: NotStarted

### FR5 Log in and log out
- **Description**: Authenticate customers via `POST /login`, keep the returned JWT in the server-side HTTP session, and allow logout by invalidating that session.
- **Priority**: High
- **Status**: NotStarted

### FR6 Book seats on a launch
- **Description**: Allow an authenticated customer to book 1 to N seats via `POST /bookings`, validating the participant count against launch capacity and surfacing API rejections when seats are exhausted.
- **Priority**: High
- **Status**: NotStarted

### FR7 Review my bookings
- **Description**: Show the authenticated customer's bookings from `GET /bookings`, optionally filtered by `activityId`, with launch summary, participant count, booking status, and date.
- **Priority**: High
- **Status**: NotStarted

## Technical Requirements

### TR1 Java Servlet and JSP application
- **Description**: Build a Jakarta EE web application using Servlets, JSP and JSTL, managed with Maven, targeting JDK 27 and packaged as a WAR for Wildfly 41.
- **Priority**: High
- **Status**: NotStarted

### TR2 Centralized API client
- **Description**: Encapsulate all calls to the AstroBookings API in a single client using `java.net.http.HttpClient`, with configurable base URL, timeouts, and JSON serialization.
- **Priority**: High
- **Status**: NotStarted

### TR3 JWT session authentication
- **Description**: Store the JWT exclusively in the `HttpSession` and attach it as `Authorization: Bearer` to protected API calls; never expose it in JSP output or client-side storage.
- **Priority**: High
- **Status**: NotStarted

### TR4 Validation and error handling
- **Description**: Apply server-side validation to every form and handle API 4xx/5xx responses consistently, redirecting 401 to login and mapping capacity conflicts back to the launch page.
- **Priority**: High
- **Status**: NotStarted

### TR5 Automated tests
- **Description**: Cover validators, services, and servlet controllers with JUnit 5 tests using a stubbed API client, without live network calls.
- **Priority**: Medium
- **Status**: NotStarted

## Assumptions and Open Questions

- A "launch" maps to an API **activity**. The API model has no rocket field; rocket information must come from the activity name or be added to the API.
- `GET /activities` does not expose remaining seats, so the UI shows maximum capacity and relies on the API to reject overbooking. An availability field is a candidate API enhancement.
- Catalog reads are public; booking endpoints require the JWT and only return the authenticated customer's bookings.
- Demo constraints (JSON file persistence, mock payment gateway, no production security) are inherited from the API and accepted.
