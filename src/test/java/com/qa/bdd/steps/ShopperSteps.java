package com.qa.bdd.steps;

import com.qa.bdd.context.ScenarioContext;
import com.qa.framework.api.ApiResponse;
import com.qa.framework.api.UserApi;
import com.qa.framework.driver.DriverManager;
import com.qa.framework.models.User;
import com.qa.framework.pages.LoginPage;
import com.qa.framework.utils.TestDataFactory;
import io.cucumber.java.en.Given;

import static org.testng.Assert.assertEquals;

/** Steps that set up who the shopper is. Accounts are seeded through the API for speed. */
public class ShopperSteps {

    private final ScenarioContext context;
    private final UserApi userApi = new UserApi();

    public ShopperSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("a new shopper who is not registered yet")
    public void aNewShopper() {
        context.setShopper(TestDataFactory.newUser());
    }

    @Given("a registered shopper")
    public void aRegisteredShopper() {
        User user = TestDataFactory.newUser();
        ApiResponse response = userApi.createAccount(user);
        assertEquals(response.responseCode(), 201, "Precondition failed, account not created: " + response.raw());
        context.setShopper(user);
    }

    @Given("a registered shopper is logged in")
    public void aRegisteredShopperIsLoggedIn() {
        aRegisteredShopper();
        User user = context.shopper();
        new LoginPage(DriverManager.getDriver()).open().loginAs(user.email(), user.password());
    }
}
