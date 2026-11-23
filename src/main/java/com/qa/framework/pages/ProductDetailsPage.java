package com.qa.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductDetailsPage extends BasePage {

    private static final By NAME = By.cssSelector(".product-information h2");
    private static final By PRICE = By.cssSelector(".product-information span > span");
    private static final By QUANTITY = By.id("quantity");
    private static final By ADD_TO_CART = By.cssSelector(".product-information button.cart");
    private static final By MODAL_VIEW_CART = By.cssSelector("#cartModal a[href='/view_cart']");

    public ProductDetailsPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open product details for #{productId}")
    public ProductDetailsPage open(int productId) {
        openPath("/product_details/" + productId);
        visible(NAME);
        return this;
    }

    public String name() {
        return text(NAME);
    }

    public String price() {
        return text(PRICE);
    }

    @Step("Add {quantity} item(s) to cart and open the cart")
    public CartPage addToCartAndViewCart(int quantity) {
        type(QUANTITY, String.valueOf(quantity));
        click(ADD_TO_CART);
        click(MODAL_VIEW_CART);
        return new CartPage(driver);
    }
}
