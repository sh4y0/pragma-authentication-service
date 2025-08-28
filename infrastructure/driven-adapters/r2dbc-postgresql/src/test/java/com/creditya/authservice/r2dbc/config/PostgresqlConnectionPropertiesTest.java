package com.creditya.authservice.r2dbc.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class PostgresqlConnectionPropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues(
                    "adapters.r2dbc.host=localhost",
                    "adapters.r2dbc.port=5432",
                    "adapters.r2dbc.database=testdb",
                    "adapters.r2dbc.schema=public",
                    "adapters.r2dbc.username=testuser",
                    "adapters.r2dbc.password=testpass"
            );

    @Test
    @DisplayName("Should bind PostgreSQL connection properties correctly from configuration")
    void testPropertiesBinding() {
        contextRunner.run(context -> {
            PostgresqlConnectionProperties props = context.getBean(PostgresqlConnectionProperties.class);
            assertThat(props.host()).isEqualTo("localhost");
            assertThat(props.port()).isEqualTo(5432);
            assertThat(props.database()).isEqualTo("testdb");
            assertThat(props.schema()).isEqualTo("public");
            assertThat(props.username()).isEqualTo("testuser");
            assertThat(props.password()).isEqualTo("testpass");
        });
    }

    @EnableConfigurationProperties(PostgresqlConnectionProperties.class)
    static class TestConfig {
    }
}
