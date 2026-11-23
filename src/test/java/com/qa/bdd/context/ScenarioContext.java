package com.qa.bdd.context;

import com.qa.framework.api.ApiResponse;
import com.qa.framework.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * State shared between the step-definition classes of one scenario.
 * PicoContainer creates a fresh instance per scenario, so parallel scenarios never share it.
 */
public class ScenarioContext {

    private User shopper;
    private ApiResponse lastApiResponse;
    private final List<User> usersToDelete = new ArrayList<>();

    public User shopper() {
        if (shopper == null) {
            throw new IllegalStateException("No shopper in this scenario yet; add a 'Given ... shopper' step");
        }
        return shopper;
    }

    /** Sets the scenario's shopper and schedules the account for API cleanup after the scenario. */
    public void setShopper(User shopper) {
        this.shopper = shopper;
        usersToDelete.add(shopper);
    }

    public ApiResponse lastApiResponse() {
        return lastApiResponse;
    }

    public void setLastApiResponse(ApiResponse response) {
        this.lastApiResponse = response;
    }

    public List<User> usersToDelete() {
        return usersToDelete;
    }
}
