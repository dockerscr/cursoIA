package com.astrobookings.client.config;

import java.time.Duration;
import java.util.Objects;

/**
 * Application configuration read from environment variables with safe local defaults.
 *
 * <p>Only this class knows the names of environment variables and the default values;
 * every other layer receives an instance of this record. No secrets are stored here.</p>
 *
 * @param apiBaseUrl     base URL of the ActivityBookings API, without a trailing slash
 * @param connectTimeout timeout for opening a connection to the API
 * @param requestTimeout timeout for completing an API request
 */
public record AppConfig(String apiBaseUrl, Duration connectTimeout, Duration requestTimeout) {

    /** Environment variable that overrides the API base URL. */
    public static final String API_BASE_URL_ENV = "ASTRO_API_BASE_URL";

    /** Local development default; the API is expected to run at this address. */
    public static final String DEFAULT_API_BASE_URL = "http://localhost:3000";

    /** Non-functional requirement from ADD.md: connect within 5 seconds. */
    public static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(5);

    /** Non-functional requirement from ADD.md: complete the request within 10 seconds. */
    public static final Duration DEFAULT_REQUEST_TIMEOUT = Duration.ofSeconds(10);

    public AppConfig {
        apiBaseUrl = requireUrl(apiBaseUrl);
        Objects.requireNonNull(connectTimeout, "connectTimeout");
        Objects.requireNonNull(requestTimeout, "requestTimeout");
    }

    /**
     * Builds the configuration for the current process.
     *
     * @return configuration using {@code ASTRO_API_BASE_URL} when present, defaults otherwise
     */
    public static AppConfig fromEnvironment() {
        return fromValues(System.getenv(API_BASE_URL_ENV));
    }

    /**
     * Builds a configuration from an explicit base URL value; useful for tests.
     *
     * @param apiBaseUrl raw value, may be {@code null} or blank
     * @return configuration using the default URL when the value is absent
     */
    public static AppConfig fromValues(String apiBaseUrl) {
        String resolved = apiBaseUrl == null || apiBaseUrl.isBlank()
                ? DEFAULT_API_BASE_URL
                : apiBaseUrl.trim();
        return new AppConfig(resolved, DEFAULT_CONNECT_TIMEOUT, DEFAULT_REQUEST_TIMEOUT);
    }

    private static String requireUrl(String value) {
        Objects.requireNonNull(value, "apiBaseUrl");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("apiBaseUrl must not be blank");
        }
        return trimmed.endsWith("/") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
    }
}
