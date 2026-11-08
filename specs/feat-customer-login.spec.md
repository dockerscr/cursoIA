# Customer Login and Logout Specification

- **Reference**: `PRD.md` FR5
- **Issue**: _to be created_
- **Status**: Draft

## Problem Description

The API issues a token that must accompany protected calls, but the client has no sign-in flow. Customers cannot access their bookings or reserve seats, and the token has no safe place to live.

### User Stories

- As a registered customer, I want to **log in with my email and password** so that I can book and review my reservations.
- As a logged-in customer, I want to **stay signed in while navigating** so that I do not re-enter my credentials on every page.
- As a customer, I want to **log out securely** so that nobody else can use my session.

## Solution Overview

### User/App interface

- Login page at `/login` with email and password fields.
- Logout action available in the navigation while a session exists.
- Navigation shows the customer username and logout when authenticated, or login and register otherwise.
- Invalid credentials show an error on the login page.

### Model and logic

- Successful login stores the token in the server-side session only.
- The token is never rendered, placed in URLs or written to logs.
- The return target is honored only when it is a local path; otherwise the catalog is used.
- Logout invalidates the session and clears authentication state.
- Session expiry returns the customer to the anonymous state.

### Persistence

- The API validates credentials and issues the token.
- The client stores only session data in the WildFly session; there is no token cookie or browser storage.

## Acceptance Criteria

- [ ] WHEN a customer submits valid credentials THE AstroBookings Client SHALL authenticate through the API.
- [ ] WHEN authentication succeeds THE AstroBookings Client SHALL store the token in the server-side session.
- [ ] WHEN authentication succeeds THE AstroBookings Client SHALL redirect to the requested page or to the catalog when no page was requested.
- [ ] IF the credentials are invalid THEN THE AstroBookings Client SHALL display an error message and keep the customer on the login page.
- [ ] IF the requested return page is not a local path THEN THE AstroBookings Client SHALL redirect to the catalog after login.
- [ ] WHILE a customer session exists THE AstroBookings Client SHALL display the username and a logout action.
- [ ] WHEN a customer logs out THE AstroBookings Client SHALL invalidate the session and display the anonymous navigation.
- [ ] WHILE no session exists THE AstroBookings Client SHALL redirect protected page requests to login with the requested page as return target.
- [ ] THE AstroBookings Client SHALL keep the token out of page content, URLs and logs.
