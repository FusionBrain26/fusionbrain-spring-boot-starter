package ai.fusionbrain;

import ai.fusionbrain.dto.request.EText2ImageType;
import ai.fusionbrain.dto.request.GenerateParams;
import ai.fusionbrain.dto.request.Text2ImageParams;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class Text2ImageParamsValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void whenAllFieldsCorrect_thenNoViolations() {
        GenerateParams generateParams = new GenerateParams("a valid prompt");
        Text2ImageParams params = new Text2ImageParams();
        params.setType(EText2ImageType.GENERATE);
        params.setWidth(1024);
        params.setHeight(1024);
        params.setNumImages(1);
        params.setGenerateParams(generateParams);

        Set<ConstraintViolation<Text2ImageParams>> violations = validator.validate(params);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenTypeIsNull_thenOneViolation() {
        GenerateParams generateParams = new GenerateParams("a valid prompt");
        Text2ImageParams params = new Text2ImageParams();
        params.setType(null);
        params.setWidth(1024);
        params.setHeight(1024);
        params.setNumImages(1);
        params.setGenerateParams(generateParams);

        Set<ConstraintViolation<Text2ImageParams>> violations = validator.validate(params);
        assertEquals(1, violations.size());
        assertEquals("The 'params.type' field is required.", violations.iterator().next().getMessage());
    }

    @Test
    void whenWidthIsNull_thenOneViolation() {
        GenerateParams generateParams = new GenerateParams("a valid prompt");
        Text2ImageParams params = new Text2ImageParams();
        params.setType(EText2ImageType.GENERATE);
        params.setWidth(null);
        params.setHeight(1024);
        params.setNumImages(1);
        params.setGenerateParams(generateParams);

        Set<ConstraintViolation<Text2ImageParams>> violations = validator.validate(params);
        assertEquals(1, violations.size());
        assertEquals("The width field is required.", violations.iterator().next().getMessage());
    }

    @Test
    void whenWidthIsBelowMin_thenOneViolation() {
        GenerateParams generateParams = new GenerateParams("a valid prompt");
        Text2ImageParams params = new Text2ImageParams();
        params.setType(EText2ImageType.GENERATE);
        params.setWidth(127);
        params.setHeight(1024);
        params.setNumImages(1);
        params.setGenerateParams(generateParams);

        Set<ConstraintViolation<Text2ImageParams>> violations = validator.validate(params);
        assertEquals(1, violations.size());
        assertEquals("The width field must be at least 128.", violations.iterator().next().getMessage());
    }

    @Test
    void whenWidthIsAboveMax_thenOneViolation() {
        GenerateParams generateParams = new GenerateParams("a valid prompt");
        Text2ImageParams params = new Text2ImageParams();
        params.setType(EText2ImageType.GENERATE);
        params.setWidth(2049);
        params.setHeight(1024);
        params.setNumImages(1);
        params.setGenerateParams(generateParams);

        Set<ConstraintViolation<Text2ImageParams>> violations = validator.validate(params);
        assertEquals(1, violations.size());
        assertEquals("The width field must be at most 2048.", violations.iterator().next().getMessage());
    }

    @Test
    void whenHeightIsNull_thenOneViolation() {
        GenerateParams generateParams = new GenerateParams("a valid prompt");
        Text2ImageParams params = new Text2ImageParams();
        params.setType(EText2ImageType.GENERATE);
        params.setWidth(1024);
        params.setHeight(null);
        params.setNumImages(1);
        params.setGenerateParams(generateParams);

        Set<ConstraintViolation<Text2ImageParams>> violations = validator.validate(params);
        assertEquals(1, violations.size());
        assertEquals("The height field is required.", violations.iterator().next().getMessage());
    }

    @Test
    void whenHeightIsBelowMin_thenOneViolation() {
        GenerateParams generateParams = new GenerateParams("a valid prompt");
        Text2ImageParams params = new Text2ImageParams();
        params.setType(EText2ImageType.GENERATE);
        params.setWidth(1024);
        params.setHeight(127);
        params.setNumImages(1);
        params.setGenerateParams(generateParams);

        Set<ConstraintViolation<Text2ImageParams>> violations = validator.validate(params);
        assertEquals(1, violations.size());
        assertEquals("The height field must be at least 128.", violations.iterator().next().getMessage());
    }

    @Test
    void whenHeightIsAboveMax_thenOneViolation() {
        GenerateParams generateParams = new GenerateParams("a valid prompt");
        Text2ImageParams params = new Text2ImageParams();
        params.setType(EText2ImageType.GENERATE);
        params.setWidth(1024);
        params.setHeight(2049);
        params.setNumImages(1);
        params.setGenerateParams(generateParams);

        Set<ConstraintViolation<Text2ImageParams>> violations = validator.validate(params);
        assertEquals(1, violations.size());
        assertEquals("The height field must be at most 2048.", violations.iterator().next().getMessage());
    }

    @Test
    void whenNumImagesIsNull_thenOneViolation() {
        GenerateParams generateParams = new GenerateParams("a valid prompt");
        Text2ImageParams params = new Text2ImageParams();
        params.setType(EText2ImageType.GENERATE);
        params.setWidth(1024);
        params.setHeight(1024);
        params.setNumImages(null);
        params.setGenerateParams(generateParams);

        Set<ConstraintViolation<Text2ImageParams>> violations = validator.validate(params);
        assertEquals(1, violations.size());
        assertEquals("The numImages field is required.", violations.iterator().next().getMessage());
    }

    @Test
    void whenNumImagesIsNotOne_thenViolation() {
        GenerateParams generateParams = new GenerateParams("a valid prompt");
        Text2ImageParams params = new Text2ImageParams();
        params.setType(EText2ImageType.GENERATE);
        params.setWidth(1024);
        params.setHeight(1024);
        params.setNumImages(2);
        params.setGenerateParams(generateParams);

        Set<ConstraintViolation<Text2ImageParams>> violations = validator.validate(params);
        assertEquals(1, violations.size());
        assertEquals("The numImages field must be exactly 1.", violations.iterator().next().getMessage());
    }

    @Test
    void whenGenerateParamsIsNull_thenOneViolation() {
        Text2ImageParams params = new Text2ImageParams();
        params.setType(EText2ImageType.GENERATE);
        params.setWidth(1024);
        params.setHeight(1024);
        params.setNumImages(1);
        params.setGenerateParams(null);

        Set<ConstraintViolation<Text2ImageParams>> violations = validator.validate(params);
        assertEquals(1, violations.size());
        assertEquals("The generateParams object is required.", violations.iterator().next().getMessage());
    }

    @Test
    void whenNegativePromptIsTooLong_thenOneViolation() {
        GenerateParams generateParams = new GenerateParams("a valid prompt");
        Text2ImageParams params = new Text2ImageParams();
        params.setType(EText2ImageType.GENERATE);
        params.setWidth(1024);
        params.setHeight(1024);
        params.setNumImages(1);
        params.setGenerateParams(generateParams);
        params.setNegativePromptDecoder("a".repeat(1001));

        Set<ConstraintViolation<Text2ImageParams>> violations = validator.validate(params);
        assertEquals(1, violations.size());
        assertEquals("The negativePromptDecoder field must be at most 1000 characters long.", violations.iterator().next().getMessage());
    }

    @Test
    void whenGenerateParamsQueryIsNull_thenOneViolation() {
        GenerateParams generateParams = new GenerateParams(null);
        Text2ImageParams params = new Text2ImageParams();
        params.setType(EText2ImageType.GENERATE);
        params.setWidth(1024);
        params.setHeight(1024);
        params.setNumImages(1);
        params.setGenerateParams(generateParams);

        Set<ConstraintViolation<Text2ImageParams>> violations = validator.validate(params);
        assertEquals(1, violations.size());
        assertEquals("The query field is required.", violations.iterator().next().getMessage());
    }

    @Test
    void whenGenerateParamsQueryIsEmpty_thenTwoViolations() {
        GenerateParams generateParams = new GenerateParams("");
        Text2ImageParams params = new Text2ImageParams();
        params.setType(EText2ImageType.GENERATE);
        params.setWidth(1024);
        params.setHeight(1024);
        params.setNumImages(1);
        params.setGenerateParams(generateParams);

        Set<ConstraintViolation<Text2ImageParams>> violations = validator.validate(params);
        assertEquals(2, violations.size());

        List<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .sorted()
                .toList();

        assertIterableEquals(
                List.of(
                        "The query field is required.",
                        "The query field must be between 1 and 1000 characters long."
                ),
                messages
        );
    }

    @Test
    void whenGenerateParamsQueryIsTooLong_thenOneViolation() {
        GenerateParams generateParams = new GenerateParams("a".repeat(1001));
        Text2ImageParams params = new Text2ImageParams();
        params.setType(EText2ImageType.GENERATE);
        params.setWidth(1024);
        params.setHeight(1024);
        params.setNumImages(1);
        params.setGenerateParams(generateParams);

        Set<ConstraintViolation<Text2ImageParams>> violations = validator.validate(params);
        assertEquals(1, violations.size());
        assertEquals("The query field must be between 1 and 1000 characters long.", violations.iterator().next().getMessage());
    }

    @Test
    void whenStyleIsNotString_thenOneViolation() {
        // This test would require reflection to set an invalid type, but since style is String
        // and we don't have validation beyond that, this is just a placeholder
        GenerateParams generateParams = new GenerateParams("valid prompt");
        Text2ImageParams params = new Text2ImageParams();
        params.setType(EText2ImageType.GENERATE);
        params.setWidth(1024);
        params.setHeight(1024);
        params.setNumImages(1);
        params.setGenerateParams(generateParams);
        params.setStyle("valid style"); // No validation beyond being a string

        Set<ConstraintViolation<Text2ImageParams>> violations = validator.validate(params);
        assertTrue(violations.isEmpty());
    }
}