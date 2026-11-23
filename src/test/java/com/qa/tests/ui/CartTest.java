package com.qa.tests.ui;

import com.qa.framework.pages.CartPage;
import com.qa.framework.pages.ProductDetailsPage;
import com.qa.framework.pages.ProductsPage;
import com.qa.tests.base.BaseUiTest;
import com.qa.tests.data.CartProduct;
import com.qa.tests.data.TestData;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Epic("UI")
@Feature("Shopping cart")
public class CartTest extends BaseUiTest {

    @Test(groups = {"ui", "smoke"}, dataProvider = "cartProducts", dataProviderClass = TestData.class,
            description = "A product added from the catalogue shows the right name, price and total in the cart")
    public void addedProductAppearsInCart(CartProduct product) {
        CartPage cart = new ProductsPage(driver()).open().addToCartAndViewCart(product.productId());

        assertEquals(cart.productNames(), List.of(product.name()));
        assertEquals(cart.priceOf(product.productId()), product.price());
        assertEquals(cart.quantityOf(product.productId()), 1);
        assertEquals(cart.totalOf(product.productId()), product.price());
    }

    @Test(groups = "ui", description = "Quantity chosen on the product page is carried into the cart and total")
    public void quantityFromProductDetailsIsApplied() {
        ProductDetailsPage details = new ProductDetailsPage(driver()).open(1);
        String name = details.name();
        int unitPrice = rupees(details.price());

        CartPage cart = details.addToCartAndViewCart(4);

        assertEquals(cart.productNames(), List.of(name));
        assertEquals(cart.quantityOf(1), 4);
        assertEquals(rupees(cart.totalOf(1)), unitPrice * 4);
    }

    @Test(groups = "ui", description = "Removing the only item empties the cart")
    public void removingProductEmptiesCart() {
        CartPage cart = new ProductsPage(driver()).open()
                .addToCartAndContinue(1)
                .addToCartAndViewCart(2);
        assertEquals(cart.rowCount(), 2);

        cart.remove(1).remove(2);

        assertTrue(cart.isEmpty(), "Cart should show the empty-cart message");
    }

    private static int rupees(String price) {
        return Integer.parseInt(price.replaceAll("[^0-9]", ""));
    }
}
