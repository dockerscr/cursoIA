package com.astrobookings.client.integration;

import com.astrobookings.client.integration.dto.ActivityDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LaunchesApiTest {

    @Mock
    private ApiClient apiClient;

    @InjectMocks
    private LaunchesApi launchesApi;

    @Test
    void findAllDelegatesToActivitiesEndpoint() {
        ActivityDto activity = new ActivityDto(
                "activity-2", "City Bike Tour", "city-bike-tour", new BigDecimal("35"),
                "2026-12-20", 120, "City Center", 3, 15, "published");
        when(apiClient.getList(LaunchesApi.ACTIVITIES_PATH, ActivityDto.class))
                .thenReturn(List.of(activity));

        List<ActivityDto> result = launchesApi.findAll();

        assertThat(result).containsExactly(activity);
        verify(apiClient).getList("/activities", ActivityDto.class);
    }
}
