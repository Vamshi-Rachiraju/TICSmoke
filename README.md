# TICSmoke

Maven + Selenium + Java + TestNG project with **Page Object Model**, **Extent Reports**, and **screenshots**. Every test uses a **DataProvider**.

## Stack

- **Java 17**
- **Maven**
- **Selenium 4**
- **TestNG**
- **Extent Reports 5**
- **WebDriverManager** (auto ChromeDriver)
- **Apache POI** (Excel DataProvider)

## Structure

```
src/test/java/com/tic/
├── base/BaseTest.java          # Driver setup, Extent + screenshot on failure
├── pages/
│   ├── BasePage.java           # POM base (wait, PageFactory)
│   └── LoginPage.java          # Login page object (HeroKu demo)
├── tests/LoginTest.java        # Login tests with Excel DataProviders
└── utils/
    ├── ExcelDataReader.java    # Read .xlsx for DataProvider
    ├── ExcelTestDataBuilder.java # Build sample LoginData.xlsx
    ├── ExtentReportManager.java
    └── ScreenshotUtils.java
src/test/resources/
├── testdata/
│   └── LoginData.xlsx          # Optional: Excel test data (see below)
└── testng.xml
```

## Excel DataProvider

- **Data file:** `src/test/resources/testdata/LoginData.xlsx` (optional). If missing, a sample file is created at `test-output/testdata/LoginData.xlsx` on first run.
- **Sheets:** `ValidLogin` (username, password), `InvalidLogin` (username, password, expectedMessagePart), `LoginForm` (username, password). First row = header, data from row 2.
- To create or refresh the Excel in resources, run `ExcelTestDataBuilder` as a Java application (main method); it writes `src/test/resources/testdata/LoginData.xlsx`.

## Run tests

```bash
mvn clean test
```

- **Report:** `test-output/extent-reports/ExtentReport.html`
- **Screenshots:** `test-output/screenshots/` (and embedded in report on failure)

## Sample login tests

Uses [The Internet - HeroKu](https://the-internet.herokuapp.com/login):

- **Valid login:** `tomsmith` / `SuperSecretPassword!`
- **Invalid:** DataProvider rows for wrong user, wrong password, empty fields

Tests:

1. `testSuccessfulLogin` – DataProvider: valid credentials.
2. `testInvalidLogin` – DataProvider: invalid credentials + expected error message part.
3. `testLoginFormAcceptsInput` – DataProvider: various username/password; checks form submit and flash message.

## Requirements

- JDK 17+
- Chrome (for headed runs; headless is used by default in `BaseTest`)

To run with visible browser, change `BaseTest.setUp()` and remove the `--headless` option from `ChromeOptions`.
