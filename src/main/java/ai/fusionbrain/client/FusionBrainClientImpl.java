package ai.fusionbrain.client;

import ai.fusionbrain.config.FusionBrainProperties;
import ai.fusionbrain.dto.*;
import ai.fusionbrain.dto.request.PipelineParams;
import ai.fusionbrain.exception.FusionBrainException;
import ai.fusionbrain.exception.FusionBrainServerException;
import ai.fusionbrain.exception.PipelineDisabledException;
import ai.fusionbrain.exception.ValidationException;
import ai.fusionbrain.utils.ValidationUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Component
@RequiredArgsConstructor
public class FusionBrainClientImpl implements FusionBrainClient {
    private final FusionBrainFeignClient feignClient;
    private final ObjectMapper objectMapper;
    private final FusionBrainProperties fusionBrainProperties;
    private final Executor asyncExecutor;

    @Override
    public List<PipelineDTO> getPipelines() throws FusionBrainException {
        log.debug("Fetching all pipelines");
        try {
            List<PipelineDTO> pipelines = feignClient.getPipelines(null);
            log.debug("Successfully fetched {} pipelines", pipelines.size());
            log.trace("Pipeline details: {}", pipelines);
            return pipelines;
        } catch (FusionBrainServerException e) {
            log.error("Feign client error while getting pipelines", e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to get pipelines", e);
            throw new FusionBrainException("Failed to get pipelines", e);
        }
    }

    @Override
    public List<PipelineDTO> getPipelines(EPipelineType type) throws FusionBrainException {
        log.debug("Fetching pipelines of type: {}", type);
        try {
            List<PipelineDTO> pipelines = feignClient.getPipelines(type);
            log.debug("Found {} pipelines of type {}", pipelines.size(), type);
            log.trace("Pipeline details for type {}: {}", type, pipelines);
            return pipelines;
        } catch (FusionBrainServerException e) {
            log.error("Feign client error while getting pipelines", e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to get pipelines", e);
            throw new FusionBrainException("Failed to get pipelines", e);
        }
    }

    @Override
    public AvailabilityStatus getPipelineAvailability(UUID pipelineId) throws FusionBrainException {
        log.debug("Checking availability for pipeline: {}", pipelineId);
        try {
            AvailabilityStatus status = feignClient.getPipelineAvailability(pipelineId);
            log.debug("Pipeline {} availability status: {}", pipelineId, status);
            return status;
        } catch (FusionBrainServerException e) {
            log.error("Feign client error while checking pipeline {} availability: {}", pipelineId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to check availability for pipeline {}: {}", pipelineId, e.getMessage());
            throw new FusionBrainException("Failed to get pipeline availability", e);
        }
    }

    @Override
    public RunResponse runPipeline(UUID pipelineId, PipelineParams params, List<byte[]> files) throws FusionBrainException {
        log.debug("Starting pipeline execution for pipeline: {}", pipelineId);
        log.trace("Pipeline parameters: {}", params);
        log.trace("Files count: {}", files != null ? files.size() : 0);

        try {
            if (Objects.nonNull(params)) {
                ValidationUtil.validate(params);
            }

            log.debug("Pipeline parameters validation successful");

            if (files != null) {
                files.forEach(file -> {
                    if (file == null || file.length == 0) {
                        log.error("Validation failed: File content is null or empty");
                        throw new ValidationException("File content cannot be null or empty");
                    }
                });
                log.debug("All {} files passed validation", files.size());
            }

            var paramsNode = objectMapper.valueToTree(params);
            log.trace("Converted params to JSON node: {}", paramsNode);

            var response = feignClient.runPipeline(paramsNode, pipelineId, files);
            log.debug("Pipeline execution started successfully for pipeline: {}", pipelineId);
            log.trace("Initial response: {}", response);

            if (Objects.nonNull(response.getModelStatus()) && response.getModelStatus().isDisabled()) {
                log.debug("Attempted to use disabled model in pipeline: {}", pipelineId);
                throw new PipelineDisabledException("Pipeline is currently disabled and cannot process requests");
            }

            return response;
        } catch (ValidationException e) {
            log.error("Validation failed for pipeline parameters: {}", e.getMessage());
            throw e;
        } catch (PipelineDisabledException e) {
            log.error("Pipeline disabled error: {}", e.getMessage());
            throw e;
        } catch (FusionBrainServerException e) {
            log.error("Feign client error while executing pipeline {}: {}", pipelineId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to run pipeline {}: {}", pipelineId, e.getMessage());
            throw new FusionBrainException("Failed to run pipeline", e);
        }
    }

    @Override
    public StatusResponse getStatus(UUID taskId) throws FusionBrainException {
        log.debug("Fetching status for task: {}", taskId);
        try {
            StatusResponse status = feignClient.getStatus(taskId);
            log.debug("Task {} status: {}", taskId, status.getStatus());
            log.trace("Full status response: {}", status);
            return status;
        } catch (FusionBrainServerException e) {
            log.error("Feign client error while getting status for task {}: {}", taskId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to get status for task {}: {}", taskId, e.getMessage());
            throw new FusionBrainException("Failed to get task status", e);
        }
    }

    @Override
    public CompletableFuture<StatusResponse> waitForCompletion(UUID taskId, long initialDelay) {
        CompletableFuture<StatusResponse> future = new CompletableFuture<>();

        asyncExecutor.execute(() -> {
            try {
                StatusResponse result = waitForCompletionInternal(taskId, initialDelay);
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    private StatusResponse waitForCompletionInternal(UUID taskId, long initialDelay) throws FusionBrainException {
        log.debug("Starting wait for task completion: {}", taskId);
        log.debug("Initial delay: {} seconds, max retries: {}, poll interval: {} seconds",
                initialDelay, fusionBrainProperties.getMaxRetries(), fusionBrainProperties.getPollInterval());

        if (initialDelay < 0) {
            log.error("Invalid initial delay: {} seconds", initialDelay);
            throw new IllegalArgumentException("Initial delay cannot be negative");
        }

        if (initialDelay > 0) {
            log.debug("Waiting initial delay of {} seconds", initialDelay);
            sleepInterruption(initialDelay * 1000L);
        }

        StatusResponse initialStatus = getStatus(taskId);
        log.debug("Initial status after delay: {}", initialStatus.getStatus());

        if (initialStatus.getStatus().isFinal()) {
            log.debug("Task {} completed with initial delay", taskId);
            return initialStatus;
        }

        int retryCount = 0;
        while (retryCount < fusionBrainProperties.getMaxRetries()) {
            log.debug("Polling attempt {}/{} for task {}",
                    retryCount + 1, fusionBrainProperties.getMaxRetries(), taskId);

            StatusResponse status = getStatus(taskId);
            log.trace("Current task status: {}", status);

            if (status.getStatus().isFinal()) {
                return status;
            }

            long nextPollDelay = fusionBrainProperties.getPollInterval() * 1000L;
            log.trace("Sleeping for {} ms before next poll", nextPollDelay);
            sleepInterruption(nextPollDelay);

            retryCount++;
        }

        log.error("Timeout waiting for task {} completion after {} attempts",
                taskId, fusionBrainProperties.getMaxRetries());
        throw new FusionBrainException("Timeout waiting for task completion after " +
                fusionBrainProperties.getMaxRetries() + " attempts");
    }

    private void sleepInterruption(long millis) throws FusionBrainException {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FusionBrainException("Polling was interrupted", e);
        }
    }
}