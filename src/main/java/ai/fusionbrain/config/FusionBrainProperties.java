package ai.fusionbrain.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for FusionBrain API integration.
 */
@Data
@ConfigurationProperties(prefix = "fusionbrain")
@Validated
public class FusionBrainProperties {
    /**
     * Whether FusionBrain API integration is enabled.
     * <p>Default: true</p>
     */
    private boolean enabled = true;

    /**
     * Base URL for the FusionBrain API.
     * <p>Defaults to {@code https://api-key.fusionbrain.ai}</p>
     */
    @NotEmpty(message = "baseUrl must not be empty")
    private String baseUrl = "https://api-key.fusionbrain.ai";

    /**
     * API key required for authenticating requests.
     *
     * @see #apiSecret
     */
    @NotEmpty(message = "API key must not be empty")
    private String apiKey;

    /**
     * API secret required for authenticating requests.
     *
     * @see #apiKey
     */
    @NotEmpty(message = "API secret must not be empty")
    private String apiSecret;

    /**
     * Maximum number of retries for failed requests.
     * <p>Default: 5</p>
     */
    @Min(value = 0, message = "maxRetries must be at least 0.")
    private int maxRetries = 5;

    /**
     * Interval between polling attempts for asynchronous operations.
     * <p>Units: seconds</p>
     * <p>Default: 3</p>
     */
    @Positive(message = "pollInterval must be positive")
    private long pollInterval = 3;

    /**
     * Maximum number of threads for asynchronous operations.
     * <p>Units: threads</p>
     * <p>Default: 1</p>
     */
    @Positive(message = "asyncCorePoolSize must be positive")
    private int asyncCorePoolSize = 1;
}