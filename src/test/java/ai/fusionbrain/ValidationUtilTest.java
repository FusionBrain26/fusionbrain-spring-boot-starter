package ai.fusionbrain;

import ai.fusionbrain.exception.ValidationException;
import ai.fusionbrain.utils.ValidationUtil;
import jakarta.validation.Validation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class ValidationUtilTest {

    // Simple test class with validation constraints
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class TestObject {
        @NotNull(message = "Field cannot be null")
        private String requiredField;

        @Size(min = 3, max = 5, message = "Length must be between 3 and 5")
        private String sizedField;
    }

    @Test
    void validate_ShouldNotThrow_WhenObjectIsValid() {
        // Arrange
        TestObject validObject = new TestObject("valid", "123");

        // Act & Assert
        assertDoesNotThrow(() -> ValidationUtil.validate(validObject));
    }

    @Test
    void validate_ShouldThrowValidationException_WhenObjectIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validate(null));
    }

    @Test
    void validate_ShouldThrowValidationException_WithSingleViolation() {
        // Arrange
        TestObject invalidObject = new TestObject(null, "123");

        // Act
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ValidationUtil.validate(invalidObject));

        // Assert
        assertEquals("Validation failed: requiredField: Field cannot be null", exception.getMessage());
    }

    @Test
    void validate_ShouldThrowValidationException_WithMultipleViolations() {
        // Arrange
        TestObject invalidObject = new TestObject(null, "123456");

        // Act
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ValidationUtil.validate(invalidObject));

        // Assert
        String message = exception.getMessage();
        assertTrue(message.contains("requiredField: Field cannot be null"));
        assertTrue(message.contains("sizedField: Length must be between 3 and 5"));
        assertTrue(message.contains("; ")); // Check proper separation
    }

    @Test
    void validate_ShouldHandleValidatorFactoryException_Gracefully() {
        // Arrange
        TestObject validObject = new TestObject("valid", "123");

        try (MockedStatic<Validation> validationMock = mockStatic(Validation.class)) {
            validationMock.when(Validation::buildDefaultValidatorFactory)
                    .thenThrow(new RuntimeException("Factory creation failed"));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> ValidationUtil.validate(validObject));
        }
    }
}