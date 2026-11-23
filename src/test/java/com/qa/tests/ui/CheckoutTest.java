package com.qa.tests.ui;

import com.qa.framework.models.User;
import com.qa.framework.pages.CartPage;
import com.qa.framework.pages.CheckoutPage;
import com.qa.framework.pages.LoginPage;
import com.qa.framework.pages.PaymentPage;
import com.qa.framework.pages.ProductsPage;
import com.qa.framework.utils.TestDataFactory;
import com.qa.tests.base.BaseUiTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Epic("UI")
@Feature("Checkout")
public class CheckoutTest extends BaseUiTest {

    @Test(groups = {"ui", "smoke", "e2e"}, description = "Logged-in user buys two products end to end")
    @Severity(SeverityLevel.BLOCKER)
    @Description("User is created through the API, then the whole purchase happens in the browser. "
            + "The account is deleted through the API afterwards.")
    public void placeOrderAsLoggedInUser() {
        User user = createUserViaApi();
        new LoginPage(driver()).open().loginAs(user.email(), user.password());

        CartPage cart = new ProductsPage(driver()).open()
                .addToCartAndContinue(1)
                .addToCartAndViewCart(2);
        assertEquals(cart.productNames(), List.of("Blue Top", "Men Tshirt"));

        CheckoutPage checkout = cart.proceedToCheckout();
        String address = checkout.deliveryAddress();
        assertTrue(address.contains(user.address1()), "Delivery address should contain " + user.address1());
        assertTrue(address.contains(user.city()), "Delivery address should contain " + user.city());

        PaymentPage payment = checkout.placeOrder("Automated order, please ignore")
                .pay(TestDataFactory.newCard(user));

        assertEquals(payment.orderConfirmation(), "ORDER PLACED!");
    }
}
