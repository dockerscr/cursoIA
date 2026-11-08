package com.astrobookings.client.web;

import com.astrobookings.client.exception.ApiException;
import com.astrobookings.client.service.CatalogService;
import com.astrobookings.client.service.LaunchSummary;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServletTest {

    @Mock
    private CatalogService catalogService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @InjectMocks
    private CatalogServlet servlet;

    @Test
    void forwardsCatalogWhenServiceSucceeds() throws Exception {
        List<LaunchSummary> launches = List.of(new LaunchSummary(
                "activity-3", "Cooking Workshop", LocalDate.of(2026, 8, 25), "25 Aug 2026",
                "Culinary Studio", new BigDecimal("75"), 180, 12, "published", true));
        when(catalogService.getVisibleLaunches()).thenReturn(launches);
        when(request.getRequestDispatcher(CatalogServlet.VIEW)).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(CatalogServlet.LAUNCHES_ATTRIBUTE, launches);
        verify(request, never()).setAttribute(eq(CatalogServlet.ERROR_ATTRIBUTE), any());
        verify(dispatcher).forward(request, response);
    }

    @Test
    void showsFriendlyErrorAndStillForwardsWhenApiFails() throws Exception {
        when(catalogService.getVisibleLaunches())
                .thenThrow(ApiException.apiError(503, "raw internal detail"));
        when(request.getRequestDispatcher(CatalogServlet.VIEW)).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(CatalogServlet.ERROR_ATTRIBUTE, CatalogServlet.FRIENDLY_ERROR);
        verify(request, never()).setAttribute(eq(CatalogServlet.LAUNCHES_ATTRIBUTE), any());
        verify(dispatcher).forward(request, response);
    }
}
