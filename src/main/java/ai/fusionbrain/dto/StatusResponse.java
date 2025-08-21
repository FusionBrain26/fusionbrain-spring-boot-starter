package ai.fusionbrain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO class representing the response status of a request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusResponse {
    /**
     * Unique identifier associated with the request.
     */
    @JsonProperty("uuid")
    private UUID id;

    /**
     * Current status of the request.
     */
    private EResourceStatus status;

    /**
     * Description providing more details about the current status.
     */
    private String statusDescription;

    /**
     * Result data associated with the request, represented as a JSON node.
     */
    private JsonNode result;

    /**
     * Time taken (in seconds) to generate the resource.
     */
    private Long generationTime;
}
