package com.gymcrm.bdd.component.stepdefs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.bdd.common.ScenarioContext;
import com.gymcrm.repository.UserRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthenticationComponentSteps {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final ScenarioContext scenarioContext;

    public AuthenticationComponentSteps(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            UserRepository userRepository,
            ScenarioContext scenarioContext
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.scenarioContext = scenarioContext;
    }

    @When("I authenticate with username {string} and password {string}")
    public void i_authenticate_with_username_and_password(String username, String password) throws Exception {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        String body = objectMapper.writeValueAsString(credentials);

        scenarioContext.setMvcResult(
                mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                ).andReturn()
        );

        scenarioContext.setCurrentUsername(username);

        if (scenarioContext.getMvcResult().getResponse().getStatus() == 200) {
            String response = scenarioContext.getMvcResult().getResponse().getContentAsString();
            if (response.contains("token")) {
                scenarioContext.setAuthToken(extractTokenFromResponse(response));
            }
        }
    }

    @Given("I have authenticated as {string} with password {string}")
    public void i_have_authenticated_as(String username, String password) throws Exception {
        i_authenticate_with_username_and_password(username, password);
    }

    @When("I request protected resource with valid token")
    public void i_request_protected_resource_with_valid_token() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .get("/api/profile")
                .contentType(MediaType.APPLICATION_JSON);

        if (scenarioContext.getAuthToken() != null) {
            requestBuilder.header("Authorization", "Bearer " + scenarioContext.getAuthToken());
        } else {
            requestBuilder.header("X-Username", scenarioContext.getCurrentUsername())
                    .header("X-Password", "pass123");
        }

        scenarioContext.setMvcResult(mockMvc.perform(requestBuilder).andReturn());
    }

    @When("I request protected resource without authentication")
    public void i_request_protected_resource_without_authentication() throws Exception {
        scenarioContext.setMvcResult(
                mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andReturn()
        );
    }

    @When("I request admin resource")
    public void i_request_admin_resource() throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .get("/api/admin/settings")
                .contentType(MediaType.APPLICATION_JSON);

        if (scenarioContext.getAuthToken() != null) {
            requestBuilder.header("Authorization", "Bearer " + scenarioContext.getAuthToken());
        } else {
            requestBuilder.header("X-Username", scenarioContext.getCurrentUsername())
                    .header("X-Password", "pass123");
        }

        scenarioContext.setMvcResult(mockMvc.perform(requestBuilder).andReturn());
    }

    @When("I request trainer-only resource")
    public void i_request_trainer_only_resource() throws Exception {
        scenarioContext.setMvcResult(
                mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/trainers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Username", scenarioContext.getCurrentUsername())
                                .header("X-Password", "pass123")
                ).andReturn()
        );
    }

    @Given("the token has expired")
    public void the_token_has_expired() {
        if (scenarioContext.getAuthToken() != null) {
            scenarioContext.setAuthToken("expired_token_" + System.currentTimeMillis());
        }
    }

    @When("I request protected resource with expired token")
    public void i_request_protected_resource_with_expired_token() throws Exception {
        scenarioContext.setMvcResult(
                mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + scenarioContext.getAuthToken())
                ).andReturn()
        );
    }

    @Then("the authentication token should be generated")
    public void the_authentication_token_should_be_generated() throws Exception {
        String response = scenarioContext.getMvcResult().getResponse().getContentAsString();
        assertThat(response).isNotEmpty();
    }

    @Then("I should have access to admin features")
    public void i_should_have_access_to_admin_features() {
        assertThat(scenarioContext.getMvcResult().getResponse().getStatus()).isEqualTo(200);
    }

    private String extractTokenFromResponse(String response) {
        int tokenStart = response.indexOf("\"token\":\"") + 9;
        int tokenEnd = response.indexOf("\"", tokenStart);
        if (tokenStart > 8 && tokenEnd > tokenStart) {
            return response.substring(tokenStart, tokenEnd);
        }
        return null;
    }
}