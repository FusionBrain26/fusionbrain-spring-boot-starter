package ai.fusionbrain;

import ai.fusionbrain.config.FeignConfig;
import ai.fusionbrain.config.FusionBrainProperties;
import ai.fusionbrain.exception.FusionBrainErrorDecoder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.ErrorDecoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeignConfigTest {

    @Mock
    private FusionBrainProperties properties;

    @Mock
    private RequestTemplate requestTemplate;

    private FeignConfig feignConfig;

    @BeforeEach
    void setUp() {
        feignConfig = new FeignConfig(properties);
    }

    @Test
    void requestInterceptor_shouldAddHeaders_whenPropertiesAreSet() {
        // Arrange
        String apiKey = "test-api-key";
        String apiSecret = "test-api-secret";
        when(properties.getApiKey()).thenReturn(apiKey);
        when(properties.getApiSecret()).thenReturn(apiSecret);

        // Act
        RequestInterceptor interceptor = feignConfig.requestInterceptor();
        interceptor.apply(requestTemplate);

        // Assert
        verify(requestTemplate).header("X-Key", "Key " + apiKey);
        verify(requestTemplate).header("X-Secret", "Secret " + apiSecret);
        verifyNoMoreInteractions(requestTemplate);
    }

    @Test
    void requestInterceptor_shouldNotFail_whenPropertiesAreNull() {
        // Arrange
        when(properties.getApiKey()).thenReturn(null);
        when(properties.getApiSecret()).thenReturn(null);

        // Act
        RequestInterceptor interceptor = feignConfig.requestInterceptor();
        interceptor.apply(requestTemplate);

        // Assert
        verify(requestTemplate).header("X-Key", "Key null");
        verify(requestTemplate).header("X-Secret", "Secret null");
    }

    @Test
    void errorDecoder_shouldReturnFusionBrainErrorDecoderInstance() {
        // Act
        ErrorDecoder errorDecoder = feignConfig.errorDecoder();

        // Assert
        assertNotNull(errorDecoder);
        assertInstanceOf(FusionBrainErrorDecoder.class, errorDecoder);
    }
}