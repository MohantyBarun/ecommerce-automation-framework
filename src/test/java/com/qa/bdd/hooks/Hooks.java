package com.qa.bdd.hooks;

import com.qa.bdd.context.ScenarioContext;
import com.qa.framework.api.UserApi;
import com.qa.framework.config.ConfigManager;
import com.qa.framework.driver.DriverManager;
import com.qa.framework.models.User;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.nio.charset.StandardCharsets;

/** Browser lifecycle for UI scenarios, failure evidence, and API cleanup of created accounts. */
public class Hooks {

    private static final Logger LOG = LogManager.getLogger(Hooks.class);

    private final ScenarioContext context;
    private final UserApi userApi = new UserApi();

    public Hooks(ScenarioContext context) {
        this.context = context;
    }

    /** Pure API scenarios (@api) never pay for a browser. */
    @Before("not @api")
    public void startBrowser() {
        DriverManager.start(ConfigManager.browser());
    }

    @After(value = "not @api", order = 100)
    public void captureEvidenceAndQuit(Scenario scenario) {
        if (!DriverManager.hasDriver()) {
            return;
        }
        try {
            if (scenario.isFailed()) {
                WebDriver driver = DriverManager.getDriver();
                scenario.attach(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES), "image/png",
                        "Screenshot on failure");
                scenario.attach(driver.getCurrentUrl(), "text/plain", "URL on failure");
                scenario.attach(driver.getPageSource().getBytes(StandardCharsets.UTF_8), "text/html",
                        "Page source on failure");
            }
        } catch (RuntimeException e) {
            LOG.warn("Could not capture failure evidence", e);
        } finally {
            DriverManager.quit();
        }
    }

    /** Runs after the browser hook (lower order runs later) and whether the scenario passed or not. */
    @After(order = 10)
    public void deleteCreatedAccounts() {
        for (User user : context.usersToDelete()) {
            try {
                userApi.deleteAccount(user.email(), user.password());
            } catch (RuntimeException e) {
                LOG.warn("Cleanup failed for {}", user.email(), e);
            }
        }
    }
}
