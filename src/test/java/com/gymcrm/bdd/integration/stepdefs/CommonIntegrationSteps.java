package com.gymcrm.bdd.integration.stepdefs;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.gymcrm.bdd.integration.config.IntegrationTestContext;
import com.gymcrm.entity.TrainingType;
import com.gymcrm.repository.TraineeRepository;
import com.gymcrm.repository.TrainerRepository;
import com.gymcrm.repository.TrainingRepository;
import com.gymcrm.repository.TrainingTypeRepository;
import com.gymcrm.repository.UserRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonIntegrationSteps {

    private final IntegrationTestContext ctx;
    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UserRepository userRepository;
    private final WireMockServer wireMockServer;

    public CommonIntegrationSteps(
            IntegrationTestContext ctx,
            TrainingRepository trainingRepository,
            TraineeRepository traineeRepository,
            TrainerRepository trainerRepository,
            TrainingTypeRepository trainingTypeRepository,
            UserRepository userRepository,
            WireMockServer wireMockServer
    ) {
        this.ctx = ctx;
        this.trainingRepository = trainingRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.userRepository = userRepository;
        this.wireMockServer = wireMockServer;
    }

    @Before
    public void resetState() {
        ctx.reset();
        wireMockServer.resetRequests();
    }

    @Transactional
    @Given("the integration database is clean")
    public void cleanDatabase() {
        trainingRepository.deleteAll();

        // Clear owning side relations while session is open
        traineeRepository.findAll().forEach(t -> t.getTrainers().clear());

        traineeRepository.deleteAll();
        trainerRepository.deleteAll();
        trainingTypeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Given("the following training types are seeded:")
    public void seedTrainingTypes(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        rows.forEach(row -> {
            String typeName = row.get("typeName");
            trainingTypeRepository.findByTrainingTypeName(typeName).orElseGet(() -> {
                TrainingType tt = new TrainingType();
                tt.setTrainingTypeName(typeName);
                return trainingTypeRepository.save(tt);
            });
        });
    }

    @Then("the response status should be {int}")
    public void verifyStatus(int expectedStatus) {
        assertThat(ctx.getLastResponse()).isNotNull();
        assertThat(ctx.getLastResponse().getStatusCode()).isEqualTo(expectedStatus);
    }

    @Then("the response should contain {string}")
    public void responseContains(String field) {
        assertThat(ctx.getLastResponse()).isNotNull();
        assertThat(ctx.getLastResponse().getBody().asString()).contains(field);
    }

    @Then("the response error should contain {string}")
    public void errorContains(String message) {
        assertThat(ctx.getLastResponse()).isNotNull();
        assertThat(ctx.getLastResponse().getBody().asString()).containsIgnoringCase(message);
    }

    @Then("I save the username from response as {string}")
    public void saveUsername(String key) {
        assertThat(ctx.getLastResponse()).isNotNull();
        String username = ctx.getLastResponse().jsonPath().getString("username");
        assertThat(username).isNotBlank();
        ctx.putSavedValue(key, username);
    }

    @Then("I save the password from response as {string}")
    public void savePassword(String key) {
        assertThat(ctx.getLastResponse()).isNotNull();
        String password = ctx.getLastResponse().jsonPath().getString("password");
        assertThat(password).isNotBlank();
        ctx.putSavedValue(key, password);
    }

    @Then("the response body should contain field {string} with value {string}")
    public void responseBodyContainsField(String field, String value) {
        assertThat(ctx.getLastResponse()).isNotNull();
        String actual = ctx.getLastResponse().jsonPath().getString(field);
        assertThat(actual).isEqualTo(value);
    }

    @Then("the saved {string} should not equal saved {string}")
    public void savedValuesShouldDiffer(String key1, String key2) {
        assertThat(ctx.getSavedValue(key1)).isNotEqualTo(ctx.getSavedValue(key2));
    }

    @Then("the response list should not be empty")
    public void listNotEmpty() {
        assertThat(ctx.getLastResponse()).isNotNull();
        List<?> list = ctx.getLastResponse().jsonPath().getList("$");
        assertThat(list).isNotEmpty();
    }
}