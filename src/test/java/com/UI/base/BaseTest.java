package com.UI.base;

import com.UI.config.ConfigManager;
import com.UI.drivers.DriverManager;
import com.UI.reports.ExtentReportManager;
import com.UI.utils.KubernetesUtil;
import com.UI.utils.LoggerUtil;
import com.UI.utils.ExcelUtil;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static com.UI.constants.FrameworkConstants.TEST_DATA_PATH;

/**
 * Base test class that all test classes should extend
 */
public class BaseTest {
    protected WebDriver driver;
    protected final ConfigManager configManager = ConfigManager.getInstance();
    protected final Logger logger = LoggerUtil.getLogger(this.getClass());

    // Constants for test data
    //protected static final String DEFAULT_SHEET = "TestData";
    //protected static final String KUBE_YAML_PATH="kubernetes/Selenium-grid.yaml";
    @BeforeSuite(alwaysRun = true)
    public void setupSuite() throws InterruptedException {
        /*System.out.println("Kubernetes Start");
        KubernetesUtil.startSeleniumGrid();
        Thread.sleep(20000);
        System.out.println("Kubernetes Assigning Port");
        KubernetesUtil.assignPortToLocal();
        logger.info("Kubernetes Setup Done");*/
        ExtentReportManager.initReports();
        logger.info("Test Suite setup completed");
    }


    @BeforeMethod(alwaysRun = true)
    public void setup(Method method, Object[] testData) {
        // Get test method name and description
        String testName = method.getName();
        String testDescription = method.getAnnotation(Test.class) != null ?
                method.getAnnotation(Test.class).description() : "";

        // Initialize test reporting
        if (testDescription.isEmpty()) {
            ExtentReportManager.createTest(testName);
        } else {
            ExtentReportManager.createTest(testName, testDescription);
        }

        // Log test start with parameters if present
        logger.info("Starting test: {}", testName);
        if (testData != null && testData.length > 0) {
            logger.info("Test Parameters: {}", (Object[]) testData);
        }

        // Initialize WebDriver
        driver = DriverManager.initializeDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        logTestResult(result);

        // Cleanup
        DriverManager.quitDriver();
        ExtentReportManager.clearThreadLocal();
        LoggerUtil.clearThreadLocalLogger();
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        ExtentReportManager.flushReports();
        logger.info("Test Suite completed");
        KubernetesUtil.stopSeleniumGrid();
        System.out.println("Kubernetes Stopped");
    }

    /**
     * Get test data from Excel file
     * @param fileName Excel file name (from test data directory)
     * @param sheetName Sheet name
     * @return Object array for TestNG DataProvider
     */
    protected Object[][] getTestData(String fileName, String sheetName) {
        String filePath = TEST_DATA_PATH + fileName;
        List<Map<String, String>> testData = ExcelUtil.readExcelData(filePath, sheetName);

        Object[][] data = new Object[testData.size()][];
        for (int i = 0; i < testData.size(); i++) {
            data[i] = new Object[]{testData.get(i)};
        }

        return data;
    }

    /**
     * Log test execution result
     * @param result TestNG test result
     */
    protected void logTestResult(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                logger.info("Test passed: {}", testName);
                ExtentReportManager.getTest().pass("Test executed successfully");
                break;
            case ITestResult.FAILURE:
                logger.error("Test failed: {}", testName);
                logger.error("Failure reason: {}", result.getThrowable().getMessage());
                ExtentReportManager.getTest().fail("Test failed: " + result.getThrowable().getMessage());
                break;
            case ITestResult.SKIP:
                logger.info("Test skipped: {}", testName);
                ExtentReportManager.getTest().skip("Test skipped: " + result.getThrowable().getMessage());
                break;
            default:
                logger.warn("Unexpected test status for test: {}", testName);
        }
    }

    /**
     * Get base URL from configuration
     * @return Base URL
     */
    protected String getBaseUrl() {
        return configManager.getProperty("base.url");
    }

    /**
     * Verify actual result matches expected
     * @param actual Actual result
     * @param expected Expected result
     * @param message Assertion message
     */
    protected void verifyEquals(Object actual, Object expected, String message) {
        try {
            org.testng.Assert.assertEquals(actual, expected, message);
            logger.info("Verification passed: {}", message);
            ExtentReportManager.getTest().pass(message);
        } catch (AssertionError e) {
            logger.error("Verification failed: {}", message);
            ExtentReportManager.getTest().fail(message +
                    "\nExpected: " + expected +
                    "\nActual: " + actual);
            throw e;
        }
    }

    /**
     * Verify condition is true
     * @param condition Condition to verify
     * @param message Assertion message
     */
    protected void verifyTrue(boolean condition, String message) {
        try {
            org.testng.Assert.assertTrue(condition, message);
            logger.info("Verification passed: {}", message);
            ExtentReportManager.getTest().pass(message);
        } catch (AssertionError e) {
            logger.error("Verification failed: {}", message);
            ExtentReportManager.getTest().fail(message);
            throw e;
        }
    }
}