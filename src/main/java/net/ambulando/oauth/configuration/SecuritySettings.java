/**
 *
 */
package net.ambulando.oauth.configuration;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Massimiliano Gerardi
 *
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "securitySettings")
public class SecuritySettings {

    private List<Client> clients;

    private String resourceId;

    /**
     * the refresh token validity in seconds.
     * Default value 30d
     */
    private int refreshTokenValiditySeconds = 60 * 60 * 24 * 30;


    /**
     * the access token validity in seconds.
     * Default value 12h
     */
    private int accessTokenValiditySeconds = 60 * 60 * 12;

}
