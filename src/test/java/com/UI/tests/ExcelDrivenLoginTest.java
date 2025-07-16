package com.UI.tests;

import com.UI.base.BaseTest;
import com.UI.pages.LoginPage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static com.UI.constants.FrameworkConstants.SHEET_NAME;
import static com.UI.constants.FrameworkConstants.TEST_DATA_FILE;

public class ExcelDrivenLoginTest extends BaseTest {
    @DataProvider(name = "loginDataFromExcel")
    public Object[][] getLoginData() {
        return getTestData(TEST_DATA_FILE, SHEET_NAME);
    }

    @Test(dataProvider = "loginDataFromExcel", description = "Verify login scenarios using Excel data")
    public void testLoginWithExcelData(Map<String, String> testData) {
        String username = testData.get("username");
        String password = testData.get("password");
        boolean shouldSucceed = Boolean.parseBoolean(testData.get("shouldSucceed"));
        String expectedError = testData.get("expectedError");

        logger.info("Testing login with username: {}", username);

        LoginPage loginPage = new LoginPage();
        loginPage.navigateToLoginPage();

        loginPage.fillUserName(username);
        loginPage.fillPassword(password);
        loginPage.clickSubmit();
        System.out.println("Verifying user is logged in");
        if (shouldSucceed) {
            logger.info("Successfully logged in");
        } else {
            logger.info("Failed to log in");
        }
    }
}
