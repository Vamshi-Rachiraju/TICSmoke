package com.tic.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object for the Login page (HeroKu App - The Internet).
 * URL: https://the-internet.herokuapp.com/login
 */
public class LoginPage extends BasePage {

    private static final String LOGIN_URL = "https://the-internet.herokuapp.com/login";

    @FindBy(id = "username")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    @FindBy(id = "flash")
    private WebElement flashMessage;

    @FindBy(css = ".flash.success")
    private WebElement successMessage;

    @FindBy(css = ".flash.error")
    private WebElement errorMessage;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        navigateTo(LOGIN_URL);
    }

    public void enterUsername(String username) {
        waitAndSendKeys(usernameInput, username);
    }

    public void enterPassword(String password) {
        waitAndSendKeys(passwordInput, password);
    }

    public void clickLogin() {
        waitAndClick(loginButton);
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public String getFlashMessageText() {
        return wait.until(webDriver -> flashMessage.getText()).trim();
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            return successMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isErrorMessageDisplayed() {
        try {
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public static String getLoginUrl() {
        return LOGIN_URL;
    }
}
