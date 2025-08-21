package ai.fusionbrain.client;

import ai.fusionbrain.dto.*;
import ai.fusionbrain.dto.request.PipelineParams;
import ai.fusionbrain.exception.FusionBrainException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Interface defining methods for interacting with FusionBrain API.
 */
public interface FusionBrainClient {
    /**
     * Retrieves a list of all available pipelines.
     *
     * @return List of {@link PipelineDTO} objects representing the pipelines.
     * @throws FusionBrainException if an error occurs during the request.
     */
    List<PipelineDTO> getPipelines() throws FusionBrainException;

    /**
     * Retrieves a list of pipelines based on the specified type.
     *
     * @param type The type of pipelines to retrieve.
     * @return List of {@link PipelineDTO} objects representing the pipelines of the specified type.
     * @throws FusionBrainException if an error occurs during the request.
     */
    List<PipelineDTO> getPipelines(EPipelineType type) throws FusionBrainException;

    /**
     * Retrieves the availability status of a specific pipeline.
     *
     * @param pipelineId The unique identifier of the pipeline.
     * @return {@link AvailabilityStatus} indicating whether the pipeline is available.
     * @throws FusionBrainException if an error occurs during the request.
     */
    AvailabilityStatus getPipelineAvailability(UUID pipelineId) throws FusionBrainException;

    /**
     * Runs a specified pipeline with given parameters and files.
     *
     * @param pipelineId The unique identifier of the pipeline to run.
     * @param params     The parameters for the pipeline execution.
     * @param files      List of byte arrays representing the input files for the pipeline.
     * @return {@link RunResponse} containing information about the initiated pipeline run.
     * @throws FusionBrainException if an error occurs during the request.
     */
    RunResponse runPipeline(UUID pipelineId, PipelineParams params, List<byte[]> files) throws FusionBrainException;

    /**
     * Runs a specified pipeline with given parameters (without files).
     *
     * @param pipelineId The unique identifier of the pipeline to run.
     * @param params     The parameters for the pipeline execution.
     * @return {@link RunResponse} containing information about the initiated pipeline run.
     * @throws FusionBrainException if an error occurs during the request.
     */
    default RunResponse runPipeline(UUID pipelineId, PipelineParams params) throws FusionBrainException {
        return runPipeline(pipelineId, params, null);
    }

    /**
     * Retrieves the current status of a running task.
     *
     * @param taskId The unique identifier of the task.
     * @return {@link StatusResponse} object containing the status information.
     * @throws FusionBrainException if an error occurs during the request.
     */
    StatusResponse getStatus(UUID taskId) throws FusionBrainException;

    /**
     * Asynchronously waits for a specified task to complete, polling at regular intervals.
     *
     * @param taskId       The unique identifier of the task.
     * @param initialDelay The time in seconds to wait before starting to poll.
     * @return {@link StatusResponse} object containing the final status after completion.
     * @throws FusionBrainException if an error occurs during the request.
     */
    CompletableFuture<StatusResponse> waitForCompletion(UUID taskId, long initialDelay) throws FusionBrainException;

    /**
     * Synchronously waits for a specified task to complete, polling at regular intervals.
     * This method blocks until the task is completed or an error occurs.
     *
     * @param taskId       The unique identifier of the task.
     * @param initialDelay The time in seconds to wait before starting to poll.
     * @return {@link StatusResponse} object containing the final status after completion.
     * @throws FusionBrainException if an error occurs during the request.
     */
    default StatusResponse waitForCompletionSync(UUID taskId, long initialDelay) {
        try {
            return waitForCompletion(taskId, initialDelay).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new FusionBrainException("Failed to execute synchronous operation", e);
        }
    }
}