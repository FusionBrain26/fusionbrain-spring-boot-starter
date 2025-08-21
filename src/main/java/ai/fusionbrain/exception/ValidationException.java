package ai.fusionbrain.exception;

/**
 * Exception thrown when parameter validation fails.
 * Subtype of FusionBrainException to maintain consistent error handling.
 */
public class ValidationException extends FusionBrainException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}