package net.ambulando.oauth.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

/**
 * A Configuration
 * @author Massimiliano Gerardi
 *
 */
@Configuration
@EnableConfigurationProperties
@EnableAutoConfiguration
public class SecurityConfiguration {

    @Bean(name = "defaultInMemoryStore")
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    @ConfigurationProperties(prefix = "settings")
    @Bean
    public SecuritySettings settings() {
        return new SecuritySettings();
    }

}
