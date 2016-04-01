package net.ambulando.oauth.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserSecurityService<T extends UserDetails> {

    T getByUsername(String username);

}
