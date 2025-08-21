package ai.fusionbrain.dto;

public enum EPipelineStatus {
    ACTIVE,
    DISABLED_MANUALLY,
    DISABLED_BY_QUEUE;

    /**
     * Checks if the pipeline status is disabled.
     *
     * @return true if the status is either DISABLED_MANUALLY or DISABLED_BY_QUEUE, false otherwise.
     */
    public boolean isDisabled() {
        return this.equals(DISABLED_MANUALLY) || this.equals(DISABLED_BY_QUEUE);
    }
}
