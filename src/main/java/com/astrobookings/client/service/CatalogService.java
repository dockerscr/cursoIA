package com.astrobookings.client.service;

import com.astrobookings.client.integration.LaunchesApi;
import com.astrobookings.client.integration.dto.ActivityDto;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Catalog use case: filter visible launches and present them in booking order.
 *
 * <p>Contains no {@code jakarta.servlet} types so it stays unit-testable. API
 * failures propagate unchanged as {@link com.astrobookings.client.exception.ApiException}.</p>
 */
public class CatalogService {

    private static final Set<String> VISIBLE_STATUSES = Set.of("published", "confirmed");

    private static final Comparator<LaunchSummary> CATALOG_ORDER =
            Comparator.comparing(LaunchSummary::date, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(LaunchSummary::name, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                    .thenComparing(LaunchSummary::id, Comparator.nullsLast(String::compareTo));

    private final LaunchesApi launchesApi;

    /**
     * @param launchesApi facade used to read activities; never {@code null}
     */
    public CatalogService(LaunchesApi launchesApi) {
        this.launchesApi = Objects.requireNonNull(launchesApi, "launchesApi");
    }

    /**
     * Reads every activity and returns the catalog rows.
     *
     * <p>Only {@code published} and {@code confirmed} launches are kept. Rows are
     * ordered by launch date ascending; launches without a parseable date sort last
     * and remain visible. Ties are broken by name.</p>
     *
     * @return ordered visible launches, empty when nothing is visible
     * @throws com.astrobookings.client.exception.ApiException when the API call fails
     */
    public List<LaunchSummary> getVisibleLaunches() {
        List<ActivityDto> activities = launchesApi.findAll();
        if (activities == null || activities.isEmpty()) {
            return List.of();
        }
        return activities.stream()
                .filter(this::isVisible)
                .map(CatalogService::toSummary)
                .sorted(CATALOG_ORDER)
                .toList();
    }

    private boolean isVisible(ActivityDto activity) {
        if (activity == null || activity.status() == null) {
            return false;
        }
        return VISIBLE_STATUSES.contains(activity.status().trim().toLowerCase(Locale.ROOT));
    }

    private static LaunchSummary toSummary(ActivityDto activity) {
        LocalDate date = LaunchDates.parse(activity.date());
        return new LaunchSummary(
                activity.id(),
                activity.name(),
                date,
                LaunchDates.label(date),
                activity.location(),
                activity.price(),
                activity.duration(),
                activity.maxParticipants(),
                activity.status(),
                true);
    }
}
