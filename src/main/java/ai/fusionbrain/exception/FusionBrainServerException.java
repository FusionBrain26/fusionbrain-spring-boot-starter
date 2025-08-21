package ai.fusionbrain.exception;

/**
 * This class represents exceptions that occur on the server side within the FusionBrain application.
 * It extends the base FusionBrainException class to provide specific handling for server-related errors.
 */
public class FusionBrainServerException extends FusionBrainException {
    /**
     * Constructs a new FusionBrainServerException with the specified message.
     *
     * @param message The detail message explaining the exception.
     */
    public FusionBrainServerException(String message) {
        super(message);
    }

    /**
     * Constructs a new FusionBrainServerException with the specified message and cause.
     *
     * @param message The detail message explaining the exception.
     * @param cause   The underlying cause of this exception.
     */
    public FusionBrainServerException(String message, Throwable cause) {
        super(message, cause);
    }
}