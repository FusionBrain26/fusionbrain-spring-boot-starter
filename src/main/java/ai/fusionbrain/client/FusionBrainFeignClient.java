package ai.fusionbrain.client;

import ai.fusionbrain.config.FeignConfig;
import ai.fusionbrain.dto.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "fusionBrainClient",
        url = "${fusionbrain.base-url}",
        path = "/key/api/v1",
        configuration = FeignConfig.class
)
@ConditionalOnProperty(prefix = "fusionbrain", name = "enabled", havingValue = "true")
public interface FusionBrainFeignClient {
    @GetMapping("/pipelines")
    List<PipelineDTO> getPipelines(@RequestParam(value = "type", required = false) EPipelineType type);

    @GetMapping("/pipeline/{pipeline_id}/availability")
    AvailabilityStatus getPipelineAvailability(@PathVariable("pipeline_id") UUID pipelineId);

    @PostMapping(value = "/pipeline/run", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    RunResponse runPipeline(
            @RequestPart(value = "params", required = false) JsonNode params,
            @RequestParam("pipeline_id") UUID pipelineId,
            @RequestPart(value = "file", required = false) List<byte[]> files
    );

    @GetMapping("/pipeline/status/{uuid}")
    StatusResponse getStatus(@PathVariable UUID uuid);
}
