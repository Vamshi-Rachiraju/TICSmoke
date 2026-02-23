package com.tic.base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.tic.utils.ExcelTestDataBuilder;
import com.tic.utils.ExtentReportManager;
import com.tic.utils.ScreenshotUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.lang.reflect.Method;

/**
 * Base test class. Initializes driver, Extent report, and captures screenshots on failure.
 */
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeSuite(alwaysRun = true)
    public void initReport() {
        ExcelTestDataBuilder.createLoginDataFileIfMissing();
        ExtentReportManager.getInstance();
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(10));

        ExtentTest test = ExtentReportManager.getInstance()
                .createTest(method.getDeclaringClass().getSimpleName() + " - " + method.getName());
        ExtentReportManager.setTest(test);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (driver != null) {
            if (result.getStatus() == ITestResult.FAILURE) {
                ScreenshotUtils.capture(driver, "failure_" + result.getMethod().getMethodName());
                String base64 = ScreenshotUtils.captureBase64(driver);
                ExtentTest test = ExtentReportManager.getTest();
                if (test != null) {
                    test.log(Status.FAIL, result.getThrowable());
                    if (base64 != null) {
                        test.fail("Screenshot", MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
                    }
                }
            } else if (result.getStatus() == ITestResult.SUCCESS) {
                ExtentTest test = ExtentReportManager.getTest();
                if (test != null) {
                    test.pass("Test passed.");
                }
            }
            driver.quit();
        }
        ExtentReportManager.removeTest();
    }

    @AfterSuite(alwaysRun = true)
    public void flushReport() {
        ExtentReportManager.flush();
    }

    protected WebDriver getDriver() {
        return driver;
    }
}
