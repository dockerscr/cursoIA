package com.astrobookings.client.integration;

import com.astrobookings.client.config.AppConfig;
import com.astrobookings.client.exception.ApiException;
import com.astrobookings.client.integration.dto.ActivityDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Exercises {@link ApiClient} against a loopback HTTP server; never calls the real API.
 */
class ApiClientTest {

    private static final String ACTIVITIES_JSON = """
            [
              {
                "id": "activity-3",
                "name": "Cooking Workshop: Italian Cuisine",
                "slug": "cooking-workshop-italian-cuisine",
                "price": 75,
                "date": "2026-08-25T14:00:00.000Z",
                "duration": 180,
                "location": "Culinary Studio",
                "minParticipants": 4,
                "maxParticipants": 12,
                "status": "published",
                "userId": "user-1",
                "createdAt": "2026-01-17T10:00:00.000Z",
                "unknownFutureField": true
              },
              {
                "id": "activity-2",
                "name": "City Bike Tour",
                "slug": "city-bike-tour",
                "price": 35,
                "date": "2026-12-20",
                "duration": 120,
                "location": "City Center",
                "minParticipants": 3,
                "maxParticipants": 15,
                "status": "published"
              }
            ]
            """;

    private HttpServer server;
    private String baseUrl;

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0), 0);
        server.createContext("/activities", exchange -> respond(exchange, 200, ACTIVITIES_JSON));
        server.createContext("/objects", exchange -> respond(exchange, 200, "{\"id\":\"activity-2\",\"name\":\"City Bike Tour\"}"));
        server.createContext("/empty", exchange -> respond(exchange, 200, "[]"));
        server.createContext("/missing", exchange -> respond(exchange, 404, "{\"message\":\"not found\"}"));
        server.createContext("/boom", exchange -> respond(exchange, 500, "{\"message\":\"secret internals\"}"));
        server.createContext("/malformed", exchange -> respond(exchange, 200, "{ not valid json"));
        server.createContext("/slow", exchange -> {
            sleep(500);
            respond(exchange, 200, "[]");
        });
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    @Test
    void getListMapsArrayAndIgnoresUnknownFields() {
        ApiClient client = new ApiClient(AppConfig.fromValues(baseUrl));

        List<ActivityDto> activities = client.getList("/activities", ActivityDto.class);

        assertThat(activities).hasSize(2);
        ActivityDto first = activities.get(0);
        assertThat(first.id()).isEqualTo("activity-3");
        assertThat(first.name()).isEqualTo("Cooking Workshop: Italian Cuisine");
        assertThat(first.price()).isEqualByComparingTo(new BigDecimal("75"));
        assertThat(first.date()).isEqualTo("2026-08-25T14:00:00.000Z");
        assertThat(first.duration()).isEqualTo(180);
        assertThat(first.maxParticipants()).isEqualTo(12);
        assertThat(first.status()).isEqualTo("published");
    }

    @Test
    void getListSupportsAnEmptyArray() {
        ApiClient client = new ApiClient(AppConfig.fromValues(baseUrl));

        assertThat(client.getList("/empty", ActivityDto.class)).isEmpty();
    }

    @Test
    void getJsonParsesResponses() {
        ApiClient client = new ApiClient(AppConfig.fromValues(baseUrl));

        JsonNode node = client.getJson("/objects");

        assertThat(node.path("id").asText()).isEqualTo("activity-2");
        assertThat(node.path("name").asText()).isEqualTo("City Bike Tour");
    }

    @Test
    void non2xxResponseCarriesStatusAndSafeMessage() {
        ApiClient client = new ApiClient(AppConfig.fromValues(baseUrl));

        assertThatThrownBy(() -> client.getString("/missing"))
                .isInstanceOf(ApiException.class)
                .satisfies(error -> {
                    ApiException apiException = (ApiException) error;
                    assertThat(apiException.getStatus()).isEqualTo(404);
                    assertThat(apiException.getMessage()).isEqualTo("The requested resource was not found.");
                });
    }

    @Test
    void serverErrorDoesNotLeakPayload() {
        ApiClient client = new ApiClient(AppConfig.fromValues(baseUrl));

        assertThatThrownBy(() -> client.getString("/boom"))
                .isInstanceOf(ApiException.class)
                .hasMessageNotContaining("secret internals")
                .satisfies(error -> assertThat(((ApiException) error).getStatus()).isEqualTo(500));
    }

    @Test
    void malformedJsonBecomesInvalidPayloadError() {
        ApiClient client = new ApiClient(AppConfig.fromValues(baseUrl));

        assertThatThrownBy(() -> client.getList("/malformed", ActivityDto.class))
                .isInstanceOf(ApiException.class)
                .satisfies(error -> assertThat(((ApiException) error).getStatus()).isEqualTo(502));
    }

    @Test
    void timeoutBecomesNetworkError() {
        AppConfig config = new AppConfig(baseUrl, Duration.ofSeconds(1), Duration.ofMillis(100));
        ApiClient client = new ApiClient(config);

        assertThatThrownBy(() -> client.getString("/slow"))
                .isInstanceOf(ApiException.class)
                .satisfies(error -> {
                    ApiException apiException = (ApiException) error;
                    assertThat(apiException.isNetworkError()).isTrue();
                    assertThat(apiException.getStatus()).isEqualTo(ApiException.NETWORK_ERROR_STATUS);
                });
    }

    private static void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
        exchange.close();
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
