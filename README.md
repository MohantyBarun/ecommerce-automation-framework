# E-commerce Hybrid UI + API Automation Framework

![Tests](https://github.com/MohantyBarun/ecommerce-automation-framework/actions/workflows/tests.yml/badge.svg)

End-to-end test automation framework for [automationexercise.com](https://automationexercise.com),
a public practice e-commerce site with both a web UI and a REST API.

**Stack:** Java 17 · Selenium 4 · TestNG · RestAssured · Maven · Allure · Jackson · Datafaker · Log4j2 · Docker Selenium Grid · GitHub Actions

## What it covers

| Capability | How |
|---|---|
| Page Object Model | `pages/` with a fluent API: `new LoginPage(driver).open().loginAs(email, pw).loggedInUsername()` |
| API testing | RestAssured clients in `api/` (`UserApi`, `ProductApi`), positive and negative cases |
| **Hybrid UI + API** | Test data is created through the API and verified in the UI, and the reverse (UI signup verified via API). UI search results are compared with the search API |
| Data-driven tests | JSON files in `src/test/resources/testdata`, loaded by `JsonDataReader` into parallel TestNG DataProviders |
| Parallel execution | `parallel="methods"` with a `ThreadLocal` WebDriver per thread; unique Datafaker users per test, so tests never collide |
| Cross-browser | Chrome, Firefox and Edge; local or Selenium Grid (`-Dexecution=remote`) |
| Retry for flaky tests | `RetryAnalyzer`, attached to every test by `RetryTransformer` (`retryCount` in config) |
| Resilience | Automatic back-off when the public site returns its "under heavy load" page (UI and API); ad interstitials are blocked/escaped |
| Reporting | Allure with `@Step` page actions, request/response attachments for every API call, and screenshot + URL + page source on failure |
| Test-data cleanup | Every account a test creates is deleted through the API in `@AfterMethod`, pass or fail |
| CI/CD | GitHub Actions: API job + UI matrix (chrome, firefox) on a Dockerised Selenium Grid, then one combined Allure report artifact |

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
src/test/resources
├── suites/       testng.xml (all), api.xml, ui.xml, smoke.xml
└── testdata/     invalid-logins.json, cart-products.json, search-terms.json
```

## Running

Requires JDK 17+. Maven does **not** need to be installed: use the wrapper (`mvnw` / `mvnw.cmd`).

```bash
./mvnw test                                                    # full regression, local Chrome
./mvnw test -Dsuite=src/test/resources/suites/smoke.xml        # smoke only
./mvnw test -Dsuite=src/test/resources/suites/api.xml          # API only, no browser
./mvnw test -Dbrowser=firefox -Dheadless=true                  # another browser, headless
```

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
- **Retry is a safety net, not a fix.** Retries are logged with their cause. Known environment problems (site overload, ad pop-ups) are handled at the source instead of being hidden by retries.
