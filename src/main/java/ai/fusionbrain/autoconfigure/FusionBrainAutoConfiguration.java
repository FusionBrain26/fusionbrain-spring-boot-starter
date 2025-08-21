package ai.fusionbrain.autoconfigure;

import ai.fusionbrain.client.FusionBrainClient;
import ai.fusionbrain.client.FusionBrainClientImpl;
import ai.fusionbrain.client.FusionBrainFeignClient;
import ai.fusionbrain.config.FeignConfig;
import ai.fusionbrain.config.FusionBrainProperties;
import ai.fusionbrain.config.FusionBrainSslProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableConfigurationProperties({FusionBrainProperties.class, FusionBrainSslProperties.class})
@ConditionalOnProperty(prefix = "fusionbrain", name = "enabled")
@EnableFeignClients(basePackageClasses = FusionBrainFeignClient.class)
@Import({FeignConfig.class, FusionBrainSslConfig.class})
@AutoConfigureAfter(name = {
        "org.springframework.cloud.openfeign.FeignAutoConfiguration",
        "org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration"
})
public class FusionBrainAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FusionBrainClient fusionBrainClient(
            FusionBrainFeignClient feignClient,
            ObjectMapper objectMapper,
            FusionBrainProperties fusionBrainProperties,
            Executor fusionBrainAsyncExecutor
    ) {
        return new FusionBrainClientImpl(feignClient, objectMapper, fusionBrainProperties, fusionBrainAsyncExecutor);
    }

    @Bean(name = "fusionBrainAsyncExecutor")
    @ConditionalOnMissingBean(name = "fusionBrainAsyncExecutor")
    public Executor fusionBrainAsyncExecutor(FusionBrainProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getAsyncCorePoolSize());
        executor.setThreadNamePrefix("FusionBrainAsync-");
        executor.initialize();
        return executor;
    }
}