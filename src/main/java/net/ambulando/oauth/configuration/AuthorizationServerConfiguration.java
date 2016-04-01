/**
 *
 */
package net.ambulando.oauth.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import net.ambulando.oauth.services.UserDetailsServiceImpl;

/**
 * @author Massimiliano Gerardi
 *
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecuritySettings settings;

    @Autowired
    private UserDetailsServiceImpl userService;

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints)
            throws Exception {
        endpoints
            .tokenStore(tokenStore)
            .authenticationManager(authenticationManager)
            .userDetailsService(userService);
    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
        final InMemoryClientDetailsServiceBuilder builder = clients.inMemory();
        settings.getClients().forEach(c -> builder
                    .withClient(c.getName())
                    .secret(c.getSecret())
                    .authorizedGrantTypes(c.getGrantTypes())
                    .authorities("USER")
                    .scopes(c.getScopes())
                    .resourceIds(c.getResourceIds()));
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        final DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setRefreshTokenValiditySeconds(settings.getRefreshTokenValiditySeconds());
        tokenServices.setAccessTokenValiditySeconds(settings.getAccessTokenValiditySeconds());
        tokenServices.setTokenStore(tokenStore);
        return tokenServices;
    }

}
