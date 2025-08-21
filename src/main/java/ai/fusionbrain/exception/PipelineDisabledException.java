package ai.fusionbrain.exception;

/**
 * Exception thrown when a pipeline is disabled.
 */
public class PipelineDisabledException extends FusionBrainException {
    /**
     * Constructs a new exception with the specified message.
     *
     * @param message The detail message.
     */
    public PipelineDisabledException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause of this exception.
     */
    public PipelineDisabledException(String message, Throwable cause) {
        super(message, cause);
    }
}