package com.UI.tests;

import com.UI.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.UI.reports.ExtentReportManager;
import com.UI.pages.LoginPage;

public class LoginTest extends BaseTest {

    /**
     * Test successful login
     */
    @Test(description = "Verify user can login successfully with valid credentials")
    public void testSuccessfulLogin() {
        ExtentReportManager.getTest().info("Testing successful login functionality");

        LoginPage loginPage = new LoginPage();
        loginPage.navigateToLoginPage();

        ExtentReportManager.getTest().info("Entering valid credentials");
        loginPage.fillUserName();
        loginPage.fillPassword();
        loginPage.clickSubmit();
        System.out.println("Verifying user is logged in    IIOOOOOO");
        ExtentReportManager.getTest().info("Verifying user is logged in");
        //Assert.assertTrue(homePage.isUserLoggedIn(), "User should be logged in");

        //String welcomeMessage = homePage.getWelcomeMessage();
       // Assert.assertTrue(welcomeMessage.contains("testuser"),
         //       "Welcome message should contain the username");
        //Assert.assertEquals(1,2);

        ExtentReportManager.getTest().fail("Login Not successful");
    }
   @Test(description = "Verify user can login successfully with valid credentials successfully")
    public void testSuccessfulLogin2() {
        ExtentReportManager.getTest().info("Testing successful login functionality");

        LoginPage loginPage = new LoginPage();
        loginPage.navigateToLoginPage();

        ExtentReportManager.getTest().info("Entering valid credentials");
        loginPage.fillUserName();
        loginPage.fillPassword();
        loginPage.clickSubmit();
        System.out.println("Verifying user is logged in");

        ExtentReportManager.getTest().info("Verifying user is logged in");

        ExtentReportManager.getTest().fail("Login Not successful");
    }
}
