/**
 *
 */
package net.ambulando.oauth.configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Massimiliano Gerardi
 *
 */
@Getter
@Setter
public class Client {

    private String name;

    private String secret;

    private String[] grantTypes;

    private String[] resourceIds;

    private String[] scopes;
}
