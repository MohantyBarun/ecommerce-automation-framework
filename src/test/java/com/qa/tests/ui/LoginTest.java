package com.qa.tests.ui;

import com.qa.framework.models.User;
import com.qa.framework.pages.HomePage;
import com.qa.framework.pages.LoginPage;
import com.qa.tests.base.BaseUiTest;
import com.qa.tests.data.LoginCase;
import com.qa.tests.data.TestData;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Epic("UI")
@Feature("Authentication")
public class LoginTest extends BaseUiTest {

    @Test(groups = {"ui", "smoke"}, description = "Registered user can log in and log out")
    @Severity(SeverityLevel.BLOCKER)
    public void loginAndLogout() {
        User user = createUserViaApi();   // fast API setup instead of clicking through signup

        HomePage home = new LoginPage(driver()).open().loginAs(user.email(), user.password());
        assertTrue(home.isLoggedIn(), "User should be logged in");
        assertEquals(home.loggedInUsername(), user.name());

        LoginPage login = home.logout();
        assertTrue(login.isOnLoginPage(), "Logout should land on the login page");
        assertFalse(new HomePage(driver()).isLoggedIn());
    }

    @Test(groups = "ui", dataProvider = "invalidLogins", dataProviderClass = TestData.class,
            description = "Invalid credentials are rejected")
    public void invalidLoginIsRejected(LoginCase testCase) {
        LoginPage page = new LoginPage(driver()).open()
                .attemptInvalidLogin(testCase.email(), testCase.password());

        if (testCase.expectedError() == null) {
            assertTrue(page.isOnLoginPage(), "Browser validation should keep the user on the form");
        } else {
            assertEquals(page.loginError(), testCase.expectedError());
        }
        assertFalse(new HomePage(driver()).isLoggedIn());
    }

    @Test(groups = "ui", description = "Signing up with an email that is already registered shows an error")
    public void signupWithExistingEmailIsRejected() {
        User existing = createUserViaApi();

        String error = new LoginPage(driver()).open()
                .attemptSignupWithExistingEmail("Someone Else", existing.email())
                .signupError();

        assertEquals(error, "Email Address already exist!");
    }
}
