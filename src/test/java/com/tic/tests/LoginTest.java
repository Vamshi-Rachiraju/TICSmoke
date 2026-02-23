package com.tic.tests;

import com.tic.base.BaseTest;
import com.tic.pages.LoginPage;
import com.tic.utils.ExcelDataReader;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Login page tests using DataProvider with Excel (.xlsx).
 * Data: src/test/resources/testdata/LoginData.xlsx (or auto-generated in test-output/testdata/).
 * Uses HeroKu "The Internet" login: https://the-internet.herokuapp.com/login
 */
public class LoginTest extends BaseTest {

    @DataProvider(name = "validLoginData")
    public Object[][] validLoginData() {
        return ExcelDataReader.getData("ValidLogin");
    }

    @DataProvider(name = "invalidLoginData")
    public Object[][] invalidLoginData() {
        return ExcelDataReader.getData("InvalidLogin");
    }

    @DataProvider(name = "loginFormData")
    public Object[][] loginFormData() {
        return ExcelDataReader.getData("LoginForm");
    }

    @Test(description = "Verify successful login with valid credentials", dataProvider = "validLoginData")
    public void testSuccessfulLogin(String username, String password) {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.open();
        loginPage.login(username, password);
        assertTrue(loginPage.isSuccessMessageDisplayed(), "Success message should be displayed after valid login");
        assertTrue(loginPage.getFlashMessageText().toLowerCase().contains("you logged into a secure area"),
                "Flash message should indicate successful login");
    }

    @Test(description = "Verify login fails with invalid credentials", dataProvider = "invalidLoginData")
    public void testInvalidLogin(String username, String password, String expectedMessagePart) {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.open();
        loginPage.login(username, password);
        assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed for invalid login");
        String flashText = loginPage.getFlashMessageText();
        assertTrue(flashText.contains(expectedMessagePart),
                "Flash message should contain: " + expectedMessagePart + ", but was: " + flashText);
    }

    @Test(description = "Verify login page loads and form accepts input", dataProvider = "loginFormData")
    public void testLoginFormAcceptsInput(String username, String password) throws InterruptedException {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.open();
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();
        Thread.sleep(5000);
        // After submit we should see either success or error message (page responded)
        String flashText = loginPage.getFlashMessageText();
        assertFalse(flashText.isEmpty(), "Flash message should be present after form submit");
    }
}
