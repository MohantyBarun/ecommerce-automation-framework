package com.qa.tests.data;

/** Row of testdata/cart-products.json. */
public record CartProduct(int productId, String name, String price) {

    @Override
    public String toString() {
        return name + " (#" + productId + ")";
    }
}
