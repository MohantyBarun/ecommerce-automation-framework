package com.qa.framework.pages;

import com.qa.framework.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Explicit-wait helpers shared by every page. No implicit waits anywhere in the framework.
 * Also contains guards for the ad interstitials that the practice site injects.
 */
public abstract class BasePage {

    private static final String AD_VIGNETTE = "#google_vignette";
    private static final String REMOVE_ADS_JS =
            "document.querySelectorAll('ins.adsbygoogle, iframe[id^=\"aswift\"], #ad_position_box, .fc-consent-root')"
                    + ".forEach(e => e.remove());";

    private static final Logger LOG = LogManager.getLogger(BasePage.class);
    private static final By OVERLOAD_PAGE = By.xpath("//h2[contains(., 'under heavy load')]");
    private static final int OVERLOAD_RETRIES = 4;

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getInt("explicitWait")));
    }

    protected void openPath(String path) {
        driver.get(ConfigManager.baseUrl() + path);
        recoverFromOverload();
        removeAds();
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected List<WebElement> allVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected void click(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        scrollIntoView(element);
        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            // An ad overlay or sticky banner is on top: strip ads and fall back to a JS click.
            removeAds();
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
        escapeAdVignette();
        recoverFromOverload();
    }

    /**
     * Dispatches the click directly on the element. Use only where a hover effect
     * deterministically covers the target (a native click would land on the overlay).
     */
    protected void jsClick(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        scrollIntoView(element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void type(By locator, String text) {
        WebElement element = visible(locator);
        scrollIntoView(element);
        element.clear();
        element.sendKeys(text);
    }

    protected void selectByValue(By locator, String value) {
        new Select(visible(locator)).selectByValue(value);
    }

    protected void selectByText(By locator, String text) {
        new Select(visible(locator)).selectByVisibleText(text);
    }

    protected String text(By locator) {
        return visible(locator).getText().trim();
    }

    protected List<String> texts(By locator) {
        return allVisible(locator).stream().map(e -> e.getText().trim()).toList();
    }

    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected boolean isVisible(By locator) {
        return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    /** Full-page Google vignette ads append #google_vignette to the URL; reload the real target. */
    private void escapeAdVignette() {
        String url = driver.getCurrentUrl();
        if (url != null && url.endsWith(AD_VIGNETTE)) {
            driver.get(url.substring(0, url.length() - AD_VIGNETTE.length()));
            removeAds();
        }
    }

    /**
     * The public practice site sometimes answers with a "under heavy load (queue full)" page.
     * Re-open the URL with back-off instead of failing the test on an environment problem.
     * <p>
     * This deliberately does a fresh GET rather than refresh(): if the overloaded page was the
     * answer to a form POST (login, payment), a refresh would resubmit the form. Firefox blocks
     * that with a "resend data?" dialog, and it could also place a second order. If the POST never
     * went through, the next step in the test fails clearly and the test-level retry takes over.
     */
    private void recoverFromOverload() {
        for (int attempt = 1; attempt <= OVERLOAD_RETRIES && isPresent(OVERLOAD_PAGE); attempt++) {
            String url = driver.getCurrentUrl();
            LOG.warn("Site overloaded at {}, reload {}/{}", url, attempt, OVERLOAD_RETRIES);
            sleep(attempt * 3_000L);
            driver.get(url);
        }
        if (isPresent(OVERLOAD_PAGE)) {
            throw new IllegalStateException("Site still overloaded after " + OVERLOAD_RETRIES + " reloads");
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void removeAds() {
        ((JavascriptExecutor) driver).executeScript(REMOVE_ADS_JS);
    }
}
