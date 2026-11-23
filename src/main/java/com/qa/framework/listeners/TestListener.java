package com.qa.framework.listeners;

import com.qa.framework.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Logs test lifecycle and writes the Allure "Environment" widget data. */
public class TestListener implements ITestListener, ISuiteListener {

    private static final Logger LOG = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ISuite suite) {
        Properties env = new Properties();
        env.setProperty("Browser", ConfigManager.browser());
        env.setProperty("Execution", ConfigManager.get("execution"));
        env.setProperty("Headless", ConfigManager.get("headless"));
        env.setProperty("Base.URL", ConfigManager.baseUrl());
        env.setProperty("API.URL", ConfigManager.apiBaseUrl());
        env.setProperty("Java", System.getProperty("java.version"));
        env.setProperty("OS", System.getProperty("os.name"));

        Path dir = Path.of(System.getProperty("allure.results.directory", "target/allure-results"));
        try {
            Files.createDirectories(dir);
            try (Writer w = Files.newBufferedWriter(dir.resolve("environment.properties"))) {
                env.store(w, "Allure environment");
            }
        } catch (IOException e) {
            LOG.warn("Could not write Allure environment file", e);
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        LOG.info("START  {}", name(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("PASS   {}", name(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOG.error("FAIL   {} -> {}", name(result), result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOG.warn("SKIP   {}{}", name(result), result.wasRetried() ? " (will retry)" : "");
    }

    private static String name(ITestResult r) {
        return r.getTestClass().getRealClass().getSimpleName() + "." + r.getMethod().getMethodName();
    }
}
