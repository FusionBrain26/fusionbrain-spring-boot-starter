package ai.fusionbrain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * DTO representing a pipeline.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PipelineDTO {
    /**
     * Unique identifier of the pipeline.
     */
    private UUID id;

    /**
     * Name of the pipeline in the default language.
     */
    private String name;

    /**
     * Name of the pipeline in English.
     */
    private String nameEn;

    /**
     * Description of the pipeline in the default language.
     */
    private String description;

    /**
     * Description of the pipeline in English.
     */
    private String descriptionEn;

    /**
     * Tags associated with this pipeline.
     */
    private Set<TagDTO> tags;

    /**
     * Version number of the pipeline.
     */
    private double version;

    /**
     * Current status of the pipeline.
     */
    private EPipelineStatus status;

    /**
     * Type of the pipeline.
     */
    private EPipelineType type;

    /**
     * Date when this pipeline was created.
     */
    private Instant createdDate;

    /**
     * Last date when this pipeline was modified.
     */
    private Instant lastModified;
}
