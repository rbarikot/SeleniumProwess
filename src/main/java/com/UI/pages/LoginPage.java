package com.UI.pages;

import org.openqa.selenium.By;

public class LoginPage extends BasePage {
    public LoginPage() {
        super();
    }
    public By username= By.id("login-username");
    public By pwd= By.id("login-password");
    public By submit= By.id("login-submit-btn");
    /**
     * Click submit Link
     */
    public void clickSubmit() {
        click(submit);
        LOGGER.info("Clicked user profile link");
        // Return a new ProfilePage object once implemented
    }
    /**
     * Send Value to Password
     */
    public void fillPassword()
    {
        sendKeys(pwd, "admin");
        LOGGER.info("Sent Value to Password field");
    }
    /**
     * Send Value to Username
     */
    public void fillUserName()
    {
        sendKeys(username, "admin");
        LOGGER.info("Sent Value to User name field");
    }

    /**
     * Navigate to Login page
     */
    public void navigateToLoginPage() {
        navigateToBaseUrl();
        LOGGER.info("Navigated to Login page");
    }
    /**
     * Send Value to Password
     */
    public void fillPassword(String password)
    {
        sendKeys(pwd,password);
        LOGGER.info("Sent Value to Password field");
    }
    /**
     * Send Value to Username
     */
    public void fillUserName(String userName)
    {
        sendKeys(username,userName);
        LOGGER.info("Sent Value to User name field");
    }


}
