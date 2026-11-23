package com.qa.tests.data;

import com.qa.framework.utils.JsonDataReader;
import org.testng.annotations.DataProvider;

/** All JSON-backed DataProviders in one place. parallel = true lets rows run concurrently. */
public final class TestData {

    private TestData() {
    }

    @DataProvider(name = "invalidLogins", parallel = true)
    public static Object[][] invalidLogins() {
        return JsonDataReader.toDataProvider("testdata/invalid-logins.json", LoginCase.class);
    }

    @DataProvider(name = "cartProducts", parallel = true)
    public static Object[][] cartProducts() {
        return JsonDataReader.toDataProvider("testdata/cart-products.json", CartProduct.class);
    }

    @DataProvider(name = "searchTerms", parallel = true)
    public static Object[][] searchTerms() {
        return JsonDataReader.toDataProvider("testdata/search-terms.json", String.class);
    }
}
