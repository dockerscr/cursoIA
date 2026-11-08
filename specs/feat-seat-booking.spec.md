# Seat Booking Specification

- **Reference**: `PRD.md` FR6
- **Issue**: _to be created_
- **Status**: Draft

## Problem Description

The core purpose of the client is to reserve seats, but no flow creates a booking. Customers need a safe way to submit reservations that respects launch capacity and reports the outcome clearly.

### User Stories

- As a customer, I want to **book a number of seats on a launch** so that I can secure my place.
- As a customer, I want to **be told when seats are no longer available** so that I can choose another launch.
- As a customer, I want to **see confirmation of my reservation** so that I know the booking succeeded.

## Solution Overview

### User/App interface

- Booking form on the launch detail page with a participant count field and submit action.
- Success redirects to `/bookings` with a success message naming the launch and seat count.
- Capacity rejection returns to the launch page with an error and no success message.
- Unauthenticated visitors see login and register links instead of the form.

### Model and logic

- A booking requires an authenticated session.
- Participant count is an integer from 1 to the launch maximum participants.
- The client sends the launch identifier and participant count; the API owns the final capacity check.
- A 401 response clears the session and sends the customer to login with the launch as return target.
- Every attempt ends with POST-redirect-GET to avoid duplicate submissions.

### Persistence

- Bookings are created and verified through the API with the session token.
- The client stores no booking data locally; state is read back from the API.

## Acceptance Criteria

- [ ] WHILE no customer session exists THE AstroBookings Client SHALL NOT submit a booking request.
- [ ] WHEN a customer submits a booking THE AstroBookings Client SHALL send the launch identifier and participant count to the API with the session token.
- [ ] IF the participant count is less than 1 or greater than maximum participants THEN THE AstroBookings Client SHALL display an inline error and SHALL NOT call the API.
- [ ] WHEN the API confirms the booking THE AstroBookings Client SHALL redirect to `/bookings` with a success message showing the launch name and participant count.
- [ ] IF the API rejects the booking for insufficient seats THEN THE AstroBookings Client SHALL return to the launch detail page with an error message and no success message.
- [ ] IF the API responds with 401 THEN THE AstroBookings Client SHALL clear the session and redirect to login with the launch page as return target.
- [ ] IF the API is unavailable THEN THE AstroBookings Client SHALL display an error message and keep the entered participant count.
- [ ] THE AstroBookings Client SHALL use POST-redirect-GET for every booking attempt to avoid duplicate submissions.
- [ ] THE AstroBookings Client SHALL display the booking status returned by the API for a confirmed booking.
