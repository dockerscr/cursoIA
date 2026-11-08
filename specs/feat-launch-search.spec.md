# Launch Search and Sort Specification

- **Reference**: `PRD.md` FR3
- **Issue**: _to be created_
- **Status**: Draft

## Problem Description

A flat catalog becomes hard to use as the number of launches grows. Visitors need to find launches by name or location and to compare them by date or price.

### User Stories

- As a visitor, I want to **search launches by text** so that I can find a destination or mission quickly.
- As a visitor, I want to **sort launches by price** so that I can compare affordable options.
- As a visitor, I want to **sort launches by date** so that I can see the next departures first.

## Solution Overview

### User/App interface

- Search field and sort selector added to the `/launches` catalog page.
- Sort options: date ascending, date descending, price ascending, price descending.
- Submitted search term and sort choice remain visible with the results.
- Clear-search action returns to the full catalog.

### Model and logic

- Search term is trimmed; an empty term is treated as no filter.
- The term is sent as the API `q` parameter.
- Sorting is sent as `_sort` and `_order` parameters.
- Default sort is date ascending when no option is selected.
- Status filtering from the catalog specification still applies.

### Persistence

- Filtering and sorting are delegated to the API query parameters.
- No local index, cache or storage.

## Acceptance Criteria

- [ ] THE AstroBookings Client SHALL provide a text search field on the catalog page.
- [ ] WHEN a visitor submits a non-empty search term THE AstroBookings Client SHALL request launches filtered by that term.
- [ ] WHEN the API returns matching launches THE AstroBookings Client SHALL display those launches.
- [ ] IF no launch matches the term THEN THE AstroBookings Client SHALL display a no-results message with a clear-search action.
- [ ] THE AstroBookings Client SHALL provide sort options for date and price in ascending and descending order.
- [ ] WHEN a visitor selects a sort option THE AstroBookings Client SHALL request launches sorted by the selected field and order.
- [ ] THE AstroBookings Client SHALL keep the submitted search term and sort option visible in the form.
- [ ] WHEN a visitor clears the search THE AstroBookings Client SHALL display the unfiltered catalog.
- [ ] WHERE no sort option is selected THE AstroBookings Client SHALL order the catalog by date ascending.
