# Customer Registration Specification

- **Reference**: `PRD.md` FR4
- **Issue**: _to be created_
- **Status**: Draft

## Problem Description

Bookings require an authenticated customer, but the client has no way to create an account. Without registration, new visitors cannot become customers and cannot book seats.

### User Stories

- As a visitor, I want to **create an account with my email and username** so that I can book launches.
- As a visitor, I want to **see clear validation messages** so that I can fix mistakes before submitting.
- As a newly registered customer, I want to **reach the login page with my email ready** so that I can sign in immediately.

## Solution Overview

### User/App interface

- Registration page at `/register` with email, username, password and terms acceptance fields.
- Inline field errors; a form-level error for API failures.
- Success redirects to `/login` with a success message and the email prefilled.
- Link to the login page for existing customers.

### Model and logic

- Required fields: email, username, password and accepted terms.
- Email must have a valid format; username at least 3 characters; password at least 8 characters.
- The password is submitted only to the API and never stored by the client.
- API errors are mapped to fields, such as an email that already exists.
- Entered values are preserved after a failed submission, except the password.

### Persistence

- Account creation is delegated to the API user registration endpoint.
- No local user store; the client keeps no credentials.

## Acceptance Criteria

- [ ] WHEN a visitor requests `/register` THE AstroBookings Client SHALL display a form with email, username, password and terms acceptance.
- [ ] IF a required field is empty THEN THE AstroBookings Client SHALL display an inline error and SHALL NOT submit the form.
- [ ] IF the email format is invalid or the password has fewer than 8 characters or the username has fewer than 3 characters THEN THE AstroBookings Client SHALL display an inline error and SHALL NOT submit the form.
- [ ] IF the terms are not accepted THEN THE AstroBookings Client SHALL display an inline error and SHALL NOT submit the form.
- [ ] WHEN valid data is submitted THE AstroBookings Client SHALL create the account through the API.
- [ ] WHEN the account is created THE AstroBookings Client SHALL redirect to the login page with a success message and the email prefilled.
- [ ] IF the API reports that the email already exists THEN THE AstroBookings Client SHALL display the error next to the email field.
- [ ] IF the API is unavailable THEN THE AstroBookings Client SHALL display a retry message and preserve the non-password fields.
- [ ] THE AstroBookings Client SHALL never display or log the submitted password.
