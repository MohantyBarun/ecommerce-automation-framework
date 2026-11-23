package com.qa.framework.api;

import io.qameta.allure.Step;

/** Catalogue endpoints of the automationexercise.com public API. */
public class ProductApi extends BaseApi {

    @Step("API: get all products")
    public ApiResponse getAllProducts() {
        return send(req -> req.get("/productsList"));
    }

    @Step("API: POST to products list (unsupported method)")
    public ApiResponse postToProductsList() {
        return send(req -> req.post("/productsList"));
    }

    @Step("API: get all brands")
    public ApiResponse getAllBrands() {
        return send(req -> req.get("/brandsList"));
    }

    @Step("API: search products for \"{term}\"")
    public ApiResponse searchProduct(String term) {
        return send(req -> req.formParam("search_product", term).post("/searchProduct"));
    }

    @Step("API: search products without a search term")
    public ApiResponse searchProductWithoutTerm() {
        return send(req -> req.post("/searchProduct"));
    }
}
