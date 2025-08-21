package ai.fusionbrain.dto;

/**
 * Enumeration representing the possible statuses of a resource.
 */
public enum EResourceStatus {
    /**
     * Initial status indicating that the resource has just been created and no processing has started yet.
     */
    INITIAL,
    /**
     * Status indicating that the resource is currently being processed.
     */
    PROCESSING,
    /**
     * Final status indicating that the resource processing has completed successfully.
     */
    DONE,
    /**
     * Final status indicating that the resource processing has failed.
     */
    FAIL,
    /**
     * Final status indicating that the user or system has canceled the resource processing.
     */
    CANCEL;

    /**
     * Checks if the current status is a final status (i.e., not in progress).
     *
     * @return true if the status is DONE, FAIL, or CANCEL; false otherwise.
     */
    public boolean isFinal() {
        return this.equals(DONE) || this.equals(FAIL) || this.equals(CANCEL);
    }
}
