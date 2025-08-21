package ai.fusionbrain;

import ai.fusionbrain.autoconfigure.FusionBrainAutoConfiguration;
import ai.fusionbrain.client.FusionBrainClient;
import ai.fusionbrain.client.FusionBrainFeignClient;
import ai.fusionbrain.config.FeignConfig;
import ai.fusionbrain.config.FusionBrainProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = FusionBrainAutoConfigurationTest.TestConfig.class)
@TestPropertySource(properties = {
        "fusionbrain.enabled=true",
        "fusionbrain.apiKey=test-key",
        "fusionbrain.apiSecret=test-secret",
        "fusionbrain.baseUrl=https://test.url"
})
class FusionBrainAutoConfigurationTest {

    @Import(FusionBrainAutoConfiguration.class)
    @EnableConfigurationProperties(FusionBrainProperties.class)
    static class TestConfig {
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        @Bean
        public FeignConfig feignConfig(FusionBrainProperties properties) {
            return new FeignConfig(properties);
        }
    }

    @Autowired(required = false)
    private FusionBrainClient fusionBrainClient;

    @MockitoBean
    private FusionBrainFeignClient fusionBrainFeignClient;

    @Autowired
    private FusionBrainProperties fusionBrainProperties;

    @Autowired
    private FeignConfig feignConfig;

    @Test
    void shouldLoadAllBeansWhenEnabled() {
        assertThat(fusionBrainClient).isNotNull();
        assertThat(fusionBrainFeignClient).isNotNull();
        assertThat(fusionBrainProperties).isNotNull();
        assertThat(feignConfig).isNotNull();

        assertThat(fusionBrainProperties.getApiKey()).isEqualTo("test-key");
        assertThat(fusionBrainProperties.getApiSecret()).isEqualTo("test-secret");
        assertThat(fusionBrainProperties.getBaseUrl()).isEqualTo("https://test.url");
    }

    @Nested
    @SpringBootTest(classes = TestConfig.class)
    @TestPropertySource(properties = "fusionbrain.enabled=false")
    class DisabledConfigurationTest {
        @Autowired(required = false)
        private FusionBrainClient fusionBrainClient;

        @Test
        void shouldNotLoadBeansWhenDisabled() {
            assertThat(fusionBrainClient).isNull();
        }
    }
}