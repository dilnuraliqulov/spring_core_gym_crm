package com.gymcrm.bdd.integration.stepdefs;

import com.gymcrm.bdd.integration.config.IntegrationTestContext;
import com.gymcrm.repository.TrainingTypeRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TrainerIntegrationSteps {

    private final IntegrationTestContext ctx;
    private final TrainingTypeRepository trainingTypeRepository;

    public TrainerIntegrationSteps(
            IntegrationTestContext ctx,
            TrainingTypeRepository trainingTypeRepository
    ) {
        this.ctx = ctx;
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @When("I call GET {string} with saved trainer credentials")
    public void callGetWithSavedTrainerCredentials(String path) {
        String username = ctx.getSavedValue("trainerUsername");
        String password = ctx.getSavedValue("trainerPassword");
        String resolvedPath = path.replace("{trainerUsername}", username);

        ctx.setLastResponse(
                RestAssured.given()
                        .header("X-Password", password)
                        .get(ctx.baseUrl() + resolvedPath)
        );
    }

    @Given("a registered trainer with firstName {string} and lastName {string} and specialization {int}")
    public void registerTrainer(String firstName, String lastName, int specializationIndex) {
        List<Long> ids = trainingTypeRepository.findAll()
                .stream()
                .map(tt -> tt.getId())
                .toList();

        assertThat(ids).hasSizeGreaterThanOrEqualTo(specializationIndex);
        Long specId = ids.get(specializationIndex - 1);

        String body = String.format(
                "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"specialization\":%d}",
                firstName, lastName, specId
        );

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .post(ctx.baseUrl() + "/api/trainers");

        ctx.putSavedValue("trainerUsername", response.jsonPath().getString("username"));
        ctx.putSavedValue("trainerPassword", response.jsonPath().getString("password"));
    }

    @When("I request unassigned trainers for the trainee")
    public void getUnassignedTrainers() {
        String traineeUsername = ctx.getSavedValue("traineeUsername");
        String traineePassword = ctx.getSavedValue("traineePassword");

        ctx.setLastResponse(
                RestAssured.given()
                        .header("X-Password", traineePassword)
                        .get(ctx.baseUrl() + "/api/trainees/" + traineeUsername + "/unassigned-trainers")
        );
    }

    @Then("the response list should contain trainer {string}")
    public void responseListContainsTrainer(String expectedUsername) {
        List<String> usernames = ctx.getLastResponse().jsonPath().getList("username");
        assertThat(usernames).contains(expectedUsername);
    }

    @When("I update the trainer profile with:")
    public void updateTrainerProfile(DataTable dataTable) {
        Map<String, String> fields = dataTable.asMap(String.class, String.class);
        String username = ctx.getSavedValue("trainerUsername");
        String password = ctx.getSavedValue("trainerPassword");

        List<Long> ids = trainingTypeRepository.findAll()
                .stream()
                .map(tt -> tt.getId())
                .toList();
        Long specId = ids.isEmpty() ? 1L : ids.get(0);

        String body = String.format(
                "{\"username\":\"%s\",\"firstName\":\"%s\",\"lastName\":\"%s\",\"specialization\":%d,\"isActive\":%s}",
                username,
                fields.get("firstName"),
                fields.get("lastName"),
                specId,
                fields.getOrDefault("isActive", "true")
        );

        ctx.setLastResponse(
                RestAssured.given()
                        .contentType(ContentType.JSON)
                        .header("X-Password", password)
                        .body(body)
                        .put(ctx.baseUrl() + "/api/trainers")
        );
    }

    @When("I deactivate the trainer")
    public void deactivateTrainer() {
        toggleTrainer(false);
    }

    @When("I activate the trainer")
    public void activateTrainer() {
        toggleTrainer(true);
    }

    @When("I activate the trainer again")
    public void activateTrainerAgain() {
        toggleTrainer(true);
    }

    private void toggleTrainer(boolean active) {
        String username = ctx.getSavedValue("trainerUsername");
        String password = ctx.getSavedValue("trainerPassword");

        String body = String.format(
                "{\"username\":\"%s\",\"isActive\":%s}",
                username, active
        );

        ctx.setLastResponse(
                RestAssured.given()
                        .contentType(ContentType.JSON)
                        .header("X-Password", password)
                        .body(body)
                        .patch(ctx.baseUrl() + "/api/trainers/activate")
        );
    }
}