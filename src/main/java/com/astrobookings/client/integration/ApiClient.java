package com.astrobookings.client.integration;

import com.astrobookings.client.config.AppConfig;
import com.astrobookings.client.exception.ApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * Single entry point for HTTP access to the ActivityBookings API.
 *
 * <p>Exactly one {@link HttpClient} is created per application instance, using the
 * timeouts from {@link AppConfig}. Every call maps non-2xx responses to an
 * {@link ApiException} carrying the status, and transport failures to a network
 * {@code ApiException}. Logs contain the method, path and status only; bodies,
 * tokens and passwords are never logged.</p>
 */
public class ApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(ApiClient.class);

    private static final String ACCEPT_HEADER = "application/json";
    private static final int INVALID_PAYLOAD_STATUS = 502;
    private static final String INVALID_PAYLOAD_MESSAGE =
            "The ActivityBookings service returned an invalid response.";

    private final AppConfig config;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    /**
     * Creates the production client, building the only {@link HttpClient} of the application.
     *
     * @param config base URL and timeouts
     */
    public ApiClient(AppConfig config) {
        this(config, new ObjectMapper());
    }

    ApiClient(AppConfig config, ObjectMapper objectMapper) {
        this(config, objectMapper, newHttpClient(config));
    }

    ApiClient(AppConfig config, ObjectMapper objectMapper, HttpClient httpClient) {
        this.config = Objects.requireNonNull(config, "config");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper");
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient");
    }

    /**
     * Performs a GET and returns the raw response body.
     *
     * @param path absolute API path, for example {@code /activities}
     * @return response body as text
     * @throws ApiException when the API returns a non-2xx status or cannot be reached
     */
    public String getString(String path) {
        Objects.requireNonNull(path, "path");

        HttpRequest request = HttpRequest.newBuilder(resolve(path))
                .timeout(config.requestTimeout())
                .header("Accept", ACCEPT_HEADER)
                .GET()
                .build();

        try {
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            int status = response.statusCode();
            LOG.debug("GET {} -> {}", path, status);
            if (status < 200 || status >= 300) {
                LOG.warn("GET {} failed with status {}", path, status);
                throw ApiException.apiError(status, messageFor(status));
            }
            return response.body();
        } catch (IOException e) {
            LOG.warn("GET {} failed with {}", path, e.getClass().getSimpleName());
            throw ApiException.networkError(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.warn("GET {} interrupted", path);
            throw ApiException.networkError(e);
        }
    }

    /**
     * Performs a GET and reads the body as a JSON tree.
     *
     * @param path absolute API path
     * @return parsed JSON tree
     * @throws ApiException on transport, status or parsing failure
     */
    public JsonNode getJson(String path) {
        String body = getString(path);
        try {
            return objectMapper.readTree(body);
        } catch (JsonProcessingException e) {
            LOG.warn("GET {} returned malformed JSON", path);
            throw ApiException.apiError(INVALID_PAYLOAD_STATUS, INVALID_PAYLOAD_MESSAGE);
        }
    }

    /**
     * Performs a GET and maps a JSON array to a typed list.
     *
     * @param path        absolute API path
     * @param elementType type of each array element
     * @param <T>         element type
     * @return the mapped list, never {@code null}
     * @throws ApiException on transport, status or parsing failure
     */
    public <T> List<T> getList(String path, Class<T> elementType) {
        Objects.requireNonNull(elementType, "elementType");
        String body = getString(path);
        try {
            JavaType listType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, elementType);
            return objectMapper.readValue(body, listType);
        } catch (JsonProcessingException e) {
            LOG.warn("GET {} returned JSON that cannot be mapped to {}", path, elementType.getSimpleName());
            throw ApiException.apiError(INVALID_PAYLOAD_STATUS, INVALID_PAYLOAD_MESSAGE);
        }
    }

    private URI resolve(String path) {
        String base = config.apiBaseUrl();
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        try {
            return URI.create(base + normalizedPath);
        } catch (IllegalArgumentException e) {
            LOG.warn("GET {} has an invalid URI", path);
            throw ApiException.networkError(e);
        }
    }

    private static HttpClient newHttpClient(AppConfig config) {
        return HttpClient.newBuilder()
                .connectTimeout(config.connectTimeout())
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    private static String messageFor(int status) {
        if (status == 401 || status == 403) {
            return "Authentication is required to perform this action.";
        }
        if (status == 404) {
            return "The requested resource was not found.";
        }
        if (status >= 500) {
            return "The ActivityBookings service is temporarily unavailable.";
        }
        return "The ActivityBookings service rejected the request.";
    }
}
