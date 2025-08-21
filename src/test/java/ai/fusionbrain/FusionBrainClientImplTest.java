package ai.fusionbrain;

import ai.fusionbrain.client.FusionBrainClient;
import ai.fusionbrain.client.FusionBrainClientImpl;
import ai.fusionbrain.client.FusionBrainFeignClient;
import ai.fusionbrain.config.FusionBrainProperties;
import ai.fusionbrain.dto.*;
import ai.fusionbrain.dto.request.GenerateParams;
import ai.fusionbrain.dto.request.Text2ImageParams;
import ai.fusionbrain.exception.FusionBrainException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

class FusionBrainClientImplTest {
    private static ObjectMapper objectMapper;
    private static FusionBrainFeignClient feignClient;
    private static FusionBrainClient fusionBrainClient;
    private static FusionBrainProperties fusionBrainProperties;

    @BeforeAll
    static void setUp() {
        feignClient = Mockito.mock(FusionBrainFeignClient.class);
        objectMapper = new ObjectMapper();
        fusionBrainProperties = new FusionBrainProperties();
        fusionBrainProperties.setMaxRetries(1);
        fusionBrainProperties.setPollInterval(1);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setThreadNamePrefix("FusionBrainAsync-");
        executor.initialize();
        fusionBrainClient = new FusionBrainClientImpl(feignClient, objectMapper, fusionBrainProperties, executor);
    }

    @Test
    void testGetPipelines() throws FusionBrainException {
        when(feignClient.getPipelines(null)).thenReturn(Collections.emptyList());

        List<PipelineDTO> pipelines = fusionBrainClient.getPipelines();

        assertThat(pipelines).isNotNull();
        verify(feignClient).getPipelines(null);
    }

    @Test
    void testGetPipelinesWithType() throws FusionBrainException {
        when(feignClient.getPipelines(EPipelineType.TEXT2IMAGE)).thenReturn(Collections.emptyList());

        List<PipelineDTO> pipelines = fusionBrainClient.getPipelines(EPipelineType.TEXT2IMAGE);

        assertThat(pipelines).isNotNull();
        verify(feignClient).getPipelines(EPipelineType.TEXT2IMAGE);
    }

    @Test
    void testGetPipelineAvailability() throws FusionBrainException {
        UUID pipelineId = UUID.randomUUID();
        AvailabilityStatus expectedStatus = new AvailabilityStatus();
        when(feignClient.getPipelineAvailability(pipelineId)).thenReturn(expectedStatus);

        AvailabilityStatus status = fusionBrainClient.getPipelineAvailability(pipelineId);

        assertThat(status).isEqualTo(expectedStatus);
        verify(feignClient).getPipelineAvailability(pipelineId);
    }

    @Test
    void testGetPipelineAvailabilityThrowsException() {
        UUID pipelineId = UUID.randomUUID();
        when(feignClient.getPipelineAvailability(pipelineId)).thenThrow(new RuntimeException("Error"));

        assertThatExceptionOfType(FusionBrainException.class)
                .isThrownBy(() -> fusionBrainClient.getPipelineAvailability(pipelineId))
                .withMessage("Failed to get pipeline availability");
    }

    @Test
    void testRunPipeline() throws FusionBrainException {
        UUID pipelineId = UUID.randomUUID();
        GenerateParams generateParams = new GenerateParams("text");
        Text2ImageParams params = new Text2ImageParams();
        params.setGenerateParams(generateParams);

        List<byte[]> files = Collections.emptyList();
        RunResponse expectedResponse = new RunResponse();
        when(feignClient.runPipeline(any(), eq(pipelineId), eq(files))).thenReturn(expectedResponse);

        RunResponse response = fusionBrainClient.runPipeline(pipelineId, params, files);

        assertThat(response).isEqualTo(expectedResponse);
        ArgumentCaptor<JsonNode> paramsCaptor = ArgumentCaptor.forClass(JsonNode.class);
        verify(feignClient).runPipeline(paramsCaptor.capture(), eq(pipelineId), eq(files));
        assertThat(paramsCaptor.getValue()).isEqualTo(objectMapper.valueToTree(params));
    }

    @Test
    void testRunPipelineThrowsException() {
        UUID pipelineId = UUID.randomUUID();
        GenerateParams generateParams = new GenerateParams("text");
        Text2ImageParams params = new Text2ImageParams();
        params.setGenerateParams(generateParams);
        List<byte[]> files = Collections.emptyList();
        when(feignClient.runPipeline(any(), eq(pipelineId), eq(files))).thenThrow(new RuntimeException("Error"));

        assertThatExceptionOfType(FusionBrainException.class)
                .isThrownBy(() -> fusionBrainClient.runPipeline(pipelineId, params, files))
                .withMessage("Failed to run pipeline");
    }

    @Test
    void testGetStatus() throws FusionBrainException {
        UUID taskId = UUID.randomUUID();
        StatusResponse expectedResponse = new StatusResponse();
        when(feignClient.getStatus(taskId)).thenReturn(expectedResponse);

        StatusResponse response = fusionBrainClient.getStatus(taskId);

        assertThat(response).isEqualTo(expectedResponse);
        verify(feignClient).getStatus(taskId);
    }

