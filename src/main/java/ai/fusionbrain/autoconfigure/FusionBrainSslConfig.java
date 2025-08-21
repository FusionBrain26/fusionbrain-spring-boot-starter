package ai.fusionbrain.autoconfigure;

import ai.fusionbrain.config.FusionBrainSslProperties;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
@ConditionalOnClass(name = "feign.httpclient.ApacheHttpClient")
@ConditionalOnProperty(prefix = "fusionbrain", name = "enabled", havingValue = "true")
public class FusionBrainSslConfig {

    @Bean
    public CloseableHttpClient fusionBrainHttpClient(FusionBrainSslProperties properties)
            throws Exception {

        SSLContext sslContext = createSslContext(properties);

        return HttpClients.custom()
                .setSSLSocketFactory(
                        new SSLConnectionSocketFactory(
                                sslContext,
                                getHostnameVerifier(properties)
                        )
                )
                .build();
    }

    public SSLContext createSslContext(FusionBrainSslProperties properties) throws Exception {
        SSLContextBuilder builder = SSLContexts.custom();

        if (!properties.isEnabled()) {
            builder.loadTrustMaterial((chain, authType) -> true);
        } else if (StringUtils.hasText(properties.getTruststore())) {
            Resource resource = new DefaultResourceLoader().getResource(properties.getTruststore());
            try (InputStream is = resource.getInputStream()) {
                KeyStore trustStore = KeyStore.getInstance(properties.getTruststoreType());
                trustStore.load(is, properties.getTruststorePassword().toCharArray());
                builder.loadTrustMaterial(trustStore, null);
            }
        }
        return builder.build();
    }

    public HostnameVerifier getHostnameVerifier(FusionBrainSslProperties properties) {
        return properties.isEnabled()
                ? SSLConnectionSocketFactory.getDefaultHostnameVerifier()
                : NoopHostnameVerifier.INSTANCE;
    }
}