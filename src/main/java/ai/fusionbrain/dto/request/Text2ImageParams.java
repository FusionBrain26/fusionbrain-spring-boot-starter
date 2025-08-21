package ai.fusionbrain.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Parameters for text-to-image generation request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Text2ImageParams extends PipelineParams {

    /**
     * The type of text-to-image operation.
     */
    @NotNull(message = "The 'params.type' field is required.")
    private EText2ImageType type = EText2ImageType.GENERATE;

    /**
     * Width of the generated image.
     * Must be between 128 and 2048 pixels.
     */
    @NotNull(message = "The width field is required.")
    @Min(value = 128, message = "The width field must be at least 128.")
    @Max(value = 2048, message = "The width field must be at most 2048.")
    private Integer width = 1024;

    /**
     * Height of the generated image.
     * Must be between 128 and 2048 pixels.
     */
    @NotNull(message = "The height field is required.")
    @Min(value = 128, message = "The height field must be at least 128.")
    @Max(value = 2048, message = "The height field must be at most 2048.")
    private Integer height = 1024;

    /**
     * Number of images to generate.
     */
    @NotNull(message = "The numImages field is required.")
    @Min(value = 1, message = "The numImages field must be exactly 1.")
    @Max(value = 1, message = "The numImages field must be exactly 1.")
    private Integer numImages = 1;

    /**
     * Parameters for image generation.
     * Must contain a non-empty query string.
     */
    @NotNull(message = "The generateParams object is required.")
    @Valid
    private GenerateParams generateParams;

    /**
     * Optional negative prompt for the decoder.
     * If provided, must not exceed 1000 characters.
     */
    @Size(max = 1000, message = "The negativePromptDecoder field must be at most 1000 characters long.")
    private String negativePromptDecoder;

    private String style;
}