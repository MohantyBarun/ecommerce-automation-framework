package com.qa.bdd.steps;

import com.qa.framework.driver.DriverManager;
import com.qa.framework.pages.CartPage;
import com.qa.framework.pages.ProductDetailsPage;
import com.qa.framework.pages.ProductsPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/** Catalogue and cart steps. Also used by the checkout feature. */
public class CartSteps {

    private int unitPrice;
    private List<Integer> productsInCart = List.of();

    private ProductsPage productsPage() {
        return new ProductsPage(DriverManager.getDriver());
    }

    private CartPage cartPage() {
        return new CartPage(DriverManager.getDriver());
    }

    @Given("the shopper is browsing the product catalogue")
    public void browsingCatalogue() {
        productsPage().open();
    }

    @When("the shopper adds product {int} to the cart and opens the cart")
    public void addProductAndOpenCart(int productId) {
        productsPage().addToCartAndViewCart(productId);
    }

    @Then("the cart contains only {string} priced {string} with quantity {int}")
    public void cartContainsOnly(String name, String price, int quantity) {
        CartPage cart = cartPage();
        assertEquals(cart.productNames(), List.of(name));
        int id = productIdOf(cart);
        assertEquals(cart.priceOf(id), price);
        assertEquals(cart.quantityOf(id), quantity);
    }

    @Given("the shopper is viewing product {int}")
    public void viewingProduct(int productId) {
        ProductDetailsPage details = new ProductDetailsPage(DriverManager.getDriver()).open(productId);
        unitPrice = rupees(details.price());
        productsInCart = List.of(productId);
    }

    @When("the shopper adds {int} of it to the cart")
    public void addQuantity(int quantity) {
        new ProductDetailsPage(DriverManager.getDriver()).addToCartAndViewCart(quantity);
    }

    @Then("the cart shows quantity {int} and a total of {int} times the unit price")
    public void cartShowsQuantityAndTotal(int quantity, int multiplier) {
        int id = productsInCart.get(0);
        assertEquals(cartPage().quantityOf(id), quantity);
        assertEquals(rupees(cartPage().totalOf(id)), unitPrice * multiplier);
    }

    @Given("the shopper has these products in the cart:")
    public void hasProductsInCart(List<Integer> productIds) {
        ProductsPage page = productsPage().open();
        for (int i = 0; i < productIds.size() - 1; i++) {
            page.addToCartAndContinue(productIds.get(i));
        }
        page.addToCartAndViewCart(productIds.get(productIds.size() - 1));
        productsInCart = productIds;
        assertEquals(cartPage().rowCount(), productIds.size());
    }

    @Then("the cart lists the products:")
    public void cartListsProducts(List<String> names) {
        assertEquals(cartPage().productNames(), names);
    }

    @When("the shopper removes every product from the cart")
    public void removeEverything() {
        CartPage cart = cartPage();
        productsInCart.forEach(cart::remove);
    }

    @Then("the cart is empty")
    public void cartIsEmpty() {
        assertTrue(cartPage().isEmpty(), "Cart should show the empty-cart message");
    }

    private static int productIdOf(CartPage cart) {
        List<Integer> ids = cart.productIds();
        assertEquals(ids.size(), 1, "Expected exactly one product line in the cart");
        return ids.get(0);
    }

    private static int rupees(String price) {
        return Integer.parseInt(price.replaceAll("[^0-9]", ""));
    }
}
