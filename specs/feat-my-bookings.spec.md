# My Bookings Specification

- **Reference**: `PRD.md` FR7
- **Issue**: _to be created_
- **Status**: Draft

## Problem Description

After booking, customers have no way to confirm their reservations or check their status. Without a history view, a booking is a blind action and support questions cannot be answered from the app.

### User Stories

- As a customer, I want to **see all my bookings in one place** so that I can track my reservations.
- As a customer, I want to **check the status of each booking** so that I know if it is confirmed.
- As a customer, I want to **open the related launch** so that I can review its details.

## Solution Overview

### User/App interface

- Protected page at `/bookings`, linked from the navigation for authenticated customers.
- Each entry shows launch name, launch date, participant count and booking status.
- Optional filter by launch when the page is opened with an `activityId` parameter.
- Link from each booking to `/launches/{id}`.
- Empty state with a link to the catalog; error state with a retry action.

### Model and logic

- Only the authenticated customer's bookings are requested.
- The API token is sent with the request; no customer identifier is taken from the browser.
- Bookings are ordered newest first.
- A 401 response clears the session and redirects to login.

### Persistence

- Bookings are read from the API on every request.
- No local booking store; the API remains the source of truth.

## Acceptance Criteria

- [ ] WHILE no customer session exists THE AstroBookings Client SHALL redirect `/bookings` requests to the login page with `/bookings` as return target.
- [ ] WHEN an authenticated customer requests `/bookings` THE AstroBookings Client SHALL retrieve the customer bookings from the API with the session token.
- [ ] THE AstroBookings Client SHALL display launch name, launch date, participant count and booking status for each booking.
- [ ] WHEN `activityId` is present in the request THE AstroBookings Client SHALL display only bookings for that launch.
- [ ] IF the customer has no bookings THEN THE AstroBookings Client SHALL display an empty-state message with a link to the catalog.
- [ ] IF the API responds with 401 THEN THE AstroBookings Client SHALL clear the session and redirect to login.
- [ ] IF the API request fails THEN THE AstroBookings Client SHALL display an error message and SHALL NOT show the empty state.
- [ ] THE AstroBookings Client SHALL order the bookings from newest to oldest.
- [ ] THE AstroBookings Client SHALL provide a link from each booking to the corresponding launch detail page.
