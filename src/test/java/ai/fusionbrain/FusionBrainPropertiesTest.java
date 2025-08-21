package ai.fusionbrain;

import ai.fusionbrain.config.FusionBrainProperties;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FusionBrainPropertiesTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldHaveCorrectDefaultValues() {
        FusionBrainProperties properties = new FusionBrainProperties();

        assertTrue(properties.isEnabled(), "Default enabled should be true");
        assertEquals("https://api-key.fusionbrain.ai", properties.getBaseUrl(), "Default baseUrl should match");
        assertEquals(5, properties.getMaxRetries(), "Default maxRetries should be 5");
        assertEquals(3, properties.getPollInterval(), "Default pollInterval should be 3");
    }

    @Test
    void shouldValidateNotEmptyFields() {
        FusionBrainProperties properties = new FusionBrainProperties();
        properties.setBaseUrl("");
        properties.setApiKey("");
        properties.setApiSecret("");

        var violations = validator.validate(properties);

        assertEquals(3, violations.size(), "Should have 3 validation errors");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("baseUrl must not be empty")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("API key must not be empty")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("API secret must not be empty")));
    }

    @Test
    void shouldValidateMaxRetriesConstraint() {
        FusionBrainProperties properties = new FusionBrainProperties();
        // Set required fields to avoid other validation errors
        properties.setApiKey("dummy-key");
        properties.setApiSecret("dummy-secret");
        properties.setMaxRetries(-1);

        var violations = validator.validate(properties);

        assertEquals(1, violations.size(), "Should have 1 validation error");
        assertEquals("maxRetries must be at least 0.", violations.iterator().next().getMessage());
    }

    @Test
    void shouldValidatePollIntervalConstraint() {
        FusionBrainProperties properties = new FusionBrainProperties();
        // Set required fields to avoid other validation errors
        properties.setApiKey("dummy-key");
        properties.setApiSecret("dummy-secret");
        properties.setPollInterval(0);

        var violations = validator.validate(properties);

        assertEquals(1, violations.size(), "Should have 1 validation error");
        assertEquals("pollInterval must be positive", violations.iterator().next().getMessage());
    }

    @Test
    void shouldValidateAsyncCorePoolSizeConstraint() {
        FusionBrainProperties properties = new FusionBrainProperties();
        // Set required fields to avoid other validation errors
        properties.setApiKey("dummy-key");
        properties.setApiSecret("dummy-secret");
        properties.setAsyncCorePoolSize(0);

        var violations = validator.validate(properties);

        assertEquals(1, violations.size(), "Should have 1 validation error");
        assertEquals("asyncCorePoolSize must be positive", violations.iterator().next().getMessage());
    }

    @Test
    void shouldBindPropertiesCorrectly() {
        Map<String, String> properties = Map.of(
                "fusionbrain.enabled", "false",
                "fusionbrain.base-url", "https://custom.api.url",
                "fusionbrain.api-key", "test-key",
                "fusionbrain.api-secret", "test-secret",
                "fusionbrain.max-retries", "10",
                "fusionbrain.poll-interval", "5"
        );

        ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(source);
        BindResult<FusionBrainProperties> bindResult = binder.bind("fusionbrain", FusionBrainProperties.class);

        assertTrue(bindResult.isBound(), "Properties should be bound successfully");

        FusionBrainProperties boundProperties = bindResult.get();
        assertFalse(boundProperties.isEnabled(), "Enabled should be bound correctly");
        assertEquals("https://custom.api.url", boundProperties.getBaseUrl(), "Base URL should be bound correctly");
        assertEquals("test-key", boundProperties.getApiKey(), "API key should be bound correctly");
        assertEquals("test-secret", boundProperties.getApiSecret(), "API secret should be bound correctly");
        assertEquals(10, boundProperties.getMaxRetries(), "Max retries should be bound correctly");
        assertEquals(5, boundProperties.getPollInterval(), "Poll interval should be bound correctly");
    }

    @Test
    void shouldValidateWhenBindingInvalidProperties() {
        Map<String, String> properties = Map.of(
                "fusionbrain.base-url", "",
                "fusionbrain.api-key", "",
                "fusionbrain.api-secret", "",
                "fusionbrain.max-retries", "-5",
                "fusionbrain.poll-interval", "0"
        );

        ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(source);
        BindResult<FusionBrainProperties> bindResult = binder.bind("fusionbrain", FusionBrainProperties.class);

        assertTrue(bindResult.isBound(), "Properties should be bound even with invalid values");

        FusionBrainProperties boundProperties = bindResult.get();
        var violations = validator.validate(boundProperties);

        assertEquals(5, violations.size(), "Should have 5 validation errors");
    }

    @Test
    void shouldHandleMissingOptionalProperties() {
        // Create properties with only the required fields
        Map<String, String> properties = Map.of(
                "fusionbrain.api-key", "dummy-key",
                "fusionbrain.api-secret", "dummy-secret"
        );

        ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(source);
        BindResult<FusionBrainProperties> bindResult = binder.bind("fusionbrain", FusionBrainProperties.class);

        assertTrue(bindResult.isBound(), "Properties should be bound when required fields are present");

        FusionBrainProperties boundProperties = bindResult.get();

        // Verify defaults are used for optional fields
        assertTrue(boundProperties.isEnabled(), "Default enabled should be true");
        assertEquals("https://api-key.fusionbrain.ai", boundProperties.getBaseUrl(), "Default baseUrl should be used");
        assertEquals(5, boundProperties.getMaxRetries(), "Default maxRetries should be used");
        assertEquals(3, boundProperties.getPollInterval(), "Default pollInterval should be used");

        // Verify required fields
        assertEquals("dummy-key", boundProperties.getApiKey(), "API key should be set");
        assertEquals("dummy-secret", boundProperties.getApiSecret(), "API secret should be set");
    }
}