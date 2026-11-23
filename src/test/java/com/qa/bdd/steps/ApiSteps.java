package com.qa.bdd.steps;

import com.qa.bdd.context.ScenarioContext;
import com.qa.framework.api.ApiResponse;
import com.qa.framework.api.ProductApi;
import com.qa.framework.api.UserApi;
import com.qa.framework.models.User;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ApiSteps {

    private final ScenarioContext context;
    private final UserApi userApi = new UserApi();
    private final ProductApi productApi = new ProductApi();

    public ApiSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("the shopper's account is created through the API")
    public void createAccount() {
        context.setLastApiResponse(userApi.createAccount(context.shopper()));
    }

    @When("the shopper's login is verified through the API")
    public void verifyLogin() {
        User user = context.shopper();
        context.setLastApiResponse(userApi.verifyLogin(user.email(), user.password()));
    }

    @When("the shopper's login is verified through the API with a wrong password")
    public void verifyLoginWrongPassword() {
        User user = context.shopper();
        context.setLastApiResponse(userApi.verifyLogin(user.email(), "wrong-" + user.password()));
    }

    @When("the shopper's account is deleted through the API")
    public void deleteAccount() {
        User user = context.shopper();
        context.setLastApiResponse(userApi.deleteAccount(user.email(), user.password()));
    }

    @When("the full product list is requested")
    public void requestProducts() {
        context.setLastApiResponse(productApi.getAllProducts());
    }

    @When("the product list endpoint is called with POST")
    public void postToProducts() {
        context.setLastApiResponse(productApi.postToProductsList());
    }

    @When("products are searched without a search term")
    public void searchWithoutTerm() {
        context.setLastApiResponse(productApi.searchProductWithoutTerm());
    }

    @Then("the API responds with code {int} and message {string}")
    public void respondsWith(int code, String message) {
        ApiResponse response = context.lastApiResponse();
        assertEquals(response.responseCode(), code, response.raw());
        assertEquals(response.message(), message);
    }

    @Then("the API responds with code {int}")
    public void respondsWithCode(int code) {
        assertEquals(context.lastApiResponse().responseCode(), code, context.lastApiResponse().raw());
    }

    @Then("every product has an id, a name and a price in rupees")
    public void everyProductWellFormed() {
        List<java.util.Map<String, Object>> products = context.lastApiResponse().body().getList("products");
        assertFalse(products.isEmpty(), "Catalogue should not be empty");
        products.forEach(p -> {
            assertTrue(p.containsKey("id") && p.containsKey("name"), "Malformed product: " + p);
            assertTrue(String.valueOf(p.get("price")).startsWith("Rs. "), "Unexpected price: " + p);
        });
    }
}
