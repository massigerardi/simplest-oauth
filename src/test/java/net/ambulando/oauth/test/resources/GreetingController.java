/**
 *
 */
package net.ambulando.oauth.test.resources;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.ambulando.oauth.test.domain.User;

/**
 * @author Massimiliano Gerardi
 *
 */
@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";

    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greetings")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    public Greeting greetings(@AuthenticationPrincipal final User user) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, user.getName()));
    }

    @RequestMapping("/salutations")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Greeting salutations(@AuthenticationPrincipal final User user) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, user.getName()));
    }

    @RequestMapping("/hereyouare")
    public Greeting hereyouare(@AuthenticationPrincipal final User user) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, user == null? "Stranger" : user.getName()));
    }

}
