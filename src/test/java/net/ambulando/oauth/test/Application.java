/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ambulando.oauth.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@SpringBootApplication
@ComponentScan({
    "net.ambulando.oauth.configuration",
    "net.ambulando.oauth.services",
    "net.ambulando.oauth.test.resources",
    "net.ambulando.oauth.test.services",
})
@Configuration
public class Application {

    @Bean
    @Primary
    public TokenStore tokenStore() {
        return new CustomTokenStore(2000);
    }

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    protected static class CustomTokenStore extends InMemoryTokenStore {

       public CustomTokenStore(final int flushInterval) {
           super();
           setFlushInterval(flushInterval);
       }
    }
}
