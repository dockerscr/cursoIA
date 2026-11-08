package com.astrobookings.client.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Tolerant date normalisation for launch payloads.
 *
 * <p>The API returns both date-only values and full ISO datetimes. Only the first
 * ten characters ({@code yyyy-MM-dd}) are relevant for display and ordering.</p>
 */
final class LaunchDates {

    /** Deterministic label, independent of the server default locale. */
    private static final DateTimeFormatter LABEL_FORMAT =
            DateTimeFormatter.ofPattern("d MMM uuuu", Locale.ENGLISH);

    /** Rendered when the API value is missing or cannot be parsed. */
    static final String UNKNOWN_LABEL = "Date to be confirmed";

    private static final int DATE_ONLY_LENGTH = 10;

    private LaunchDates() {
    }

    /**
     * Parses the date prefix of an API value.
     *
     * @param raw date-only or ISO datetime value, may be {@code null}
     * @return the normalised date, or {@code null} when it cannot be parsed
     */
    static LocalDate parse(String raw) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        if (trimmed.length() < DATE_ONLY_LENGTH) {
            return null;
        }
        try {
            return LocalDate.parse(trimmed.substring(0, DATE_ONLY_LENGTH));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Builds the label shown in the catalog.
     *
     * @param date normalised date, may be {@code null}
     * @return formatted date such as {@code 25 Aug 2026}, or {@link #UNKNOWN_LABEL}
     */
    static String label(LocalDate date) {
        return date == null ? UNKNOWN_LABEL : LABEL_FORMAT.format(date);
    }
}
