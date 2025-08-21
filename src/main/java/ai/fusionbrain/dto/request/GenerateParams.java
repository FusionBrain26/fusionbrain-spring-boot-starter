package ai.fusionbrain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generation parameters containing the text query.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateParams {

    /**
     * The text query for image generation.
     * Must be between 1 and 1000 characters long.
     */
    @NotBlank(message = "The query field is required.")
    @Size(min = 1, max = 1000, message = "The query field must be between 1 and 1000 characters long.")
    private String query;
}