package com.qa.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By LOGIN_EMAIL = By.cssSelector("input[data-qa='login-email']");
    private static final By LOGIN_PASSWORD = By.cssSelector("input[data-qa='login-password']");
    private static final By LOGIN_BUTTON = By.cssSelector("button[data-qa='login-button']");
    private static final By LOGIN_ERROR = By.cssSelector("form[action='/login'] p");

    private static final By SIGNUP_NAME = By.cssSelector("input[data-qa='signup-name']");
    private static final By SIGNUP_EMAIL = By.cssSelector("input[data-qa='signup-email']");
    private static final By SIGNUP_BUTTON = By.cssSelector("button[data-qa='signup-button']");
    private static final By SIGNUP_ERROR = By.cssSelector("form[action='/signup'] p");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open login page")
    public LoginPage open() {
        openPath("/login");
        visible(LOGIN_EMAIL);
        return this;
    }

    @Step("Log in as {email}")
    public HomePage loginAs(String email, String password) {
        submitLogin(email, password);
        return new HomePage(driver);
    }

    @Step("Attempt login with {email} (expecting failure)")
    public LoginPage attemptInvalidLogin(String email, String password) {
        submitLogin(email, password);
        return this;
    }

    public String loginError() {
        return text(LOGIN_ERROR);
    }

    /** True while the browser is still on the login form (HTML5 validation blocked submit). */
    public boolean isOnLoginPage() {
        return driver.getCurrentUrl().contains("/login") && isVisible(LOGIN_EMAIL);
    }

    @Step("Start signup for {name} / {email}")
    public SignupPage startSignup(String name, String email) {
        type(SIGNUP_NAME, name);
        type(SIGNUP_EMAIL, email);
        click(SIGNUP_BUTTON);
        return new SignupPage(driver);
    }

    @Step("Start signup with an already registered email {email}")
    public LoginPage attemptSignupWithExistingEmail(String name, String email) {
        type(SIGNUP_NAME, name);
        type(SIGNUP_EMAIL, email);
        click(SIGNUP_BUTTON);
        return this;
    }

    public String signupError() {
        return text(SIGNUP_ERROR);
    }

    private void submitLogin(String email, String password) {
        type(LOGIN_EMAIL, email);
        type(LOGIN_PASSWORD, password);
        click(LOGIN_BUTTON);
    }
}
