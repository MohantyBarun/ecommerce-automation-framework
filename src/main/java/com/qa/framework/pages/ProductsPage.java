package com.qa.framework.pages;

import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ProductsPage extends BasePage {

    private static final By SEARCH_INPUT = By.id("search_product");
    private static final By SEARCH_BUTTON = By.id("submit_search");
    private static final By SECTION_TITLE = By.cssSelector(".features_items h2.title");
    private static final By PRODUCT_NAMES = By.cssSelector(".features_items .productinfo p");
    private static final By MODAL_CONTINUE = By.cssSelector("#cartModal .close-modal");
    private static final By MODAL_VIEW_CART = By.cssSelector("#cartModal a[href='/view_cart']");
    private static final int ADD_TO_CART_ATTEMPTS = 3;
    private static final Logger LOG = LogManager.getLogger(ProductsPage.class);

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open products page")
    public ProductsPage open() {
        openPath("/products");
        visible(SEARCH_INPUT);
        return this;
    }

    @Step("Search products for \"{term}\"")
    public ProductsPage search(String term) {
        type(SEARCH_INPUT, term);
        click(SEARCH_BUTTON);
        return this;
    }

    public String sectionTitle() {
        return text(SECTION_TITLE);
    }

    public List<String> productNames() {
        return texts(PRODUCT_NAMES);
    }

    @Step("Add product #{productId} to cart and keep shopping")
    public ProductsPage addToCartAndContinue(int productId) {
        clickAddToCart(productId);
        click(MODAL_CONTINUE);
        wait.until(d -> !isVisible(MODAL_CONTINUE));
        return this;
    }

    @Step("Add product #{productId} to cart and open the cart")
    public CartPage addToCartAndViewCart(int productId) {
        clickAddToCart(productId);
        click(MODAL_VIEW_CART);
        return new CartPage(driver);
    }

    @Step("Open details of product #{productId}")
    public ProductDetailsPage viewProduct(int productId) {
        click(By.cssSelector("a[href='/product_details/" + productId + "']"));
        return new ProductDetailsPage(driver);
    }

    /**
     * Hovering a product card slides .product-overlay over this button. In Firefox the native
     * click lands on the overlay and is silently lost, so the click is dispatched on the button itself.
     * <p>
     * The confirmation modal is opened only by the success callback of the add-to-cart AJAX call.
     * If it does not appear, the request failed (typically the site's overload response) and nothing
     * was added, so clicking again cannot create a duplicate cart line.
     */
    private void clickAddToCart(int productId) {
        By button = By.cssSelector(".productinfo a.add-to-cart[data-product-id='" + productId + "']");
        WebDriverWait modalWait = new WebDriverWait(driver, Duration.ofSeconds(8));
        for (int attempt = 1; attempt <= ADD_TO_CART_ATTEMPTS; attempt++) {
            jsClick(button);
            try {
                modalWait.until(ExpectedConditions.visibilityOfElementLocated(MODAL_CONTINUE));
                return;
            } catch (TimeoutException e) {
                LOG.warn("Cart modal did not open for product #{} (attempt {}/{})", productId, attempt,
                        ADD_TO_CART_ATTEMPTS);
            }
        }
        throw new IllegalStateException("Could not add product #" + productId + " to cart after "
                + ADD_TO_CART_ATTEMPTS + " attempts");
    }
}
