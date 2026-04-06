package com.gymcrm.bdd.component.stepdefs;

import com.gymcrm.bdd.common.ScenarioContext;
import com.gymcrm.entity.TrainingType;
import com.gymcrm.repository.TrainingTypeRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;

public class TrainingTypeComponentSteps {

    private final MockMvc mockMvc;
    private final TrainingTypeRepository trainingTypeRepository;
    private final ScenarioContext scenarioContext;

    public TrainingTypeComponentSteps(
            MockMvc mockMvc,
            TrainingTypeRepository trainingTypeRepository,
            ScenarioContext scenarioContext
    ) {
        this.mockMvc = mockMvc;
        this.trainingTypeRepository = trainingTypeRepository;
        this.scenarioContext = scenarioContext;
    }

    @Given("the following training types exist:")
    public void the_following_training_types_exist(io.cucumber.datatable.DataTable table) {
        table.asMaps(String.class, String.class).forEach(row -> {
            String typeName = row.get("typeName");
            if (trainingTypeRepository.findByTrainingTypeName(typeName).isEmpty()) {
                TrainingType type = new TrainingType();
                type.setTrainingTypeName(typeName);
                trainingTypeRepository.save(type);
            }
        });
    }

    @When("I request all training types")
    public void i_request_all_training_types() throws Exception {
        scenarioContext.setMvcResult(
                mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/training-types")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andReturn()
        );
    }

    @Then("the response should contain {int} training types")
    public void the_response_should_contain_training_types(Integer count) throws Exception {
        String response = scenarioContext.getMvcResult().getResponse().getContentAsString();
        assertThat(response).isNotBlank();
    }

    @Then("the training types should include {string}, {string}, {string}, {string}")
    public void the_training_types_should_include(String t1, String t2, String t3, String t4) throws Exception {
        String response = scenarioContext.getMvcResult().getResponse().getContentAsString();
        assertThat(response).contains(t1, t2, t3, t4);
    }
}