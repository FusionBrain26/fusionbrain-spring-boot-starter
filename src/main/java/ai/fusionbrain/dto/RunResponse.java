package ai.fusionbrain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO class representing a response for a run operation.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RunResponse {
    /**
     * Unique identifier for the run request.
     */
    @JsonProperty("uuid")
    private UUID id;

    /**
     * The current status of the resource.
     */
    private EResourceStatus status;

    /**
     * The pipeline status. This field is present only when the pipeline is unavailable.
     */
    @JsonProperty("model_status")
    private EPipelineStatus modelStatus;

    /**
     * This value represents the number of seconds after which the status should be called.
     */
    @JsonProperty("status_time")
    private long statusTime;
}
