package com.qa.framework.pages;

import com.qa.framework.models.User;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** "Enter Account Information" form plus the "Account Created!" confirmation. */
public class SignupPage extends BasePage {

    private static final By TITLE_MR = By.id("id_gender1");
    private static final By TITLE_MRS = By.id("id_gender2");
    private static final By PASSWORD = By.cssSelector("[data-qa='password']");
    private static final By DAY = By.cssSelector("[data-qa='days']");
    private static final By MONTH = By.cssSelector("[data-qa='months']");
    private static final By YEAR = By.cssSelector("[data-qa='years']");
    private static final By NEWSLETTER = By.id("newsletter");
    private static final By FIRST_NAME = By.cssSelector("[data-qa='first_name']");
    private static final By LAST_NAME = By.cssSelector("[data-qa='last_name']");
    private static final By COMPANY = By.cssSelector("[data-qa='company']");
    private static final By ADDRESS1 = By.cssSelector("[data-qa='address']");
    private static final By ADDRESS2 = By.cssSelector("[data-qa='address2']");
    private static final By COUNTRY = By.cssSelector("[data-qa='country']");
    private static final By STATE = By.cssSelector("[data-qa='state']");
    private static final By CITY = By.cssSelector("[data-qa='city']");
    private static final By ZIPCODE = By.cssSelector("[data-qa='zipcode']");
    private static final By MOBILE = By.cssSelector("[data-qa='mobile_number']");
    private static final By CREATE_ACCOUNT = By.cssSelector("[data-qa='create-account']");

    private static final By ACCOUNT_CREATED = By.cssSelector("[data-qa='account-created']");
    private static final By CONTINUE = By.cssSelector("[data-qa='continue-button']");

    public SignupPage(WebDriver driver) {
        super(driver);
    }

    @Step("Fill account information and create account")
    public SignupPage createAccount(User user) {
        click("Mrs".equals(user.title()) ? TITLE_MRS : TITLE_MR);
        type(PASSWORD, user.password());
        selectByValue(DAY, user.birthDay());
        selectByValue(MONTH, user.birthMonth());
        selectByValue(YEAR, user.birthYear());
        click(NEWSLETTER);
        type(FIRST_NAME, user.firstName());
        type(LAST_NAME, user.lastName());
        type(COMPANY, user.company());
        type(ADDRESS1, user.address1());
        type(ADDRESS2, user.address2());
        selectByText(COUNTRY, user.country());
        type(STATE, user.state());
        type(CITY, user.city());
        type(ZIPCODE, user.zipcode());
        type(MOBILE, user.mobileNumber());
        click(CREATE_ACCOUNT);
        return this;
    }

    public String accountCreatedMessage() {
        return text(ACCOUNT_CREATED);
    }

    @Step("Continue to home page")
    public HomePage continueToHome() {
        click(CONTINUE);
        return new HomePage(driver);
    }
}