    @Test
    void testGetStatusThrowsException() {
        UUID taskId = UUID.randomUUID();
        when(feignClient.getStatus(taskId)).thenThrow(new RuntimeException("Error"));

        assertThatExceptionOfType(FusionBrainException.class)
                .isThrownBy(() -> fusionBrainClient.getStatus(taskId))
                .withMessage("Failed to get task status");
    }

    @Test
    void testWaitForCompletion() throws Exception {
        UUID taskId = UUID.randomUUID();
        StatusResponse initialStatus = new StatusResponse();
        initialStatus.setStatus(EResourceStatus.INITIAL);
        StatusResponse finalStatus = new StatusResponse();
        finalStatus.setStatus(EResourceStatus.DONE);

        when(feignClient.getStatus(taskId)).thenReturn(initialStatus, finalStatus);

        CompletableFuture<StatusResponse> future = fusionBrainClient.waitForCompletion(taskId, 0);

        // Using Awaitility for async assertions
        await().atMost(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(future).isCompleted();
                    assertThat(future.get()).isEqualTo(finalStatus);
                });

        verify(feignClient, times(2)).getStatus(taskId);
    }

    @Test
    void testWaitForCompletionThrowsExceptionOnInitialStatus() {
        UUID taskId = UUID.randomUUID();
        when(feignClient.getStatus(taskId)).thenThrow(new RuntimeException("Error"));

        CompletableFuture<StatusResponse> future = fusionBrainClient.waitForCompletion(taskId, 0);

        await().atMost(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(future).isCompletedExceptionally();
                    assertThatExceptionOfType(ExecutionException.class)
                            .isThrownBy(future::get)
                            .withCauseInstanceOf(FusionBrainException.class)
                            .withMessageContaining("Failed to get task status");
                });
    }

    @Test
    void testWaitForCompletionTimeout() {
        UUID taskId = UUID.randomUUID();
        StatusResponse initialStatus = new StatusResponse();
        initialStatus.setStatus(EResourceStatus.INITIAL);

        when(feignClient.getStatus(taskId)).thenReturn(initialStatus);

        CompletableFuture<StatusResponse> future = fusionBrainClient.waitForCompletion(taskId, 0);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(future).isCompletedExceptionally();
                    assertThatExceptionOfType(ExecutionException.class)
                            .isThrownBy(future::get)
                            .withCauseInstanceOf(FusionBrainException.class)
                            .withMessageContaining("Timeout waiting for task completion after " + fusionBrainProperties.getMaxRetries() + " attempts");
                });
    }

    // Additional test for immediate completion
    @Test
    void testWaitForCompletionImmediateFinalStatus() {
        UUID taskId = UUID.randomUUID();
        StatusResponse finalStatus = new StatusResponse();
        finalStatus.setStatus(EResourceStatus.DONE);

        when(feignClient.getStatus(taskId)).thenReturn(finalStatus);

        CompletableFuture<StatusResponse> future = fusionBrainClient.waitForCompletion(taskId, 0);

        await().atMost(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(future).isCompleted();
                    assertThat(future.get()).isEqualTo(finalStatus);
                });

        verify(feignClient, times(1)).getStatus(taskId);
    }

    @Test
    void testWaitForCompletionSyncSuccess() {
        UUID taskId = UUID.randomUUID();
        StatusResponse expectedResponse = new StatusResponse();
        expectedResponse.setStatus(EResourceStatus.DONE);

        when(feignClient.getStatus(taskId)).thenReturn(expectedResponse);

        StatusResponse response = fusionBrainClient.waitForCompletionSync(taskId, 0);

        assertThat(response).isEqualTo(expectedResponse);
        verify(feignClient).getStatus(taskId);
    }

    @Test
    void testWaitForCompletionSyncInterruptedException() throws Exception {
        UUID taskId = UUID.randomUUID();

        // Mock the future to throw InterruptedException
        CompletableFuture<StatusResponse> mockFuture = mock(CompletableFuture.class);
        when(mockFuture.get()).thenThrow(new InterruptedException("Interrupted"));

        // Spy on the client to return our mock future
        FusionBrainClient spyClient = spy(fusionBrainClient);
        when(spyClient.waitForCompletion(eq(taskId), anyLong())).thenReturn(mockFuture);

        assertThatExceptionOfType(FusionBrainException.class)
                .isThrownBy(() -> spyClient.waitForCompletionSync(taskId, 0))
                .withMessage("Failed to execute synchronous operation")
                .withCauseInstanceOf(InterruptedException.class);

        // Verify thread interrupt flag was set
        assertThat(Thread.interrupted()).isTrue();
    }

    @Test
    void testWaitForCompletionSyncExecutionException() throws Exception {
        UUID taskId = UUID.randomUUID();

        // Mock the future to throw ExecutionException
        CompletableFuture<StatusResponse> mockFuture = mock(CompletableFuture.class);
        when(mockFuture.get()).thenThrow(new ExecutionException("Execution failed", new RuntimeException()));

        // Spy on the client to return our mock future
        FusionBrainClient spyClient = spy(fusionBrainClient);
        when(spyClient.waitForCompletion(eq(taskId), anyLong())).thenReturn(mockFuture);

        assertThatExceptionOfType(FusionBrainException.class)
                .isThrownBy(() -> spyClient.waitForCompletionSync(taskId, 0))
                .withMessage("Failed to execute synchronous operation")
                .withCauseInstanceOf(ExecutionException.class);
    }
}
