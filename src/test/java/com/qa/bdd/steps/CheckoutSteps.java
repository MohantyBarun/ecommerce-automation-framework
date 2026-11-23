package com.qa.bdd.steps;

import com.qa.bdd.context.ScenarioContext;
import com.qa.framework.driver.DriverManager;
import com.qa.framework.models.User;
import com.qa.framework.pages.CartPage;
import com.qa.framework.pages.CheckoutPage;
import com.qa.framework.pages.PaymentPage;
import com.qa.framework.utils.TestDataFactory;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class CheckoutSteps {

    private final ScenarioContext context;

    public CheckoutSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("the shopper proceeds to checkout")
    public void proceedToCheckout() {
        new CartPage(DriverManager.getDriver()).proceedToCheckout();
    }

    @Then("the delivery address matches the shopper's account")
    public void deliveryAddressMatches() {
        User user = context.shopper();
        String address = new CheckoutPage(DriverManager.getDriver()).deliveryAddress();
        assertTrue(address.contains(user.firstName() + " " + user.lastName()), "Name missing from: " + address);
        assertTrue(address.contains(user.address1()), "Street missing from: " + address);
        assertTrue(address.contains(user.city()), "City missing from: " + address);
        assertTrue(address.contains(user.country()), "Country missing from: " + address);
    }

    @When("the shopper places the order with the comment {string} and pays by card")
    public void placeOrderAndPay(String comment) {
        new CheckoutPage(DriverManager.getDriver())
                .placeOrder(comment)
                .pay(TestDataFactory.newCard(context.shopper()));
    }

    @Then("the order confirmation {string} is shown")
    public void orderConfirmationShown(String expected) {
        assertEquals(new PaymentPage(DriverManager.getDriver()).orderConfirmation(), expected);
    }
}
