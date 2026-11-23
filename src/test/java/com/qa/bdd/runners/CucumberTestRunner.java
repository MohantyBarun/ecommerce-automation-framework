package com.qa.bdd.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

/**
 * Runs every feature under src/test/resources/features through TestNG.
 * Scenarios run in parallel (data-provider-thread-count in bdd.xml); each gets its own
 * browser via the ThreadLocal DriverManager and its own ScenarioContext via PicoContainer.
 * <p>
 * Filter by tag: -Dcucumber.filter.tags="@smoke and not @api"
 */
@CucumberOptions(
        features = "classpath:features",
        glue = "com.qa.bdd",
        plugin = {
                "io.qameta.allure.cucumber8jvm.AllureCucumber8Jvm",
                "html:target/cucumber/report.html",
                "json:target/cucumber/report.json",
                "rerun:target/cucumber/rerun.txt"
        })
public class CucumberTestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios(ITestContext context) {
        return super.scenarios(context);
    }
}
