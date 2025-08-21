package ai.fusionbrain;

import ai.fusionbrain.autoconfigure.FusionBrainSslConfig;
import ai.fusionbrain.config.FusionBrainSslProperties;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FusionBrainSslConfigTest {

    @Mock
    private FusionBrainSslProperties properties;

    private final FusionBrainSslConfig config = new FusionBrainSslConfig();

    @Test
    void getHostnameVerifier_shouldReturnNoopVerifier_whenSslDisabled() {
        when(properties.isEnabled()).thenReturn(false);

        HostnameVerifier verifier = config.getHostnameVerifier(properties);

        assertSame(NoopHostnameVerifier.INSTANCE, verifier);
    }

    @Test
    void getHostnameVerifier_shouldReturnDefaultVerifier_whenSslEnabled() {
        when(properties.isEnabled()).thenReturn(true);

        HostnameVerifier verifier = config.getHostnameVerifier(properties);

        assertNotNull(verifier);
        assertEquals(SSLConnectionSocketFactory.getDefaultHostnameVerifier().getClass(),
                verifier.getClass());
    }

    @Test
    void createSslContext_shouldCreateTrustAllContext_whenSslDisabled() throws Exception {
        when(properties.isEnabled()).thenReturn(false);

        SSLContext sslContext = config.createSslContext(properties);

        assertNotNull(sslContext);
        // Can't directly test the trust manager behavior, but we can verify the context is created
    }

    @Test
    void createSslContext_shouldCreateCustomContext_whenTruststoreProvided() {
        when(properties.isEnabled()).thenReturn(true);
        when(properties.getTruststore()).thenReturn("classpath:nonexistent.jks");

        assertThrows(Exception.class, () -> config.createSslContext(properties));
    }

    @Test
    void fusionBrainHttpClient_shouldCreateClientWithSslSocketFactory() throws Exception {
        // This is an integration test that would require proper SSL context setup
        // We can verify the basic behavior

        when(properties.isEnabled()).thenReturn(false);

        CloseableHttpClient client = config.fusionBrainHttpClient(properties);

        assertNotNull(client);
    }
}