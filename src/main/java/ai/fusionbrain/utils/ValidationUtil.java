package ai.fusionbrain.utils;

import ai.fusionbrain.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.Set;
import java.util.stream.Collectors;

public class ValidationUtil {
    /**
     * Validates the given object using the Jakarta Bean Validation API.
     * If validation fails, a ValidationException is thrown with details of all violations.
     *
     * @param object The object to validate
     * @throws ValidationException If validation fails
     */
    public static <T> void validate(T object) {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

            // Validate the object and collect constraint violations
            Set<ConstraintViolation<T>> violations = validator.validate(object);

            // Check if there are any validation errors
            if (!violations.isEmpty()) {
                // Generate a user-friendly error message from the violations
                String errorMessage = violations.stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.joining("; "));
                throw new ValidationException("Validation failed: " + errorMessage);
            }
        }
    }
}