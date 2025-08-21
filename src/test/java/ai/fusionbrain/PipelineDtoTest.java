package ai.fusionbrain;

import ai.fusionbrain.dto.EPipelineStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PipelineDtoTest {
    @Test
    void testEPipelineStatusIsDisabled() {
        // Test ACTIVE -> should return false
        assertThat(EPipelineStatus.ACTIVE.isDisabled()).isFalse();

        // Test DISABLED_MANUALLY -> should return true
        assertThat(EPipelineStatus.DISABLED_MANUALLY.isDisabled()).isTrue();

        // Test DISABLED_BY_QUEUE -> should return true
        assertThat(EPipelineStatus.DISABLED_BY_QUEUE.isDisabled()).isTrue();
    }
}
