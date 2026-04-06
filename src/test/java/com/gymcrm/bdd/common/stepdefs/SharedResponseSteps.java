package com.gymcrm.bdd.common.stepdefs;

import com.gymcrm.bdd.common.ScenarioContext;
import io.cucumber.java.en.Then;

import static org.assertj.core.api.Assertions.assertThat;

public class SharedResponseSteps {

    private final ScenarioContext scenarioContext;

    public SharedResponseSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer expectedStatus) {
        assertThat(scenarioContext.getMvcResult()).isNotNull();
        assertThat(scenarioContext.getMvcResult().getResponse().getStatus()).isEqualTo(expectedStatus);
    }

    @Then("the error message should contain {string}")
    public void the_error_message_should_contain(String expectedMessage) throws Exception {
        assertThat(scenarioContext.getMvcResult()).isNotNull();
        String response = scenarioContext.getMvcResult().getResponse().getContentAsString();
        assertThat(response.toLowerCase()).contains(expectedMessage.toLowerCase());
    }
}