package com.qa.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutPage extends BasePage {

    private static final By DELIVERY_ADDRESS = By.id("address_delivery");
    private static final By COMMENT = By.cssSelector("textarea[name='message']");
    private static final By PLACE_ORDER = By.cssSelector("a[href='/payment']");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public String deliveryAddress() {
        return text(DELIVERY_ADDRESS);
    }

    @Step("Add order comment and place order")
    public PaymentPage placeOrder(String comment) {
        type(COMMENT, comment);
        click(PLACE_ORDER);
        return new PaymentPage(driver);
    }
}
