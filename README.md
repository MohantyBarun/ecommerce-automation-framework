# E-commerce Hybrid UI + API Automation Framework

[![Tests](https://github.com/MohantyBarun/ecommerce-automation-framework/actions/workflows/tests.yml/badge.svg)](https://github.com/MohantyBarun/ecommerce-automation-framework/actions/workflows/tests.yml)
[![Allure Report](https://img.shields.io/badge/Allure-live%20report-orange)](https://mohantybarun.github.io/ecommerce-automation-framework/)

**[Live Allure report →](https://mohantybarun.github.io/ecommerce-automation-framework/)** published by CI on every push to `main`, with trend history across runs.

End-to-end test automation framework for [automationexercise.com](https://automationexercise.com),
a public practice e-commerce site with both a web UI and a REST API.

**Stack:** Java 17 · Selenium 4 · TestNG · Cucumber (BDD) · RestAssured · Maven · Allure · Jackson · Datafaker · Log4j2 · Docker Selenium Grid · GitHub Actions

## What it covers

| Capability | How |
|---|---|
| Page Object Model | `pages/` with a fluent API: `new LoginPage(driver).open().loginAs(email, pw).loggedInUsername()` |
| API testing | RestAssured clients in `api/` (`UserApi`, `ProductApi`), positive and negative cases |
| **Hybrid UI + API** | Test data is created through the API and verified in the UI, and the reverse (UI signup verified via API). UI search results are compared with the search API |
| **BDD (Cucumber + Gherkin)** | 5 feature files (UI, checkout, hybrid, API) with Scenario Outlines, data tables and tags. Step definitions reuse the same page objects and API clients; PicoContainer gives each scenario its own context |
| Data-driven tests | JSON files in `src/test/resources/testdata`, loaded by `JsonDataReader` into parallel TestNG DataProviders |
| Parallel execution | `parallel="methods"` with a `ThreadLocal` WebDriver per thread; unique Datafaker users per test, so tests never collide |
| Cross-browser | Chrome, Firefox and Edge; local or Selenium Grid (`-Dexecution=remote`) |
| Retry for flaky tests | `RetryAnalyzer`, attached to every test by `RetryTransformer` (`retryCount` in config) |
| Resilience | Automatic back-off when the public site returns its "under heavy load" page (UI and API); ad interstitials are blocked/escaped |
| Reporting | Allure with `@Step` page actions, request/response attachments for every API call, and screenshot + URL + page source on failure |
| Test-data cleanup | Every account a test creates is deleted through the API in `@AfterMethod`, pass or fail |
| CI/CD | GitHub Actions: API job (TestNG + `@api` features) + UI matrix (chrome, firefox; TestNG + UI features) on a Dockerised Selenium Grid, then one combined Allure report published to GitHub Pages with run-over-run trends |

## Project structure

```
src/main/java/com/qa/framework
├── api/          BaseApi (spec, Allure filter, back-off), UserApi, ProductApi, ApiResponse
├── config/       ConfigManager: system property > env var > config.properties
├── driver/       DriverFactory (local/remote, chrome/firefox/edge), DriverManager (ThreadLocal)
├── listeners/    RetryAnalyzer, RetryTransformer, TestListener (logging + Allure environment)
├── models/       User, PaymentCard
├── pages/        BasePage (explicit waits only) + one class per page
└── utils/        JsonDataReader, TestDataFactory
src/test/java/com/qa/tests
├── base/         BaseTest (API setup + cleanup), BaseUiTest (browser lifecycle + failure evidence)
├── api/          ProductApiTest, UserApiTest
├── ui/           LoginTest, CartTest, CheckoutTest
├── hybrid/       ApiUiConsistencyTest
└── data/         DataProviders + JSON row records
src/test/java/com/qa/bdd
├── runners/      CucumberTestRunner (TestNG, scenarios run in parallel)
├── steps/        Shopper, Authentication, Cart, Checkout, Hybrid, Api step definitions
├── hooks/        Browser per UI scenario, failure evidence, API cleanup
└── context/      ScenarioContext (per-scenario state, injected by PicoContainer)
src/test/resources
├── features/     authentication, cart, checkout, ui_api_consistency, api (.feature)
├── suites/       testng.xml (all), api.xml, ui.xml, smoke.xml, bdd.xml
└── testdata/     invalid-logins.json, cart-products.json, search-terms.json
```

### BDD example

```gherkin
@hybrid
Scenario: An account registered through the UI is stored correctly
  Given a new shopper who is not registered yet
  When the shopper registers through the signup form
  Then the confirmation "ACCOUNT CREATED!" is shown and the shopper is logged in
  And the account details returned by the API match what the shopper entered
```

Tags: `@ui` `@api` `@hybrid` `@smoke` `@e2e`, plus one per feature area. `@api` scenarios never start a browser.

## Running

Requires JDK 17+. Maven does **not** need to be installed: use the wrapper (`mvnw` / `mvnw.cmd`).

```bash
./mvnw test                                                    # everything: TestNG suite, then Cucumber features
./mvnw test -DskipBdd=true                                     # TestNG tests only
./mvnw test -DskipTestNG=true                                  # Cucumber features only
./mvnw test -DskipTestNG=true -Dcucumber.filter.tags="@smoke and not @api"   # features by tag
./mvnw test -Dsuite=src/test/resources/suites/smoke.xml -Dcucumber.filter.tags=@smoke   # smoke, both layers
./mvnw test -Dbrowser=firefox -Dheadless=true                  # another browser, headless
```

Cucumber also writes its own HTML report to `target/cucumber/report.html`, and failed scenarios to `target/cucumber/rerun.txt`.

Any key in `src/main/resources/config.properties` can be overridden with `-Dkey=value` or an env var (`gridUrl` → `GRID_URL`).

### Selenium Grid in Docker

```bash
docker compose up -d                                           # hub + chrome + firefox nodes
./mvnw test -Dexecution=remote -Dbrowser=firefox               # Grid UI at http://localhost:4444/ui
docker compose down
```

### Allure report

```bash
./mvnw allure:serve        # build and open in the browser
./mvnw allure:report       # static report in target/site/allure-maven-plugin
```

## Design decisions

- **API for setup, UI for behaviour.** Creating a user through the signup form takes about 15 s and is not what most tests are about. Creating it through the API takes about 1 s, so UI tests start at the interesting step and the suite stays fast.
- **The site reports errors inside a 200.** automationexercise.com always returns HTTP 200 (as `text/html`) and puts the real status in a `responseCode` field. `ApiResponse` parses the body as JSON regardless and exposes both codes.
- **Explicit waits only.** Implicit waits are never set, so waits are predictable and do not stack with explicit ones.
- **BDD is a layer, not a second framework.** Feature files describe behaviour in business terms. Step definitions are thin and call the same page objects, API clients and data factory as the TestNG tests, so a locator change is fixed in one place. Cucumber runs in its own Surefire execution with Allure's TestNG listener switched off (`-spilistenerstoskip`), so each scenario shows up in the report once, with its Gherkin steps.
- **Safe-to-repeat versus unsafe actions.** Login and the first signup step are resubmitted automatically if the overloaded site swallows them. Creating an account and paying are never resubmitted automatically; a failure there is left to fail and be retried as a whole test, so a real defect (for example a payment silently bouncing back to the form) stays visible in the report.
- **Retry is a safety net, not a fix.** Retries are logged with their cause. Known environment problems (site overload, ad pop-ups) are handled at the source instead of being hidden by retries.
