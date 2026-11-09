package com.astrobookings.client.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * Payload returned by {@code GET /activities}.
 *
 * <p>The date is kept as a {@link String} because the API mixes date-only values
 * ({@code 2026-12-20}) with ISO datetimes ({@code 2026-08-25T14:00:00.000Z}).
 * Normalisation happens in the service layer.</p>
 *
 * <p>Unknown fields are ignored so additions to the API contract do not break the client.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ActivityDto(
        String id,
        String name,
        String slug,
        BigDecimal price,
        String date,
        int duration,
        String location,
        int minParticipants,
        int maxParticipants,
        String status) {
}
