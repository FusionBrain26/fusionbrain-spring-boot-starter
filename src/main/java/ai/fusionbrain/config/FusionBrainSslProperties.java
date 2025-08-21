package ai.fusionbrain.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "fusionbrain.ssl")
public class FusionBrainSslProperties {
    /**
     * Whether SSL certificate validation is enabled.
     * <p>Default: false</p>
     */
    private boolean enabled = false;

    /**
     * Path to a truststore file (classpath: or file: prefix)
     */
    private String truststore;

    /**
     * Truststore password
     */
    private String truststorePassword;

    /**
     * Truststore type (JKS, PKCS12)
     */
    private String truststoreType = "JKS";
}