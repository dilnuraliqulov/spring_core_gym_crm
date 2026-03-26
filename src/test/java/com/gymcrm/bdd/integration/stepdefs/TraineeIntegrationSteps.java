package com.gymcrm.bdd.integration.stepdefs;

import com.gymcrm.bdd.integration.config.IntegrationTestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import java.util.Map;

public class TraineeIntegrationSteps {

    private final IntegrationTestContext ctx;

    public TraineeIntegrationSteps(IntegrationTestContext ctx) {
        this.ctx = ctx;
    }

    @When("I call POST {string} with body:")
    public void callPost(String path, String body) {
        ctx.setLastResponse(
                RestAssured.given()
                        .contentType(ContentType.JSON)
                        .body(body)
                        .post(ctx.baseUrl() + path)
        );
    }

    @When("I call GET {string} with saved credentials")
    public void callGetWithSavedCredentials(String path) {
        String username = ctx.getSavedValue("traineeUsername");
        String password = ctx.getSavedValue("traineePassword");
        String resolvedPath = path.replace("{traineeUsername}", username);

        ctx.setLastResponse(
                RestAssured.given()
                        .header("X-Password", password)
                        .get(ctx.baseUrl() + resolvedPath)
        );
    }

    @When("I call GET {string} with credentials {string} and {string}")
    public void callGetWithCredentials(String path, String username, String password) {
        ctx.setLastResponse(
                RestAssured.given()
                        .header("X-Password", password)
                        .get(ctx.baseUrl() + path)
        );
    }

    @Given("a registered trainee with firstName {string} and lastName {string}")
    public void registerTrainee(String firstName, String lastName) {
        String body = String.format(
                "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"dateOfBirth\":\"1990-01-01\"}",
                firstName, lastName
        );

        ctx.setLastResponse(
                RestAssured.given()
                        .contentType(ContentType.JSON)
                        .body(body)
                        .post(ctx.baseUrl() + "/api/trainees")
        );

        ctx.putSavedValue(
                "traineeUsername",
                ctx.getLastResponse().jsonPath().getString("username")
        );
        ctx.putSavedValue(
                "traineePassword",
                ctx.getLastResponse().jsonPath().getString("password")
        );
    }

    @When("I update the trainee profile with:")
    public void updateTraineeProfile(DataTable dataTable) {
        Map<String, String> fields = dataTable.asMap(String.class, String.class);
        String username = ctx.getSavedValue("traineeUsername");
        String password = ctx.getSavedValue("traineePassword");

        String body = String.format(
                "{\"username\":\"%s\",\"firstName\":\"%s\",\"lastName\":\"%s\",\"isActive\":%s}",
                username,
                fields.get("firstName"),
                fields.get("lastName"),
                fields.get("isActive")
        );

        ctx.setLastResponse(
                RestAssured.given()
                        .contentType(ContentType.JSON)
                        .header("X-Password", password)
                        .body(body)
                        .put(ctx.baseUrl() + "/api/trainees")
        );
    }

    @When("I delete the trainee profile")
    public void deleteTrainee() {
        String username = ctx.getSavedValue("traineeUsername");
        String password = ctx.getSavedValue("traineePassword");

        ctx.setLastResponse(
                RestAssured.given()
                        .header("X-Password", password)
                        .delete(ctx.baseUrl() + "/api/trainees/" + username)
        );

        ctx.putSavedValue("deletedUsername", username);
        ctx.putSavedValue("deletedPassword", password);
    }

    @When("I call GET with the deleted trainee credentials")
    public void getDeletedTrainee() {
        String username = ctx.getSavedValue("deletedUsername");
        String password = ctx.getSavedValue("deletedPassword");

        ctx.setLastResponse(
                RestAssured.given()
                        .header("X-Password", password)
                        .get(ctx.baseUrl() + "/api/trainees/" + username)
        );
    }

    @When("I deactivate the trainee")
    public void deactivateTrainee() {
        toggleTrainee(false);
    }

    @When("I activate the trainee")
    public void activateTrainee() {
        toggleTrainee(true);
    }

    @When("I activate the trainee again")
    public void activateTraineeAgain() {
        toggleTrainee(true);
    }

    private void toggleTrainee(boolean active) {
        String username = ctx.getSavedValue("traineeUsername");
        String password = ctx.getSavedValue("traineePassword");

        String body = String.format(
                "{\"username\":\"%s\",\"isActive\":%s}",
                username,
                active
        );

        ctx.setLastResponse(
                RestAssured.given()
                        .contentType(ContentType.JSON)
                        .header("X-Password", password)
                        .body(body)
                        .patch(ctx.baseUrl() + "/api/trainees/activate")
        );
    }
}