# FusionBrain Spring Boot Starter

[![Maven Central](https://img.shields.io/maven-central/v/ru.fb.fusionbrain/fusionbrain-spring-boot-starter)](https://central.sonatype.com/artifact/ru.fb.fusionbrain/fusionbrain-spring-boot-starter)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)

Spring Boot Starter for seamless integration with FusionBrain AI API.

## Features

- Auto-configuration for FusionBrain API client
- Synchronous and asynchronous operations support
- Configurable retry mechanism for a long polling process
- SSL configuration options
- Easy-to-use client interface

## Installation

### Maven
Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>ru.fb.fusionbrain</groupId>
    <artifactId>fusionbrain-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle
Add the dependency to your `build.gradle`:

```groovy
implementation 'ru.fb.fusionbrain:fusionbrain-spring-boot-starter:1.0.0'
```

## Configuration

Add the following properties to your `application.yml` or `application.properties`:

```yaml
fusionbrain:
  enabled: true
  base-url: https://api-key.fusionbrain.ai
  api-key: your-api-key
  api-secret: your-api-secret
  max-retries: 5
  poll-interval: 3 # seconds
  async-core-pool-size: 1
  ssl:
    enabled: false
    # Optional SSL configuration
    # truststore: classpath:truststore.jks
    # truststore-password: changeit
    # truststore-type: JKS
```

### Configuration Properties

| Property                              | Type    | Default                          | Description                                     |
|---------------------------------------|---------|----------------------------------|-------------------------------------------------|
| `fusionbrain.enabled`                 | Boolean | `true`                           | Enable/disable FusionBrain integration          |
| `fusionbrain.base-url`                | String  | `https://api-key.fusionbrain.ai` | FusionBrain API base URL                        |
| `fusionbrain.api-key`                 | String  | -                                | Required API key                                |
| `fusionbrain.api-secret`              | String  | -                                | Required API secret                             |
| `fusionbrain.max-retries`             | Integer | `5`                              | Maximum retry attempts for failed requests      |
| `fusionbrain.poll-interval`           | Long    | `3`                              | Polling interval for async operations (seconds) |
| `fusionbrain.async-core-pool-size`    | Integer | `1`                              | Thread pool size for async operations           |
| `fusionbrain.ssl.enabled`             | Boolean | `false`                          | Enable SSL validation                           |
| `fusionbrain.ssl.truststore`          | String  | -                                | Path to truststore file                         |
| `fusionbrain.ssl.truststore-password` | String  | -                                | Truststore password                             |
| `fusionbrain.ssl.truststore-type`     | String  | `"JKS"`                          | Truststore type (JKS/PKCS12)                    |

## Usage

```java
import org.springframework.stereotype.Service;
import client.ai.fusionbrain.FusionBrainClient;
import dto.ai.fusionbrain.EPipelineType;
import dto.ai.fusionbrain.RunResponse;
import dto.ai.fusionbrain.StatusResponse;
import request.dto.ai.fusionbrain.GenerateParams;
import request.dto.ai.fusionbrain.Text2ImageParams;

import java.util.concurrent.CompletableFuture;

@Service
public class MyAIService {

    private final FusionBrainClient fusionBrainClient;

    public MyAIService(FusionBrainClient fusionBrainClient) {
        this.fusionBrainClient = fusionBrainClient;
    }

    public void processWithAI() {
        // Get available pipelines
        var pipeline = fusionBrainClient.getPipelines(EPipelineType.TEXT2IMAGE).stream().findFirst().orElseThrow();

        // Run a pipeline
        Text2ImageParams params = new Text2ImageParams();
        params.setGenerateParams(new GenerateParams("A dramatic, moody ocean under a stormy sky"));
        RunResponse response = fusionBrainClient.runPipeline(pipeline.getId(), params);

        // Wait for completion (synchronously)
        StatusResponse status = fusionBrainClient.waitForCompletionSync(
                response.getId(), response.getStatusTime());

        // Or asynchronously
        CompletableFuture<StatusResponse> future = fusionBrainClient.waitForCompletion(
                response.getId(), response.getStatusTime());

        future.thenAccept(s -> {
            // Handle completed task
        });
    }
}
```

## API Methods

### Pipeline Operations

- `List<PipelineDTO> getPipelines()` - Get all available pipelines
- `List<PipelineDTO> getPipelines(EPipelineType type)` - Get pipelines by type
- `AvailabilityStatus getPipelineAvailability(UUID pipelineId)` - Check pipeline availability

### Task Operations

- `RunResponse runPipeline(UUID pipelineId, PipelineParams params, List<byte[]> files)` - Run a pipeline
- `RunResponse runPipeline(UUID pipelineId, PipelineParams params)` - Run a pipeline without files
- `StatusResponse getStatus(UUID taskId)` - Get task status
- `CompletableFuture<StatusResponse> waitForCompletion(UUID taskId, long initialDelay)` - Async wait for completion
- `StatusResponse waitForCompletionSync(UUID taskId, long initialDelay)` - Sync wait for completion

## Error Handling

All methods throw `FusionBrainException` for API-related errors.

## SSL Configuration

For custom SSL configuration:

1. Set `fusionbrain.ssl.enabled=true`
2. Provide truststore details if needed:

```yaml
fusionbrain:
  ssl:
    truststore: classpath:truststore.jks
    truststore-password: changeit
    truststore-type: JKS
```

## License

This project is licensed under the MIT License.
