package com.qa.tests.api;

import com.qa.framework.api.ApiResponse;
import com.qa.framework.api.ProductApi;
import com.qa.tests.base.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Epic("API")
@Feature("Product catalogue")
public class ProductApiTest extends BaseTest {

    private final ProductApi productApi = new ProductApi();

    @Test(groups = {"api", "smoke"}, description = "GET productsList returns a non-empty, well-formed catalogue")
    @Severity(SeverityLevel.CRITICAL)
    public void getAllProductsReturnsCatalogue() {
        ApiResponse response = productApi.getAllProducts();

        assertEquals(response.httpStatus(), 200);
        assertEquals(response.responseCode(), 200);
        List<Map<String, Object>> products = response.body().getList("products");
        assertFalse(products.isEmpty(), "Catalogue should not be empty");
        products.forEach(p -> {
            assertTrue(p.containsKey("id") && p.containsKey("name") && p.containsKey("price"), "Malformed product: " + p);
            assertTrue(p.get("price").toString().startsWith("Rs. "), "Unexpected price format: " + p);
        });
    }

    @Test(groups = "api", description = "POST productsList is rejected with 405")
    public void postToProductsListIsNotAllowed() {
        ApiResponse response = productApi.postToProductsList();

        assertEquals(response.responseCode(), 405);
        assertEquals(response.message(), "This request method is not supported.");
    }

    @Test(groups = "api", description = "GET brandsList returns brands")
    public void getAllBrands() {
        ApiResponse response = productApi.getAllBrands();

        assertEquals(response.responseCode(), 200);
        List<String> brands = response.body().getList("brands.brand");
        assertFalse(brands.isEmpty());
        assertTrue(brands.contains("Polo"), "Expected Polo among brands: " + brands);
    }

    @Test(groups = "api", description = "POST searchProduct returns only matching products")
    public void searchProductReturnsMatches() {
        ApiResponse response = productApi.searchProduct("top");

        assertEquals(response.responseCode(), 200);
        List<String> names = response.body().getList("products.name");
        assertFalse(names.isEmpty());
        assertTrue(names.contains("Blue Top"), "Expected 'Blue Top' in " + names);
    }

    @Test(groups = "api", description = "POST searchProduct without the parameter returns 400")
    public void searchProductWithoutParameterIsBadRequest() {
        ApiResponse response = productApi.searchProductWithoutTerm();

        assertEquals(response.responseCode(), 400);
        assertEquals(response.message(), "Bad request, search_product parameter is missing in POST request.");
    }
}
