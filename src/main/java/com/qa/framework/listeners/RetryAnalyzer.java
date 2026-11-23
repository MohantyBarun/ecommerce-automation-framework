package com.qa.framework.listeners;

import com.qa.framework.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/** Re-runs a failed test up to retryCount times before reporting it as failed. */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOG = LogManager.getLogger(RetryAnalyzer.class);
    private final int maxRetries = ConfigManager.getInt("retryCount");
    private int attempts;

    @Override
    public boolean retry(ITestResult result) {
        if (attempts < maxRetries) {
            attempts++;
            LOG.warn("Retrying {} (attempt {}/{}) after: {}", result.getName(), attempts, maxRetries,
                    result.getThrowable() == null ? "unknown" : result.getThrowable().getMessage());
            return true;
        }
        return false;
    }
}
