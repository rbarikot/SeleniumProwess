package com.UI.utils;

import com.UI.config.ConfigManager;
import com.UI.drivers.DriverManager;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.UI.constants.FrameworkConstants.SCREENSHOT_PATH;

/**
 * Utility class for taking screenshots
 */
public class ScreenshotUtil {
    private static final Logger LOGGER = LoggerUtil.getLogger(ScreenshotUtil.class);
    private static final ConfigManager CONFIG_MANAGER = ConfigManager.getInstance();
    //private static final String SCREENSHOT_PATH = "test-output/reports/screenshots";

    private ScreenshotUtil() {
        // Private constructor to prevent instantiation
    }

    public static String takeScreenshot() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "screenshot_" + timeStamp + ".png";
        return takeScreenshot(fileName);
    }

    public static String takeScreenshot(String fileName) {
        WebDriver driver = DriverManager.getDriver();
        if (driver == null) {
            LOGGER.error("Cannot take screenshot - WebDriver is null");
            return null;
        }

        try {
            File directory = new File(SCREENSHOT_PATH);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String filePath = SCREENSHOT_PATH + File.separator + fileName;
            File destination = new File(filePath);
            FileUtils.copyFile(screenshotFile, destination);

            // Return relative path for ExtentReports
            return "screenshots/" + fileName;
        } catch (IOException e) {
            LOGGER.error("Failed to take screenshot", e);
            return null;
        }
    }

    public static String takeFailureScreenshot(String testName, String testClass) {
        String className = testClass.substring(testClass.lastIndexOf('.') + 1);
        String fileName = "failure_" + className + "_" + testName + "_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".png";
        return takeScreenshot(fileName);
    }
}