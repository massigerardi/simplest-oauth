/**
 *
 */
package net.ambulando.oauth.configuration;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * @author Massimiliano Gerardi
 *
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    private SecuritySettings settings;

    @Override
    public void configure(final ResourceServerSecurityConfigurer resources) {
        resources.resourceId(settings.getResourceId());
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
         http.csrf().disable()
             .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
             .and()
             .authorizeRequests()
             .anyRequest().permitAll();
    }

    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

}
