package ai.fusionbrain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class representing the availability status of a pipeline.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityStatus {
    /**
     * The current status of the pipeline.
     */
    private EPipelineStatus status;
}
