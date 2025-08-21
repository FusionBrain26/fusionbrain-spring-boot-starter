package ai.fusionbrain;

import ai.fusionbrain.autoconfigure.FusionBrainSslConfig;
import ai.fusionbrain.config.FusionBrainSslProperties;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = FusionBrainSslIntegrationTest.TestConfig.class)
@TestPropertySource(properties = {
        "fusionbrain.enabled=true",
        "fusionbrain.ssl.enabled=false"
})
class FusionBrainSslIntegrationTest {

    @Import({FusionBrainSslConfig.class, FusionBrainSslProperties.class})
    static class TestConfig {
    }

    @Autowired(required = false)
    private CloseableHttpClient httpClient;

    @Autowired
    private FusionBrainSslProperties sslProperties;

    @Test
    void shouldLoadHttpClientWithDisabledSsl() {
        assertThat(httpClient).isNotNull();
        assertThat(sslProperties.isEnabled()).isFalse();
    }
}

@SpringBootTest(classes = FusionBrainSslWithTruststoreIntegrationTest.TestConfig.class)
@TestPropertySource(properties = {
        "fusionbrain.enabled=true",
        "fusionbrain.ssl.enabled=true",
        "fusionbrain.ssl.truststore=classpath:truststore.jks",
        "fusionbrain.ssl.truststore-password=changeit"
})
class FusionBrainSslWithTruststoreIntegrationTest {

    @Import({FusionBrainSslConfig.class, FusionBrainSslProperties.class})
    static class TestConfig {
    }

    @Autowired(required = false)
    private CloseableHttpClient httpClient;

    @Test
    void shouldLoadHttpClientWithTruststore() {
        assertThat(httpClient).isNotNull();
    }
}