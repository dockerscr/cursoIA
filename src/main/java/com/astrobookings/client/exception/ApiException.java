package com.astrobookings.client.exception;

/**
 * Failure raised when the ActivityBookings API cannot satisfy a request.
 *
 * <p>The message is always safe to show to a visitor: it never contains tokens,
 * passwords, raw payloads or stack traces. The HTTP status is kept so callers can
 * react to well-known codes such as {@code 401} or {@code 404}.</p>
 */
public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** Status used when the API could not be reached at all. */
    public static final int NETWORK_ERROR_STATUS = 0;

    private final int status;

    private ApiException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    /**
     * Creates an exception for a non-2xx API response.
     *
     * @param status  HTTP status returned by the API
     * @param message user-safe description of the failure
     * @return a new exception carrying the status
     */
    public static ApiException apiError(int status, String message) {
        return new ApiException(status, message, null);
    }

    /**
     * Creates an exception for a transport failure (connection refused, timeout, ...).
     *
     * @param cause low-level failure kept for diagnostics; never shown to visitors
     * @return a new exception with {@link #NETWORK_ERROR_STATUS}
     */
    public static ApiException networkError(Throwable cause) {
        return new ApiException(
                NETWORK_ERROR_STATUS,
                "The ActivityBookings service is unavailable. Please try again later.",
                cause);
    }

    /**
     * @return HTTP status returned by the API, or {@link #NETWORK_ERROR_STATUS} for transport failures
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return {@code true} when the API could not be reached
     */
    public boolean isNetworkError() {
        return status == NETWORK_ERROR_STATUS;
    }
}
