package com.qa.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class ProductsPage extends BasePage {

    private static final By SEARCH_INPUT = By.id("search_product");
    private static final By SEARCH_BUTTON = By.id("submit_search");
    private static final By SECTION_TITLE = By.cssSelector(".features_items h2.title");
    private static final By PRODUCT_NAMES = By.cssSelector(".features_items .productinfo p");
    private static final By MODAL_CONTINUE = By.cssSelector("#cartModal .close-modal");
    private static final By MODAL_VIEW_CART = By.cssSelector("#cartModal a[href='/view_cart']");

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

    private void clickAddToCart(int productId) {
        // Hovering a product card slides .product-overlay over this button. In Firefox the native
        // click lands on the overlay and is silently lost, so dispatch the click on the button itself.
        jsClick(By.cssSelector(".productinfo a.add-to-cart[data-product-id='" + productId + "']"));
        visible(MODAL_CONTINUE);
    }
}
