package ai.fusionbrain.config;

import ai.fusionbrain.exception.FusionBrainErrorDecoder;
import feign.Client;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import feign.httpclient.ApacheHttpClient;
import lombok.RequiredArgsConstructor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {
    private static final String KEY_HEADER = "X-Key";
    private static final String KEY_PREFIX = "Key ";
    private static final String SECRET_HEADER = "X-Secret";
    private static final String SECRET_PREFIX = "Secret ";

    private final FusionBrainProperties properties;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header(KEY_HEADER, KEY_PREFIX + properties.getApiKey());
            requestTemplate.header(SECRET_HEADER, SECRET_PREFIX + properties.getApiSecret());
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FusionBrainErrorDecoder();
    }

    @Bean
    @ConditionalOnBean(CloseableHttpClient.class)
    public Client feignClient(CloseableHttpClient httpClient) {
        return new ApacheHttpClient(httpClient);
    }
}