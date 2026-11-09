package com.astrobookings.client.service;

import com.astrobookings.client.exception.ApiException;
import com.astrobookings.client.integration.LaunchesApi;
import com.astrobookings.client.integration.dto.ActivityDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private LaunchesApi launchesApi;

    private CatalogService catalogService;

    @BeforeEach
    void setUp() {
        catalogService = new CatalogService(launchesApi);
    }

    @Test
    void keepsOnlyPublishedAndConfirmedLaunches() {
        when(launchesApi.findAll()).thenReturn(List.of(
                activity("draft", "Draft launch", "2026-01-01", "draft"),
                activity("done", "Done launch", "2026-01-02", "done"),
                activity("cancelled", "Cancelled launch", "2026-01-03", "cancelled"),
                activity("published", "Published launch", "2026-01-04", "published"),
                activity("confirmed", "Confirmed launch", "2026-01-05", "confirmed"),
                activity("mixed", "Mixed case launch", "2026-01-06", " Published ")));

        List<LaunchSummary> visible = catalogService.getVisibleLaunches();

        assertThat(visible)
                .extracting(LaunchSummary::id)
                .containsExactly("published", "confirmed", "mixed");
    }

    @Test
    void ordersByDateAscendingAcrossMixedDateFormats() {
        when(launchesApi.findAll()).thenReturn(List.of(
                activity("activity-2", "City Bike Tour", "2026-12-20", "published"),
                activity("activity-3", "Cooking Workshop: Italian Cuisine", "2026-08-25T14:00:00.000Z", "published"),
                activity("activity-1", "Surf", "2026-10-26", "draft"),
                activity("activity-4", "Photography Walk", "2027-03-01T09:00:00.000Z", "published")));

        List<LaunchSummary> visible = catalogService.getVisibleLaunches();

        assertThat(visible)
                .extracting(LaunchSummary::id)
                .containsExactly("activity-3", "activity-2", "activity-4");
    }

    @Test
    void usesNameAsTieBreakerForEqualDates() {
        when(launchesApi.findAll()).thenReturn(List.of(
                activity("zulu", "Zulu mission", "2026-08-25", "published"),
                activity("alpha", "Alpha mission", "2026-08-25T14:00:00.000Z", "published")));

        List<LaunchSummary> visible = catalogService.getVisibleLaunches();

        assertThat(visible)
                .extracting(LaunchSummary::id)
                .containsExactly("alpha", "zulu");
    }

    @Test
    void unparseableDatesSortLastButRemainVisible() {
        when(launchesApi.findAll()).thenReturn(List.of(
                activity("unknown", "Unknown date", null, "published"),
                activity("bad", "Bad date", "not-a-date", "published"),
                activity("dated", "Dated launch", "2026-08-25", "confirmed")));

        List<LaunchSummary> visible = catalogService.getVisibleLaunches();

        assertThat(visible)
                .extracting(LaunchSummary::id)
                .containsExactly("dated", "bad", "unknown");
        assertThat(visible.get(1).dateLabel()).isEqualTo(LaunchDates.UNKNOWN_LABEL);
        assertThat(visible.get(2).dateLabel()).isEqualTo(LaunchDates.UNKNOWN_LABEL);
    }

    @Test
    void mapsApiFieldsToViewSummary() {
        when(launchesApi.findAll()).thenReturn(List.of(
                new ActivityDto(
                        "activity-3", "Cooking Workshop: Italian Cuisine", "cooking-workshop-italian-cuisine",
                        new BigDecimal("75"), "2026-08-25T14:00:00.000Z", 180,
                        "Culinary Studio", 4, 12, "published")));

        LaunchSummary summary = catalogService.getVisibleLaunches().get(0);

        assertThat(summary.id()).isEqualTo("activity-3");
        assertThat(summary.name()).isEqualTo("Cooking Workshop: Italian Cuisine");
        assertThat(summary.date()).isEqualTo(LocalDate.of(2026, 8, 25));
        assertThat(summary.dateLabel()).isEqualTo("25 Aug 2026");
        assertThat(summary.location()).isEqualTo("Culinary Studio");
        assertThat(summary.price()).isEqualByComparingTo(new BigDecimal("75"));
        assertThat(summary.durationMinutes()).isEqualTo(180);
        assertThat(summary.maxParticipants()).isEqualTo(12);
        assertThat(summary.status()).isEqualTo("published");
        assertThat(summary.bookable()).isTrue();
    }

    @Test
    void emptyApiResultYieldsEmptyCatalog() {
        when(launchesApi.findAll()).thenReturn(List.of());

        assertThat(catalogService.getVisibleLaunches()).isEmpty();
    }

    @Test
    void nullApiResultYieldsEmptyCatalog() {
        when(launchesApi.findAll()).thenReturn(null);

        assertThat(catalogService.getVisibleLaunches()).isEmpty();
    }

    @Test
    void apiExceptionPropagatesUnchanged() {
        ApiException failure = ApiException.apiError(503, "API down");
        when(launchesApi.findAll()).thenThrow(failure);

        assertThatThrownBy(() -> catalogService.getVisibleLaunches()).isSameAs(failure);
    }

    private static ActivityDto activity(String id, String name, String date, String status) {
        return new ActivityDto(
                id, name, id, new BigDecimal("35"), date, 120,
                "Location", 2, 10, status);
    }
}
