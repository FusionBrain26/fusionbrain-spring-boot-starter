package ai.fusionbrain;

import ai.fusionbrain.config.FusionBrainSslProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FusionBrainSslPropertiesTest {
    @Test
    void shouldHaveCorrectDefaultValues() {
        FusionBrainSslProperties properties = new FusionBrainSslProperties();

        assertFalse(properties.isEnabled(), "Default enabled should be false");
        assertEquals("JKS", properties.getTruststoreType(), "Default truststoreType should be JKS");
        assertNull(properties.getTruststore(), "Default truststore should be null");
        assertNull(properties.getTruststorePassword(), "Default truststorePassword should be null");
    }

    @Test
    void shouldBindPropertiesCorrectly() {
        Map<String, String> properties = Map.of(
                "fusionbrain.ssl.enabled", "true",
                "fusionbrain.ssl.truststore", "classpath:truststore.jks",
                "fusionbrain.ssl.truststore-password", "changeit",
                "fusionbrain.ssl.truststore-type", "PKCS12"
        );

        ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(source);
        BindResult<FusionBrainSslProperties> bindResult = binder.bind("fusionbrain.ssl", FusionBrainSslProperties.class);

        assertTrue(bindResult.isBound(), "Properties should be bound successfully");

        FusionBrainSslProperties boundProperties = bindResult.get();
        assertTrue(boundProperties.isEnabled(), "Enabled should be bound correctly");
        assertEquals("classpath:truststore.jks", boundProperties.getTruststore(), "Truststore should be bound correctly");
        assertEquals("changeit", boundProperties.getTruststorePassword(), "Truststore password should be bound correctly");
        assertEquals("PKCS12", boundProperties.getTruststoreType(), "Truststore type should be bound correctly");
    }
}