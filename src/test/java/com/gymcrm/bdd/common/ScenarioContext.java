package com.gymcrm.bdd.common;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

@Component
@ScenarioScope
public class ScenarioContext {

    private MvcResult mvcResult;
    private String authToken;
    private String currentUsername;

    public MvcResult getMvcResult() {
        return mvcResult;
    }

    public void setMvcResult(MvcResult mvcResult) {
        this.mvcResult = mvcResult;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
    }

    public void clear() {
        this.mvcResult = null;
        this.authToken = null;
        this.currentUsername = null;
    }
}