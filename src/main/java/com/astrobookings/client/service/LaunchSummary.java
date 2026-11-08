package com.astrobookings.client.service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * View model rendered by the catalog page.
 *
 * @param id               launch identifier used to build the detail link
 * @param name             launch name
 * @param date             normalised launch date, or {@code null} when it cannot be parsed
 * @param dateLabel        human-readable date, always safe to render
 * @param location         launch location
 * @param price            price per seat
 * @param durationMinutes  duration in minutes
 * @param maxParticipants  maximum number of seats
 * @param status           API status of the launch
 * @param bookable         {@code true} for published or confirmed launches
 */
public record LaunchSummary(
        String id,
        String name,
        LocalDate date,
        String dateLabel,
        String location,
        BigDecimal price,
        int durationMinutes,
        int maxParticipants,
        String status,
        boolean bookable) {
}
