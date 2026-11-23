package com.qa.tests.base;

import com.qa.framework.config.ConfigManager;
import com.qa.framework.driver.DriverManager;
import io.qameta.allure.Allure;
import io.qameta.allure.AttachmentOptions;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/** Starts a fresh browser per test method and attaches a screenshot + URL to Allure on failure. */
public abstract class BaseUiTest extends BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void startBrowser() {
        DriverManager.start(ConfigManager.browser());
    }

    @AfterMethod(alwaysRun = true)
    public void stopBrowser(ITestResult result) {
        if (!DriverManager.hasDriver()) {
            return;
        }
        try {
            if (!result.isSuccess()) {
                WebDriver driver = DriverManager.getDriver();
                byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                Allure.attachment("Screenshot on failure", "image/png", new ByteArrayInputStream(png),
                        AttachmentOptions.withFileExtension("png"));
                Allure.attachment("URL on failure", driver.getCurrentUrl());
                Allure.attachment("Page source on failure", "text/html",
                        new ByteArrayInputStream(driver.getPageSource().getBytes(StandardCharsets.UTF_8)),
                        AttachmentOptions.withFileExtension("html"));
            }
        } catch (RuntimeException e) {
            LOG.warn("Could not capture failure evidence", e);
        } finally {
            DriverManager.quit();
        }
    }

    protected WebDriver driver() {
        return DriverManager.getDriver();
    }
}
