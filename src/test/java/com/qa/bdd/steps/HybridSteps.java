package com.qa.bdd.steps;

import com.qa.bdd.context.ScenarioContext;
import com.qa.framework.api.ApiResponse;
import com.qa.framework.api.ProductApi;
import com.qa.framework.api.UserApi;
import com.qa.framework.driver.DriverManager;
import com.qa.framework.models.User;
import com.qa.framework.pages.LoginPage;
import com.qa.framework.pages.ProductsPage;
import com.qa.framework.pages.SignupPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.testng.Assert.assertEquals;

/** Steps that cross the UI/API boundary. */
public class HybridSteps {

    private final ScenarioContext context;
    private final ProductApi productApi = new ProductApi();
    private final UserApi userApi = new UserApi();
    private SignupPage signupPage;

    public HybridSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("the shopper searches the catalogue for {string}")
    public void searchFor(String term) {
        new ProductsPage(DriverManager.getDriver()).search(term);
    }

    @Then("the results section is titled {string}")
    public void resultsTitled(String title) {
        assertEquals(new ProductsPage(DriverManager.getDriver()).sectionTitle(), title);
    }

    @Then("the products shown match the search API results for {string}")
    public void resultsMatchApi(String term) {
        List<String> apiNames = productApi.searchProduct(term).body().getList("products.name");
        List<String> uiNames = new ProductsPage(DriverManager.getDriver()).productNames();
        assertEquals(uiNames, apiNames, "UI and API disagree for '" + term + "'");
    }

    @When("the shopper registers through the signup form")
    public void registerThroughUi() {
        User user = context.shopper();
        signupPage = new LoginPage(DriverManager.getDriver()).open()
                .startSignup(user.name(), user.email())
                .createAccount(user);
    }

    @Then("the confirmation {string} is shown and the shopper is logged in")
    public void accountCreatedAndLoggedIn(String message) {
        assertEquals(signupPage.accountCreatedMessage(), message);
        assertEquals(signupPage.continueToHome().loggedInUsername(), context.shopper().name());
    }

    @Then("the account details returned by the API match what the shopper entered")
    public void apiDetailsMatch() {
        User user = context.shopper();
        ApiResponse details = userApi.getUserDetailByEmail(user.email());
        assertEquals(details.responseCode(), 200, details.raw());
        assertEquals(details.body().getString("user.name"), user.name());
        assertEquals(details.body().getString("user.first_name"), user.firstName());
        assertEquals(details.body().getString("user.last_name"), user.lastName());
        assertEquals(details.body().getString("user.country"), user.country());
        assertEquals(details.body().getString("user.city"), user.city());
    }
}
