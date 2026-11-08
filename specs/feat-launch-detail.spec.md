# Launch Detail Specification

- **Reference**: `PRD.md` FR2
- **Issue**: _to be created_
- **Status**: Draft

## Problem Description

The catalog alone cannot answer a customer's questions about a specific launch, and it offers no place to start a reservation. A detail page is required to present full launch information and the booking entry point.

### User Stories

- As a visitor, I want to **see all details of a launch** so that I can decide whether it fits my plans.
- As a customer, I want to **start a booking from the launch page** so that I can reserve seats without leaving the context.
- As a visitor without an account, I want to **understand that logging in is required** so that I know my next step.

## Solution Overview

### User/App interface

- Detail page served at `/launches/{id}` and linked from the catalog.
- Shows name, date, location, price, duration, minimum participants, maximum participants and status.
- Booking form with a participant count field for authenticated customers.
- Login and register links for anonymous visitors.
- Link back to the catalog; not-found page for unknown launches.

### Model and logic

- The launch is identified by the path identifier.
- A non-numeric or unknown identifier produces a not-found result.
- The booking form appears only for launches with published or confirmed status.
- Participant count must be an integer between 1 and the launch maximum participants.
- Seat availability is enforced by the API, not by the page.

### Persistence

- Launch data is read from the API by identifier on every request.
- No local storage; session state is limited to the customer identity.

## Acceptance Criteria

- [ ] WHEN a visitor requests `/launches/{id}` THE AstroBookings Client SHALL retrieve the launch from the API.
- [ ] THE AstroBookings Client SHALL display name, date, location, price, duration, minimum participants, maximum participants and status.
- [ ] IF the launch does not exist THEN THE AstroBookings Client SHALL return a not-found page with a link to the catalog.
- [ ] WHERE the launch status is draft, done or cancelled THE AstroBookings Client SHALL hide the booking form.
- [ ] WHILE no customer session exists THE AstroBookings Client SHALL show login and register links in place of the booking form.
- [ ] WHILE a customer session exists THE AstroBookings Client SHALL show a booking form with a participants field.
- [ ] IF the submitted participant count is outside the range of 1 to maximum participants THEN THE AstroBookings Client SHALL display an inline error and SHALL NOT call the booking API.
- [ ] WHEN a valid booking is submitted THE AstroBookings Client SHALL send the request to create the booking.
- [ ] IF the API request fails THEN THE AstroBookings Client SHALL display an error message and keep the booking form available.
