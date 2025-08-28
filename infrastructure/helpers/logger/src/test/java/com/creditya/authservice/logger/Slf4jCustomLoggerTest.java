package com.creditya.authservice.logger;

import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Slf4jCustomLoggerTest {

    private Slf4jUseCaseLogger customLogger;
    private TestLogger testLogger;

    @BeforeEach
    void setUp() {
        customLogger = new Slf4jUseCaseLogger();
        testLogger = TestLoggerFactory.getTestLogger(Slf4jUseCaseLogger.class);
        TestLoggerFactory.clear();
    }

    @Test
    @DisplayName("Should log a TRACE message correctly")
    void testTrace() {
        customLogger.trace("trace message {}", 123);
        assertThat(testLogger.getLoggingEvents())
                .anyMatch(event -> event.getLevel().toString().equals("TRACE")
                        && event.getMessage().equals("trace message {}")
                        && event.getArguments().contains(123));
    }

    @Test
    @DisplayName("Should log an INFO message correctly")
    void testInfo() {
        customLogger.info("info message {}", "arg");
        assertThat(testLogger.getLoggingEvents())
                .anyMatch(event -> event.getLevel().toString().equals("INFO")
                        && event.getMessage().equals("info message {}")
                        && event.getArguments().contains("arg"));
    }

    @Test
    @DisplayName("Should log a WARN message correctly")
    void testWarn() {
        customLogger.warn("warn message {}", true);
        assertThat(testLogger.getLoggingEvents())
                .anyMatch(event -> event.getLevel().toString().equals("WARN")
                        && event.getMessage().equals("warn message {}")
                        && event.getArguments().contains(true));
    }

    @Test
    @DisplayName("Should log an ERROR message correctly")
    void testError() {
        customLogger.error("error message {}", "error");
        assertThat(testLogger.getLoggingEvents())
                .anyMatch(event -> event.getLevel().toString().equals("ERROR")
                        && event.getMessage().equals("error message {}")
                        && event.getArguments().contains("error"));
    }
}