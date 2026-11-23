package com.qa.tests.api;

import com.qa.framework.api.ApiResponse;
import com.qa.framework.models.User;
import com.qa.framework.utils.TestDataFactory;
import com.qa.tests.base.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Epic("API")
@Feature("User accounts")
public class UserApiTest extends BaseTest {

    @Test(groups = {"api", "smoke"}, description = "Account lifecycle: create, verify login, read details, delete")
    @Severity(SeverityLevel.BLOCKER)
    public void accountLifecycle() {
        User user = TestDataFactory.newUser();

        ApiResponse created = userApi.createAccount(user);
        assertEquals(created.responseCode(), 201, created.raw());
        assertEquals(created.message(), "User created!");

        ApiResponse login = userApi.verifyLogin(user.email(), user.password());
        assertEquals(login.responseCode(), 200);
        assertEquals(login.message(), "User exists!");

        ApiResponse details = userApi.getUserDetailByEmail(user.email());
        assertEquals(details.responseCode(), 200);
        assertEquals(details.body().getString("user.email"), user.email());
        assertEquals(details.body().getString("user.name"), user.name());
        assertEquals(details.body().getString("user.city"), user.city());

        ApiResponse deleted = userApi.deleteAccount(user.email(), user.password());
        assertEquals(deleted.responseCode(), 200);
        assertEquals(deleted.message(), "Account deleted!");

        assertEquals(userApi.verifyLogin(user.email(), user.password()).responseCode(), 404,
                "Deleted user should no longer be able to log in");
    }

    @Test(groups = "api", description = "Creating an account with an existing email is rejected")
    public void duplicateEmailIsRejected() {
        User user = createUserViaApi();

        ApiResponse duplicate = userApi.createAccount(user);

        assertEquals(duplicate.responseCode(), 400);
        assertEquals(duplicate.message(), "Email already exists!");
    }

    @Test(groups = "api", description = "verifyLogin with wrong password returns 404")
    public void verifyLoginWithWrongPassword() {
        User user = createUserViaApi();

        ApiResponse response = userApi.verifyLogin(user.email(), "wrong-" + user.password());

        assertEquals(response.responseCode(), 404);
        assertEquals(response.message(), "User not found!");
    }
}
