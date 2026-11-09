# Launch Catalog Specification

- **Reference**: `PRD.md` FR1
- **Issue**: [`specs/feat-launch-catalog.issue.json`](./feat-launch-catalog.issue.json)
- **Status**: Done

## Problem Description

The API exposes launches only as JSON, so visitors have no way to discover what is available. Without a catalog page, no customer can start a booking journey.

### User Stories

- As a visitor, I want to **browse available rocket launches** so that I can choose one to book.
- As a visitor, I want to **see price and seat capacity at a glance** so that I can compare launches before deciding.
- As a visitor, I want to **know whether a launch is open for booking** so that I do not attempt to reserve a closed launch.

## Solution Overview

### User/App interface

- Catalog page served at `/launches` and reachable from the app navigation.
- Each entry shows launch name, date, location, price, duration and maximum seats.
- Entry links to `/launches/{id}` for details.
- Anonymous visitors also see links to log in and register.
- Empty state when no launch is visible; error state with a retry action when the API fails.

### Model and logic

- The catalog displays launches whose status is published or confirmed.
- Launches with draft, done or cancelled status are excluded.
- Default order is launch date ascending.
- Remaining seats are not exposed by the API; the catalog shows maximum capacity only.

### Persistence

- Launches are read from the API on every request.
- No local storage or caching; the API is the source of truth.

## Acceptance Criteria

- [x] WHEN a visitor requests `/launches` THE AstroBookings Client SHALL retrieve launches from the API.
- [x] THE AstroBookings Client SHALL display name, date, location, price, duration and maximum seats for each listed launch.
- [x] WHERE a launch status is draft, done or cancelled THE AstroBookings Client SHALL exclude it from the catalog.
- [x] THE AstroBookings Client SHALL order the catalog by launch date ascending.
- [x] IF the API returns zero visible launches THEN THE AstroBookings Client SHALL display an empty-state message.
- [x] IF the API request fails THEN THE AstroBookings Client SHALL display an error message and offer a retry action.
- [x] WHEN a visitor selects a launch THE AstroBookings Client SHALL open the launch detail page for that launch.
- [x] WHILE no customer session exists THE AstroBookings Client SHALL display links to log in and register.
