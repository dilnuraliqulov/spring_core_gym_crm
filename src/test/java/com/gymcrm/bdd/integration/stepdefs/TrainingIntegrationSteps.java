package com.gymcrm.bdd.integration.stepdefs;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.gymcrm.bdd.integration.config.IntegrationTestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

public class TrainingIntegrationSteps {

    private final IntegrationTestContext ctx;
    private final WireMockServer wireMockServer;

    public TrainingIntegrationSteps(
            IntegrationTestContext ctx,
            WireMockServer wireMockServer
    ) {
        this.ctx = ctx;
        this.wireMockServer = wireMockServer;
    }

    @When("I create a training with:")
    public void createTraining(DataTable dataTable) {
        Map<String, String> fields = dataTable.asMap(String.class, String.class);

        String traineeUsername = ctx.getSavedValue("traineeUsername");
        String traineePassword = ctx.getSavedValue("traineePassword");
        String trainerUsername = ctx.getSavedValue("trainerUsername");

        String body = String.format("""
            {
              "traineeUsername": "%s",
              "trainerUsername": "%s",
              "trainingName": "%s",
              "trainingDate": "%s",
              "duration": %s
            }""",
                traineeUsername,
                trainerUsername,
                fields.get("trainingName"),
                fields.get("trainingDate"),
                fields.get("duration")
        );

        ctx.setLastResponse(
                RestAssured.given()
                        .contentType(ContentType.JSON)
                        .header("X-Username", traineeUsername)
                        .header("X-Password", traineePassword)
                        .body(body)
                        .post(ctx.baseUrl() + "/api/trainings")
        );
    }

    @Then("the Training Workload Service should have received a workload update request")
    public void verifyWorkloadServiceCalled() {
        wireMockServer.verify(postRequestedFor(urlPathMatching("/api/workload.*")));
    }

    @When("I request trainings for the trainee")
    public void getTraineeTrainings() {
        String username = ctx.getSavedValue("traineeUsername");
        String password = ctx.getSavedValue("traineePassword");

        ctx.setLastResponse(
                RestAssured.given()
                        .header("X-Password", password)
                        .get(ctx.baseUrl() + "/api/trainees/" + username + "/trainings")
        );
    }

    @Given("the trainee is assigned to the trainer")
    public void assignTraineeToTrainer() {
        String traineeUsername = ctx.getSavedValue("traineeUsername");
        String trainerUsername = ctx.getSavedValue("trainerUsername");
        String password = ctx.getSavedValue("traineePassword");

        String body = String.format(
                "{\"traineeUsername\":\"%s\",\"trainerUsernames\":[\"%s\"]}",
                traineeUsername, trainerUsername
        );

        ctx.setLastResponse(
                RestAssured.given()
                        .contentType(ContentType.JSON)
                        .header("X-Password", password)
                        .body(body)
                        .put(ctx.baseUrl() + "/api/trainees/trainers")
        );
    }

    @Given("a training exists for the trainee")
    public void createExistingTraining() {
        String traineeUsername = ctx.getSavedValue("traineeUsername");
        String traineePassword = ctx.getSavedValue("traineePassword");
        String trainerUsername = ctx.getSavedValue("trainerUsername");

        String body = String.format("""
            {
              "traineeUsername": "%s",
              "trainerUsername": "%s",
              "trainingName": "Test Training",
              "trainingDate": "2026-04-01",
              "duration": 45
            }""",
                traineeUsername,
                trainerUsername
        );

        ctx.setLastResponse(
                RestAssured.given()
                        .contentType(ContentType.JSON)
                        .header("X-Username", traineeUsername)
                        .header("X-Password", traineePassword)
                        .body(body)
                        .post(ctx.baseUrl() + "/api/trainings")
        );
    }

    @When("I call POST {string} with invalid trainer and credentials")
    public void callPostWithInvalidTrainer(String path) {
        String traineeUsername = ctx.getSavedValue("traineeUsername");
        String traineePassword = ctx.getSavedValue("traineePassword");

        String body = String.format("""
            {
              "traineeUsername": "%s",
              "trainerUsername": "ghost.trainer",
              "trainingName": "Bad Training",
              "trainingDate": "2026-04-01",
              "duration": 30
            }""",
                traineeUsername
        );

        ctx.setLastResponse(
                RestAssured.given()
                        .contentType(ContentType.JSON)
                        .header("X-Username", traineeUsername)
                        .header("X-Password", traineePassword)
                        .body(body)
                        .post(ctx.baseUrl() + "/api/trainings")
        );
    }
}