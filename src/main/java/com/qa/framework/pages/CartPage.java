package com.qa.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class CartPage extends BasePage {

    private static final By CART_ROWS = By.cssSelector("#cart_info_table tbody tr");
    private static final By PRODUCT_NAMES = By.cssSelector("#cart_info_table .cart_description h4 a");
    private static final By EMPTY_CART = By.id("empty_cart");
    private static final By PROCEED_TO_CHECKOUT = By.cssSelector("a.check_out");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open cart")
    public CartPage open() {
        openPath("/view_cart");
        return this;
    }

    public List<String> productNames() {
        return texts(PRODUCT_NAMES);
    }

    public int rowCount() {
        return driver.findElements(CART_ROWS).size();
    }

    public String priceOf(int productId) {
        return text(cell(productId, "cart_price"));
    }

    public int quantityOf(int productId) {
        return Integer.parseInt(text(By.cssSelector("tr#product-" + productId + " .cart_quantity button")));
    }

    public String totalOf(int productId) {
        return text(cell(productId, "cart_total"));
    }

    @Step("Remove product #{productId} from cart")
    public CartPage remove(int productId) {
        By row = By.id("product-" + productId);
        click(By.cssSelector("tr#product-" + productId + " .cart_quantity_delete"));
        wait.until(d -> !isPresent(row));
        return this;
    }

    public boolean isEmpty() {
        return wait.until(d -> isVisible(EMPTY_CART));
    }

    @Step("Proceed to checkout")
    public CheckoutPage proceedToCheckout() {
        click(PROCEED_TO_CHECKOUT);
        return new CheckoutPage(driver);
    }

    private static By cell(int productId, String cssClass) {
        return By.cssSelector("tr#product-" + productId + " ." + cssClass + " p");
    }
}
