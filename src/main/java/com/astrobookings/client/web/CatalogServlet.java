package com.astrobookings.client.web;

import com.astrobookings.client.config.AppConfig;
import com.astrobookings.client.exception.ApiException;
import com.astrobookings.client.integration.ApiClient;
import com.astrobookings.client.integration.LaunchesApi;
import com.astrobookings.client.service.CatalogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Public launch catalog at {@code /launches}.
 *
 * <p>Delegates every request to {@link CatalogService} and forwards to the shared
 * catalog view. API failures produce a friendly error state with a retry action
 * instead of a stack trace.</p>
 */
@WebServlet(name = "CatalogServlet", urlPatterns = "/launches")
public class CatalogServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    static final String VIEW = "/WEB-INF/views/launches.jsp";
    static final String LAUNCHES_ATTRIBUTE = "launches";
    static final String ERROR_ATTRIBUTE = "errorMessage";
    static final String FRIENDLY_ERROR =
            "We could not load the launch catalog right now. Please try again in a moment.";

    private static final Logger LOG = LoggerFactory.getLogger(CatalogServlet.class);

    private transient CatalogService catalogService;

    /**
     * Required by the servlet container.
     */
    public CatalogServlet() {
    }

    /**
     * Test seam: allows a stub service without starting the container.
     *
     * @param catalogService service used by {@link #doGet}
     */
    CatalogServlet(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    public void init() throws ServletException {
        AppConfig config = AppConfig.fromEnvironment();
        ApiClient apiClient = new ApiClient(config);
        this.catalogService = new CatalogService(new LaunchesApi(apiClient));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute(LAUNCHES_ATTRIBUTE, catalogService.getVisibleLaunches());
        } catch (ApiException e) {
            LOG.warn("Catalog unavailable: status={}", e.getStatus());
            request.setAttribute(ERROR_ATTRIBUTE, FRIENDLY_ERROR);
        }
        request.getRequestDispatcher(VIEW).forward(request, response);
    }
}
