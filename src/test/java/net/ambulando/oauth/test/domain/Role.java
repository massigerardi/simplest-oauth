/**
 *
 */
package net.ambulando.oauth.test.domain;

import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Massimiliano Gerardi
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Role implements GrantedAuthority {

    /**
     *
     */
    private static final long serialVersionUID = 8985806030428404580L;
    private String role;

    @Override
    public String getAuthority() {
        return role;
    }

}