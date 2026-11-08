package com.astrobookings.client.config;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class AppConfigTest {

    @Test
    void defaultsToLocalApiWhenNoValueIsProvided() {
        AppConfig config = AppConfig.fromValues(null);

        assertThat(config.apiBaseUrl()).isEqualTo("http://localhost:3000");
        assertThat(config.connectTimeout()).isEqualTo(Duration.ofSeconds(5));
        assertThat(config.requestTimeout()).isEqualTo(Duration.ofSeconds(10));
    }

    @Test
    void blankValueFallsBackToTheDefault() {
        assertThat(AppConfig.fromValues("   ").apiBaseUrl()).isEqualTo("http://localhost:3000");
    }

    @Test
    void overrideIsUsedAndTrailingSlashRemoved() {
        AppConfig config = AppConfig.fromValues("https://api.example.test/");

        assertThat(config.apiBaseUrl()).isEqualTo("https://api.example.test");
    }

    @Test
    void explicitConstructorNormalizesTheUrl() {
        AppConfig config = new AppConfig(
                "http://127.0.0.1:3000/", Duration.ofSeconds(2), Duration.ofSeconds(4));

        assertThat(config.apiBaseUrl()).isEqualTo("http://127.0.0.1:3000");
        assertThat(config.connectTimeout()).isEqualTo(Duration.ofSeconds(2));
        assertThat(config.requestTimeout()).isEqualTo(Duration.ofSeconds(4));
    }
}
