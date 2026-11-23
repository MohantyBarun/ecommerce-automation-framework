package com.qa.framework.pages;

import com.qa.framework.models.PaymentCard;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Payment form plus the "Order Placed!" confirmation. */
public class PaymentPage extends BasePage {

    private static final By NAME_ON_CARD = By.cssSelector("[data-qa='name-on-card']");
    private static final By CARD_NUMBER = By.cssSelector("[data-qa='card-number']");
    private static final By CVC = By.cssSelector("[data-qa='cvc']");
    private static final By EXPIRY_MONTH = By.cssSelector("[data-qa='expiry-month']");
    private static final By EXPIRY_YEAR = By.cssSelector("[data-qa='expiry-year']");
    private static final By PAY = By.cssSelector("[data-qa='pay-button']");
    private static final By ORDER_PLACED = By.cssSelector("[data-qa='order-placed']");

    public PaymentPage(WebDriver driver) {
        super(driver);
    }

    @Step("Pay with card ending {card.number}")
    public PaymentPage pay(PaymentCard card) {
        type(NAME_ON_CARD, card.nameOnCard());
        type(CARD_NUMBER, card.number());
        type(CVC, card.cvc());
        type(EXPIRY_MONTH, card.expiryMonth());
        type(EXPIRY_YEAR, card.expiryYear());
        click(PAY);
        return this;
    }

    public String orderConfirmation() {
        return text(ORDER_PLACED);
    }
}
