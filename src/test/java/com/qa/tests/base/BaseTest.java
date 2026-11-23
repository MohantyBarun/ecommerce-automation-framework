package com.qa.tests.base;

import com.qa.framework.api.ApiResponse;
import com.qa.framework.api.UserApi;
import com.qa.framework.models.User;
import com.qa.framework.utils.TestDataFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Base for every test. Provides API-driven test-data setup and guaranteed cleanup:
 * any account created through {@link #createUserViaApi()} or registered with
 * {@link #registerForCleanup(User)} is deleted after the test, pass or fail.
 */
public abstract class BaseTest {

    protected static final Logger LOG = LogManager.getLogger(BaseTest.class);
    private static final ThreadLocal<List<User>> USERS_TO_DELETE = ThreadLocal.withInitial(ArrayList::new);

    protected final UserApi userApi = new UserApi();

    protected User createUserViaApi() {
        User user = TestDataFactory.newUser();
        ApiResponse response = userApi.createAccount(user);
        assertEquals(response.responseCode(), 201, "Precondition failed, account not created: " + response.raw());
        registerForCleanup(user);
        return user;
    }

    protected void registerForCleanup(User user) {
        USERS_TO_DELETE.get().add(user);
    }

    @AfterMethod(alwaysRun = true)
    public void deleteTestUsers() {
        for (User user : USERS_TO_DELETE.get()) {
            try {
                ApiResponse r = userApi.deleteAccount(user.email(), user.password());
                LOG.info("Cleanup {} -> {}", user.email(), r.message());
            } catch (RuntimeException e) {
                LOG.warn("Cleanup failed for {}", user.email(), e);
            }
        }
        USERS_TO_DELETE.get().clear();
    }
}
