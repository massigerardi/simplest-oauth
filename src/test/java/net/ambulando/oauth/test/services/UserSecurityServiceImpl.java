package net.ambulando.oauth.test.services;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.ambulando.oauth.services.UserSecurityService;
import net.ambulando.oauth.test.domain.Role;
import net.ambulando.oauth.test.domain.User;

@Service
public class UserSecurityServiceImpl implements UserSecurityService<User> {

    Map<String, User> users = Maps.newHashMap();

    @PostConstruct
    public void setUp() {
        final Role userRole = Role.builder().role("ROLE_USER").build();
        final Role adminRole = Role.builder().role("ROLE_ADMIN").build();
        final Role guestRole = Role.builder().role("ROLE_GUEST").build();

        users.put("roy", User.builder().name("Roy").username("roy").password("spring").roles(Lists.newArrayList(userRole, adminRole)).build());
        users.put("greg", User.builder().name("Greg").username("greg").password("spring").roles(Lists.newArrayList(userRole, guestRole)).build());
        users.put("craig", User.builder().name("Craig").username("craig").password("spring").roles(Lists.newArrayList(userRole)).build());

    }

    @Override
    public User getByUsername(final String username) {
        return users.get(username);
    }

}
