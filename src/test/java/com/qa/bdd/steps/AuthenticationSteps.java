package com.qa.bdd.steps;

import com.qa.bdd.context.ScenarioContext;
import com.qa.framework.driver.DriverManager;
import com.qa.framework.models.User;
import com.qa.framework.pages.HomePage;
import com.qa.framework.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class AuthenticationSteps {

    private final ScenarioContext context;

    public AuthenticationSteps(ScenarioContext context) {
        this.context = context;
    }

    private LoginPage loginPage() {
        return new LoginPage(DriverManager.getDriver());
    }

    private HomePage homePage() {
        return new HomePage(DriverManager.getDriver());
    }

    @Given("the shopper is on the login page")
    public void onLoginPage() {
        loginPage().open();
    }

    @When("the shopper logs in with their own credentials")
    public void logInWithOwnCredentials() {
        User user = context.shopper();
        loginPage().open().loginAs(user.email(), user.password());
    }

    @When("the shopper logs in with email {string} and password {string}")
    public void logInWith(String email, String password) {
        loginPage().attemptInvalidLogin(email, password);
    }

    @Then("the shopper is logged in under their own name")
    public void loggedInUnderOwnName() {
        assertTrue(homePage().isLoggedIn(), "Shopper should be logged in");
        assertEquals(homePage().loggedInUsername(), context.shopper().name());
    }

    @When("the shopper logs out")
    public void logOut() {
        homePage().logout();
    }

    @Then("the shopper is back on the login page")
    public void backOnLoginPage() {
        assertTrue(loginPage().isOnLoginPage(), "Should be on the login page");
        assertFalse(homePage().isLoggedIn(), "Should no longer be logged in");
    }

    @Then("the login error {string} is shown")
    public void loginErrorShown(String expected) {
        assertEquals(loginPage().loginError(), expected);
        assertFalse(homePage().isLoggedIn());
    }

    @Then("the browser keeps the shopper on the login form")
    public void browserValidationBlocksSubmit() {
        assertTrue(loginPage().isOnLoginPage(), "Browser validation should keep the shopper on the form");
        assertFalse(homePage().isLoggedIn());
    }

    @When("someone tries to sign up as {string} with the shopper's email")
    public void signUpWithExistingEmail(String name) {
        loginPage().open().attemptSignupWithExistingEmail(name, context.shopper().email());
    }

    @Then("the signup error {string} is shown")
    public void signupErrorShown(String expected) {
        assertEquals(loginPage().signupError(), expected);
    }
}
