package com.qa.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Landing page plus the header navigation that is shared by every page. */
public class HomePage extends BasePage {

    private static final By SLIDER = By.id("slider");
    private static final By NAV_LOGIN = By.cssSelector("a[href='/login']");
    private static final By NAV_PRODUCTS = By.cssSelector("a[href='/products']");
    private static final By NAV_CART = By.cssSelector(".shop-menu a[href='/view_cart']");
    private static final By NAV_LOGOUT = By.cssSelector("a[href='/logout']");
    private static final By NAV_DELETE_ACCOUNT = By.cssSelector("a[href='/delete_account']");
    private static final By LOGGED_IN_AS = By.xpath("//a[contains(., 'Logged in as')]/b");
    private static final By ACCOUNT_DELETED = By.cssSelector("[data-qa='account-deleted']");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @Step("Open home page")
    public HomePage open() {
        openPath("/");
        visible(SLIDER);
        return this;
    }

    @Step("Go to Signup / Login")
    public LoginPage goToLogin() {
        click(NAV_LOGIN);
        return new LoginPage(driver);
    }

    @Step("Go to Products")
    public ProductsPage goToProducts() {
        click(NAV_PRODUCTS);
        return new ProductsPage(driver);
    }

    @Step("Go to Cart")
    public CartPage goToCart() {
        click(NAV_CART);
        return new CartPage(driver);
    }

    public boolean isLoggedIn() {
        return isVisible(LOGGED_IN_AS);
    }

    public String loggedInUsername() {
        return text(LOGGED_IN_AS);
    }

    @Step("Log out")
    public LoginPage logout() {
        click(NAV_LOGOUT);
        return new LoginPage(driver);
    }

    @Step("Delete account from the UI")
    public boolean deleteAccount() {
        click(NAV_DELETE_ACCOUNT);
        return visible(ACCOUNT_DELETED).isDisplayed();
    }
}
