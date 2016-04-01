# simplest-oauth
This a simple implementation of oauth *quite* ready to go.
The library needs just some configuration and a service to retrieve your users.

## Import dependency

At the moment the library is not deployed on a central repository.

```xml

<dependency>
    <groupId>net.ambulando</groupId>
    <artifactId>simplest-oauth</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>

```

## Configuration

in `application.yml` add a new sections `securitySettings`. an example is provided in `src/test/resources/application.yml` 

```yml
securitySettings:
  resourceId: "test"
  refreshTokenValiditySeconds: 44200 
  accessTokenValiditySeconds: 88400
  clients:
  - name: "clientapp"
    secret: "123456"
    scopes:
      - "read"
      - "write"
    grantTypes:
      - "password"
      - "refresh_token"
    resourceIds:
    - "test"
  - name: "clienttest"
    secret: "123654"
    scopes:
      - "read"
      - "write"
    grantTypes:
      - "password"
    resourceIds:
      - "test"
```

## Define components scan

You need to import the configuration adding the following package in one of your own configuration classes:

```java
@ComponentScan({
    "net.ambulando.oauth",
    ...,
})
```
## UserService

implement the interface `net.ambulando.oauth.services.UserSecurityService` to retrieve your users.
The class returned as user must implements `org.springframework.security.core.userdetails.UserDetails`.

## Token Store

The library uses `InMemoryTokenStore` as default.
If a different TokenStore is desired, you create a different implementation of `TokenStore` and must annotate the bean with `@Primary`

```java

@Bean
@Primary
public TokenStore myTokenStore() {
...
}
```

## Setting up rules

This solution makes use of the `@PreAuthorize` annotation to add different level of security access to paths, allowing a very fine tuning of security controls.
To define the security level of a path, you can use the annotation at class level or at method level.

Check the example in `net.ambulando.oauth.test.resources.GreetingController` 





