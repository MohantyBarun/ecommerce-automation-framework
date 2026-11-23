package com.qa.framework.driver;

import com.qa.framework.config.ConfigManager;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;

/** Builds local or Grid WebDriver instances for Chrome, Firefox and Edge. */
public final class DriverFactory {

    /** Ad and tracker hosts that slow the practice site down and pop up interstitials. */
    private static final String AD_HOST_RULES = String.join(", ",
            "MAP *.googlesyndication.com ~NOTFOUND",
            "MAP *.doubleclick.net ~NOTFOUND",
            "MAP *.googleadservices.com ~NOTFOUND",
            "MAP *.google-analytics.com ~NOTFOUND",
            "MAP *.googletagmanager.com ~NOTFOUND",
            "MAP *.googletagservices.com ~NOTFOUND",
            "MAP *.adtrafficquality.google ~NOTFOUND",
            "MAP fundingchoicesmessages.google.com ~NOTFOUND");

    private DriverFactory() {
    }

    public static WebDriver create(String browser) {
        MutableCapabilities options = switch (browser) {
            case "chrome" -> chromium(new ChromeOptions());
            case "edge" -> chromium(new EdgeOptions());
            case "firefox" -> firefox();
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };

        WebDriver driver = "remote".equalsIgnoreCase(ConfigManager.get("execution"))
                ? remote(options)
                : local(browser, options);

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigManager.getInt("pageLoadTimeout")));
        return driver;
    }

    private static <T extends ChromiumOptions<?>> T chromium(T options) {
        if (ConfigManager.getBoolean("headless")) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1920,1080", "--disable-notifications",
                "--disable-search-engine-choice-screen", "--no-sandbox", "--disable-dev-shm-usage");
        if (ConfigManager.getBoolean("blockAds")) {
            options.addArguments("--host-resolver-rules=" + AD_HOST_RULES);
        }
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        return options;
    }

    private static FirefoxOptions firefox() {
        FirefoxOptions options = new FirefoxOptions();
        if (ConfigManager.getBoolean("headless")) {
            options.addArguments("-headless");
        }
        options.addArguments("--width=1920", "--height=1080");
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        return options;
    }

    private static WebDriver local(String browser, MutableCapabilities options) {
        // Selenium Manager resolves the matching driver binary automatically.
        return switch (browser) {
            case "chrome" -> new ChromeDriver((ChromeOptions) options);
            case "edge" -> new EdgeDriver((EdgeOptions) options);
            default -> new FirefoxDriver((FirefoxOptions) options);
        };
    }

    private static WebDriver remote(MutableCapabilities options) {
        try {
            return new RemoteWebDriver(URI.create(ConfigManager.get("gridUrl")).toURL(), options);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid gridUrl", e);
        }
    }
}
