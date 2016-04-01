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

package net.ambulando.oauth.test.resources;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ambulando.oauth.test.Application;
/**
 *
 * @author Massimiliano Gerardi
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
public class GreetingControllerTest {

    final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    WebApplicationContext context;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @InjectMocks
    GreetingController controller;

    private MockMvc mvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(context).addFilter(springSecurityFilterChain).build();
    }

    private Map<String, ?> getAccessTokenFromRefreshToken(final String refreshToken, final String scope, final String clientId, final String secret, final String grant) throws Exception {
        final String credentials = clientId+":"+secret;
        final String authorization = "Basic " + new String(Base64Utils.encode(credentials.getBytes()));
        final String contentType = MediaType.APPLICATION_JSON + ";charset=UTF-8";

        final String content = mvc
                .perform(post("/oauth/token").header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED).param("refresh_token", refreshToken)
                        .param("grant_type", grant).param("scope", scope)
                        .param("client_id", clientId).param("client_secret", secret))
                .andExpect(status().isOk()).andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.access_token", is(notNullValue())))
                .andExpect(jsonPath("$.token_type", is(equalTo("bearer"))))
                .andExpect(jsonPath("$.refresh_token", is(notNullValue())))
                .andExpect(jsonPath("$.expires_in", is(greaterThan(4000))))
                .andExpect(jsonPath("$.scope", is(equalTo("read write")))).andReturn().getResponse()
                .getContentAsString();

        return mapper.readValue(content, new TypeReference<Map<String,?>>() {});
    }

    private Map<String, ?> getAccessTokenFromPassword(final String username, final String password, final String scope, final String clientId, final String secret, final String grant) throws Exception {
        final String credentials = clientId+":"+secret;
        final String authorization = "Basic " + new String(Base64Utils.encode(credentials.getBytes()));
        final String contentType = MediaType.APPLICATION_JSON + ";charset=UTF-8";

        final String content = mvc
                .perform(post("/oauth/token").header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED).param("username", username)
                        .param("password", password).param("grant_type", grant).param("scope", scope)
                        .param("client_id", clientId).param("client_secret", secret))
                .andExpect(status().isOk()).andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.access_token", is(notNullValue())))
                .andExpect(jsonPath("$.token_type", is(equalTo("bearer"))))
                .andExpect(jsonPath("$.scope", is(equalTo("read write")))).andReturn().getResponse()
                .getContentAsString();

        return mapper.readValue(content, new TypeReference<Map<String,?>>() {});
    }

    @Test
    public void refreshToken() throws Exception {
        Map<String, ?> response = getAccessTokenFromPassword("greg", "spring", "read write", "clientapp", "123456", "password");
        final String refreshToken = response.get("refresh_token").toString();
        final String accessToken = response.get("access_token").toString();
        response = getAccessTokenFromRefreshToken(refreshToken, "read write", "clientapp", "123456", "refresh_token");
        final String accessToken2 = response.get("access_token").toString();
        Assert.assertNotEquals(accessToken, accessToken2);

    }

    @Test
    public void refreshTokenDisabled() throws Exception {
        final Map<String, ?> response = getAccessTokenFromPassword("greg", "spring", "read write", "clienttest", "123654", "password");
        Assert.assertNull(response.get("refresh_token"));
    }

    @Test
    public void greetingUnknownUser() throws Exception {
        mvc.perform(get("/hereyouare").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", is("Hello, Stranger!")));

    }

    @Test
    public void greetingAuthorizedGuest() throws Exception {
        final Map<String, ?> response = getAccessTokenFromPassword("greg", "spring", "read write", "clientapp", "123456", "password");


        mvc.perform(get("/greetings").header("Authorization", "Bearer " + response.get("access_token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("Hello, Greg!")));


        mvc.perform(get("/salutations").header("Authorization", "Bearer " + response.get("access_token")).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("access_denied")));


        mvc.perform(get("/hereyouare").header("Authorization", "Bearer " + response.get("access_token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("Hello, Greg!")));

    }

    @Test
    public void greetingAuthorizedAdmin() throws Exception {
        final Map<String, ?> response = getAccessTokenFromPassword("roy", "spring", "read write", "clientapp", "123456", "password");

        mvc.perform(get("/greetings").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        mvc.perform(get("/salutations").header("Authorization", "Bearer " + response.get("access_token")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", is("Hello, Roy!")));


        mvc.perform(get("/greetings").header("Authorization", "Bearer " + response.get("access_token")).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));


        mvc.perform(get("/hereyouare").header("Authorization", "Bearer " + response.get("access_token")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", is("Hello, Roy!")));

    }

    @Test
    public void greetingAuthorizedUser() throws Exception {
        final Map<String, ?> response = getAccessTokenFromPassword("craig", "spring", "read write", "clientapp", "123456", "password"
                + "");

        mvc.perform(get("/salutations").header("Authorization", "Bearer " + response.get("access_token")).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("access_denied")));


        mvc.perform(get("/greetings").header("Authorization", "Bearer " + response.get("access_token")).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("access_denied")));


        mvc.perform(get("/hereyouare").header("Authorization", "Bearer " + response.get("access_token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("Hello, Craig!")));

    }


}
