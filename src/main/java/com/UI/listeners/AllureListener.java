package com.UI.listeners;

import io.qameta.allure.listener.TestLifecycleListener;
import org.testng.ITestListener;
import com.UI.utils.LoggerUtil;
import com.UI.utils.ScreenshotUtil;
import io.qameta.allure.Attachment;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.TestResult;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.nio.file.Files;
import java.nio.file.Paths;

public class AllureListener implements ITestListener, TestLifecycleListener {
    private static final Logger LOGGER = LoggerUtil.getLogger(AllureListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        LOGGER.info("Starting test: {}", result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOGGER.info("Test passed: {}", result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOGGER.error("Test failed: {}", result.getName());
        LOGGER.error("Failure reason: {}", result.getThrowable().getMessage());

        String screenshotPath = ScreenshotUtil.takeFailureScreenshot(result.getName(), result.getInstanceName());
        try {
            byte[] screenshot = Files.readAllBytes(Paths.get(screenshotPath));
            saveScreenshot(screenshot);
            saveLogs(LoggerUtil.getTestLogs());
        } catch (Exception e) {
            LOGGER.error("Failed to attach screenshot to Allure report", e);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOGGER.info("Test skipped: {}", result.getName());
    }

    @Override
    public void beforeTestStop(TestResult result) {
        if (result.getStatus() == Status.FAILED) {
            LOGGER.info("Attaching logs to failed test in Allure report");
            saveLogs(LoggerUtil.getTestLogs());
        }
    }

    @Attachment(value = "Screenshot", type = "image/png")
    private byte[] saveScreenshot(byte[] screenshot) {
        return screenshot;
    }

    @Attachment(value = "Test Logs", type = "text/plain")
    private String saveLogs(String logs) {
        return logs;
    }

    @Override
    public void onStart(ITestContext context) {
        LOGGER.info("Test Suite started: {}", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        LOGGER.info("Test Suite finished: {}", context.getName());
    }
}
