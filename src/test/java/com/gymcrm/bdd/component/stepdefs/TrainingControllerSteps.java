package com.gymcrm.bdd.component.stepdefs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.bdd.common.ScenarioContext;
import com.gymcrm.dto.request.AddTrainingRequest;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class TrainingControllerSteps {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final ScenarioContext scenarioContext;

    private AddTrainingRequest currentRequest;
    private boolean authValid = true;

    public TrainingControllerSteps(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            ScenarioContext scenarioContext
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.scenarioContext = scenarioContext;
    }

    @Given("a valid training add request")
    public void a_valid_training_add_request() {
        currentRequest = new AddTrainingRequest();
        currentRequest.setTraineeUsername("alice");
        currentRequest.setTrainerUsername("coach");
        currentRequest.setTrainingName("Morning Yoga");
        currentRequest.setTrainingDate(new Date(System.currentTimeMillis() + 86_400_000));
        currentRequest.setDuration(60);
    }

    @Given("authentication credentials are invalid")
    public void authentication_credentials_are_invalid() {
        authValid = false;
    }

    @Given("an invalid training add request missing {string}")
    public void an_invalid_training_add_request_missing_field(String field) {
        a_valid_training_add_request();

        switch (field) {
            case "trainingName" -> currentRequest.setTrainingName(null);
            case "traineeUsername" -> currentRequest.setTraineeUsername(null);
            case "trainerUsername" -> currentRequest.setTrainerUsername(null);
            case "duration" -> currentRequest.setDuration(null);
            case "trainingDate" -> currentRequest.setTrainingDate(null);
            default -> throw new IllegalArgumentException("Unsupported field: " + field);
        }
    }

    @And("authentication is not provided")
    public void authentication_is_not_provided() {
        authValid = false;
    }

    @When("the client submits the training")
    public void the_client_submits_the_training() throws Exception {
        String body = objectMapper.writeValueAsString(currentRequest);

        var builder = MockMvcRequestBuilders.post("/api/trainings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header("X-Username", currentRequest.getTraineeUsername());

        if (authValid) {
            builder.header("X-Password", "password123");
        } else {
            builder.header("X-Password", "wrongpassword");
        }

        scenarioContext.setMvcResult(mockMvc.perform(builder).andReturn());
    }

    @Then("the training controller error message should contain {string}")
    public void the_training_controller_error_message_should_contain(String expected) throws Exception {
        String json = scenarioContext.getMvcResult().getResponse().getContentAsString();
        assertThat(json.toLowerCase()).contains(expected.toLowerCase());
    }
}