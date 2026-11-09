package com.astrobookings.client.service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * View model rendered by the catalog page.
 *
 * <p>Record accessors alone are not visible to JSP EL in WildFly 41 (Jakarta EL
 * resolves bean properties through {@code getXxx}/{@code isXxx} methods only), so
 * this record also exposes explicit JavaBean getters for every value the view
 * renders. The component accessors remain the API used by the service layer.</p>
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

    /** @return launch identifier used to build the detail link */
    public String getId() {
        return id;
    }

    /** @return launch name */
    public String getName() {
        return name;
    }

    /** @return normalised launch date, or {@code null} when it cannot be parsed */
    public LocalDate getDate() {
        return date;
    }

    /** @return human-readable date, always safe to render */
    public String getDateLabel() {
        return dateLabel;
    }

    /** @return launch location */
    public String getLocation() {
        return location;
    }

    /** @return price per seat */
    public BigDecimal getPrice() {
        return price;
    }

    /** @return duration in minutes */
    public int getDurationMinutes() {
        return durationMinutes;
    }

    /** @return maximum number of seats */
    public int getMaxParticipants() {
        return maxParticipants;
    }

    /** @return API status of the launch */
    public String getStatus() {
        return status;
    }

    /** @return {@code true} for published or confirmed launches */
    public boolean isBookable() {
        return bookable;
    }
}
