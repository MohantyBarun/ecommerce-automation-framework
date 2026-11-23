package com.qa.tests.hybrid;

import com.qa.framework.api.ApiResponse;
import com.qa.framework.api.ProductApi;
import com.qa.framework.models.User;
import com.qa.framework.pages.HomePage;
import com.qa.framework.pages.LoginPage;
import com.qa.framework.pages.ProductsPage;
import com.qa.framework.pages.SignupPage;
import com.qa.framework.utils.TestDataFactory;
import com.qa.tests.base.BaseUiTest;
import com.qa.tests.data.TestData;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/** Tests that cross the API/UI boundary in both directions. */
@Epic("Hybrid UI + API")
@Feature("Data consistency")
public class ApiUiConsistencyTest extends BaseUiTest {

    private final ProductApi productApi = new ProductApi();

    @Test(groups = {"hybrid", "smoke"}, description = "Account created via API can log in via UI")
    @Severity(SeverityLevel.CRITICAL)
    @Description("API -> UI: seed a user with createAccount, then prove the web app recognises them.")
    public void apiCreatedUserCanLogInThroughUi() {
        User user = createUserViaApi();

        HomePage home = new LoginPage(driver()).open().loginAs(user.email(), user.password());

        assertTrue(home.isLoggedIn());
        assertEquals(home.loggedInUsername(), user.name());
    }

    @Test(groups = "hybrid", description = "Account registered via UI is stored correctly (verified via API)")
    @Severity(SeverityLevel.CRITICAL)
    @Description("UI -> API: register through the signup form, then read the account back from the API.")
    public void uiRegisteredUserIsPersisted() {
        User user = TestDataFactory.newUser();
        registerForCleanup(user);

        SignupPage signup = new LoginPage(driver()).open()
                .startSignup(user.name(), user.email())
                .createAccount(user);
        assertEquals(signup.accountCreatedMessage(), "ACCOUNT CREATED!");
        assertEquals(signup.continueToHome().loggedInUsername(), user.name());

        ApiResponse details = userApi.getUserDetailByEmail(user.email());
        assertEquals(details.responseCode(), 200, details.raw());
        assertEquals(details.body().getString("user.name"), user.name());
        assertEquals(details.body().getString("user.first_name"), user.firstName());
        assertEquals(details.body().getString("user.last_name"), user.lastName());
        assertEquals(details.body().getString("user.country"), user.country());
        assertEquals(details.body().getString("user.city"), user.city());
        assertEquals(userApi.verifyLogin(user.email(), user.password()).responseCode(), 200);
    }

    @Test(groups = "hybrid", dataProvider = "searchTerms", dataProviderClass = TestData.class,
            description = "UI search results match the searchProduct API")
    public void uiSearchMatchesApiSearch(String term) {
        List<String> apiNames = productApi.searchProduct(term).body().getList("products.name");

        ProductsPage page = new ProductsPage(driver()).open().search(term);

        assertEquals(page.sectionTitle(), "SEARCHED PRODUCTS");
        assertEquals(page.productNames(), apiNames, "UI and API disagree for search term '" + term + "'");
    }
}
