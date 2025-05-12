package com.UI.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.UI.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.UI.constants.FrameworkConstants.REPORT_DIRECTORY;

/**
 * Manager for ExtentReports
 */
public class ExtentReportManager {
    private static final Logger LOGGER = LoggerUtil.getLogger(ExtentReportManager.class);
    private static ExtentReports extentReports;
    private static final Map<Long, ExtentTest> TEST_MAP = new HashMap<>();
    private static final ThreadLocal<ExtentTest> EXTENT_TEST_THREAD_LOCAL = new ThreadLocal<>();

    private ExtentReportManager() {
        // Private constructor to prevent instantiation
    }

    public static synchronized ExtentReports initReports() {
        if (extentReports == null) {
            File directory = new File(REPORT_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String reportFilePath = REPORT_DIRECTORY + File.separator + "TestReport_" + timestamp + ".html";
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFilePath);
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setDocumentTitle("Test Automation Report");
            sparkReporter.config().setReportName("Test Execution Report");
            sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
            extentReports.setSystemInfo("Environment", "QA");
            LOGGER.info("ExtentReports initialized. Report will be saved to: {}", reportFilePath);
        }
        return extentReports;
    }

    public static synchronized ExtentTest createTest(String testName) {
        ExtentTest test = initReports().createTest(testName);
        TEST_MAP.put(Thread.currentThread().getId(), test);
        EXTENT_TEST_THREAD_LOCAL.set(test);
        LOGGER.debug("Created test in ExtentReports: {}", testName);
        return test;
    }

    public static synchronized ExtentTest createTest(String testName, String description) {
        ExtentTest test = initReports().createTest(testName, description);
        TEST_MAP.put(Thread.currentThread().getId(), test);
        EXTENT_TEST_THREAD_LOCAL.set(test);
        LOGGER.debug("Created test in ExtentReports: {} - {}", testName, description);
        return test;
    }

    public static synchronized ExtentTest getTest() {
        return EXTENT_TEST_THREAD_LOCAL.get();
    }

    public static synchronized void flushReports() {
        if (extentReports != null) {
            extentReports.flush();
            LOGGER.info("ExtentReports flushed to disk");
        }
    }

    public static void addScreenshotToReport(String screenshotPath, String title) {
        try {
            if (screenshotPath != null) {
                ExtentTest test = getTest();
                if (test != null) {
                    test.fail(MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                    LOGGER.debug("Added screenshot to report: {}", title);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to add screenshot to report", e);
        }
    }

    public static void clearThreadLocal() {
        EXTENT_TEST_THREAD_LOCAL.remove();
    }
}