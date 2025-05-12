package com.UI.listeners;

import com.aventstack.extentreports.Status;
import com.UI.reports.ExtentReportManager;
import com.UI.utils.LoggerUtil;
import com.UI.utils.ScreenshotUtil;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener for test events
 */
public class TestListener implements ITestListener {
    private static final Logger LOGGER = LoggerUtil.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        LOGGER.info("Test started: {}", result.getName());

        String testDescription = result.getMethod().getDescription();
        if (testDescription == null || testDescription.isEmpty()) {
            testDescription = result.getName();
        }

        Object[] params = result.getParameters();
        if (params != null && params.length > 0) {
            StringBuilder paramLog = new StringBuilder("Test parameters: ");
            for (Object param : params) {
                paramLog.append(param).append(", ");
            }
            LOGGER.info(paramLog.substring(0, paramLog.length() - 2));
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOGGER.info("Test passed: {}", result.getName());
        ExtentReportManager.getTest().log(Status.PASS, "Test passed successfully");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOGGER.error("Test failed: {}", result.getName());
        LOGGER.error("Failure reason: {}", result.getThrowable().getMessage());

        String testClass = result.getInstanceName();
        String testName = result.getName();
        String screenshotPath = ScreenshotUtil.takeFailureScreenshot(testName, testClass);

        ExtentReportManager.getTest().log(Status.FAIL, "Test Failed: " + result.getThrowable().getMessage());
        ExtentReportManager.getTest().log(Status.FAIL, result.getThrowable());

        if (screenshotPath != null) {
            ExtentReportManager.addScreenshotToReport(screenshotPath, "Failure Screenshot");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOGGER.info("Test skipped: {}", result.getName());
        ExtentReportManager.getTest().log(Status.SKIP, "Test Skipped: " + result.getThrowable().getMessage());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        LOGGER.info("Test failed but within success percentage: {}", result.getName());
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        LOGGER.error("Test failed with timeout: {}", result.getName());
        onTestFailure(result);
    }

    @Override
    public void onStart(ITestContext context) {
        LOGGER.info("Test Suite started: {}", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        LOGGER.info("Test Suite finished: {}", context.getName());

        int passedTests = context.getPassedTests().size();
        int failedTests = context.getFailedTests().size();
        int skippedTests = context.getSkippedTests().size();

        LOGGER.info("Test Results Summary - Passed: {}, Failed: {}, Skipped: {}",
                passedTests, failedTests, skippedTests);
    }
}