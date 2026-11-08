package com.astrobookings.client.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class LaunchDatesTest {

    @Test
    void parsesDateOnlyValues() {
        assertThat(LaunchDates.parse("2026-12-20")).isEqualTo(LocalDate.of(2026, 12, 20));
    }

    @Test
    void parsesIsoDatetimeValuesFromTheFirstTenCharacters() {
        assertThat(LaunchDates.parse("2026-08-25T14:00:00.000Z")).isEqualTo(LocalDate.of(2026, 8, 25));
    }

    @Test
    void returnsNullForNullBlankShortAndInvalidValues() {
        assertThat(LaunchDates.parse(null)).isNull();
        assertThat(LaunchDates.parse("   ")).isNull();
        assertThat(LaunchDates.parse("2026-8-5")).isNull();
        assertThat(LaunchDates.parse("not-a-date")).isNull();
    }

    @Test
    void formatsLabelsWithFixedEnglishLocale() {
        assertThat(LaunchDates.label(LocalDate.of(2026, 8, 25))).isEqualTo("25 Aug 2026");
        assertThat(LaunchDates.label(LocalDate.of(2026, 12, 20))).isEqualTo("20 Dec 2026");
    }

    @Test
    void unknownDatesGetASafeLabel() {
        assertThat(LaunchDates.label(null)).isEqualTo(LaunchDates.UNKNOWN_LABEL);
    }
}
