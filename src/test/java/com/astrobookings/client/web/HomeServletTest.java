package com.astrobookings.client.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HomeServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Test
    void redirectsContextRootToCatalog() throws Exception {
        when(request.getContextPath()).thenReturn("/astrobookings-client");

        new HomeServlet().doGet(request, response);

        verify(response).sendRedirect("/astrobookings-client/launches");
    }
}
