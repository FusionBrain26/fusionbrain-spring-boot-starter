package ai.fusionbrain.exception;

import lombok.Getter;

/**
 * A generic exception class for FusionBrain.
 */
@Getter
public class FusionBrainException extends RuntimeException {

    /**
     * Constructs a new FusionBrainException with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the getMessage() method).
     */
    public FusionBrainException(String message) {
        super(message);
    }

    /**
     * Constructs a new FusionBrainException with the specified detail message and cause.
     *
     * @param message The detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause   The cause (which is saved for later retrieval by the getCause() method).
     *                (A null value is permitted, and indicates that the cause is unknown.)
     */
    public FusionBrainException(String message, Throwable cause) {
        super(message, cause);
    }
}
