package com.astrobookings.client.integration;

import com.astrobookings.client.integration.dto.ActivityDto;

import java.util.List;
import java.util.Objects;

/**
 * Facade over the launch endpoints of the ActivityBookings API.
 *
 * <p>Kept as a plain, non-final class with constructor injection so services can
 * receive a Mockito stub in unit tests.</p>
 */
public class LaunchesApi {

    static final String ACTIVITIES_PATH = "/activities";

    private final ApiClient apiClient;

    /**
     * @param apiClient shared HTTP client; never {@code null}
     */
    public LaunchesApi(ApiClient apiClient) {
        this.apiClient = Objects.requireNonNull(apiClient, "apiClient");
    }

    /**
     * Retrieves every activity known to the API.
     *
     * @return all activities, unfiltered and in API order
     * @throws com.astrobookings.client.exception.ApiException when the API call fails
     */
    public List<ActivityDto> findAll() {
        return apiClient.getList(ACTIVITIES_PATH, ActivityDto.class);
    }
}
