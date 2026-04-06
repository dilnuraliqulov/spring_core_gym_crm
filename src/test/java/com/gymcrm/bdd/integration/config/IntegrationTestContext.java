package com.gymcrm.bdd.integration.config;

import io.cucumber.spring.ScenarioScope;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ScenarioScope
public class IntegrationTestContext {

    @Value("${server.port:8081}")
    private int port;

    private Response lastResponse;
    private final Map<String, String> savedValues = new HashMap<>();

    public void reset() {
        savedValues.clear();
        lastResponse = null;
    }

    public String baseUrl() {
        return "http://localhost:" + port;
    }

    public Response getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(Response lastResponse) {
        this.lastResponse = lastResponse;
    }

    public void putSavedValue(String key, String value) {
        savedValues.put(key, value);
    }

    public String getSavedValue(String key) {
        return savedValues.get(key);
    }

    public boolean hasSavedValue(String key) {
        return savedValues.containsKey(key);
    }
}