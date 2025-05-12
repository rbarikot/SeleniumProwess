package com.UI.utils;

import com.UI.config.ConfigManager;
import com.UI.drivers.DriverManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

import static com.UI.constants.FrameworkConstants.POLLING_INTERVAL;

/**
 * Utility class for waiting operations
 */
public class WaitUtil {
    private static final Logger LOGGER = LoggerUtil.getLogger(WaitUtil.class);
    private static final ConfigManager CONFIG_MANAGER = ConfigManager.getInstance();
    private static final int DEFAULT_EXPLICIT_WAIT = CONFIG_MANAGER.getIntProperty("explicit.wait");
    private WaitUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get WebDriverWait instance with default timeout
     * @return WebDriverWait instance
     */
    public static WebDriverWait getWait() {
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(DEFAULT_EXPLICIT_WAIT));
    }

    /**
     * Get WebDriverWait instance with custom timeout
     * @param timeoutInSeconds Timeout in seconds
     * @return WebDriverWait instance
     */
    public static WebDriverWait getWait(int timeoutInSeconds) {
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutInSeconds));
    }

    /**
     * Get FluentWait instance
     * @param timeoutInSeconds Timeout in seconds
     * @return FluentWait instance
     */
    public static FluentWait<WebDriver> getFluentWait(int timeoutInSeconds) {
        return new FluentWait<>(DriverManager.getDriver())
                .withTimeout(Duration.ofSeconds(timeoutInSeconds))
                .pollingEvery(Duration.ofMillis(POLLING_INTERVAL))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    /**
     * Wait for element to be visible
     * @param element WebElement to wait for
     * @return WebElement that is visible
     */
    public static WebElement waitForElementVisible(WebElement element) {
        try {
            return getWait().until(ExpectedConditions.visibilityOf(element));
        } catch (TimeoutException e) {
            LOGGER.error("Element not visible after waiting: {}", element, e);
            throw e;
        }
    }

    /**
     * Wait for element to be visible
     * @param locator By locator to find element
     * @return WebElement that is visible
     */
    public static WebElement waitForElementVisible(By locator) {
        try {
            return getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            LOGGER.error("Element not visible after waiting: {}", locator, e);
            throw e;
        }
    }

    /**
     * Wait for element to be clickable
     * @param element WebElement to wait for
     * @return WebElement that is clickable
     */
    public static WebElement waitForElementClickable(WebElement element) {
        try {
            return getWait().until(ExpectedConditions.elementToBeClickable(element));
        } catch (TimeoutException e) {
            LOGGER.error("Element not clickable after waiting: {}", element, e);
            throw e;
        }
    }

    /**
     * Wait for element to be clickable
     * @param locator By locator to find element
     * @return WebElement that is clickable
     */
    public static WebElement waitForElementClickable(By locator) {
        try {
            return getWait().until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            LOGGER.error("Element not clickable after waiting: {}", locator, e);
            throw e;
        }
    }

    /**
     * Wait for element to be present
     * @param locator By locator to find element
     * @return WebElement that is present
     */
    public static WebElement waitForElementPresent(By locator) {
        try {
            return getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            LOGGER.error("Element not present after waiting: {}", locator, e);
            throw e;
        }
    }

    /**
     * Wait for custom condition
     * @param condition ExpectedCondition to wait for
     * @param <T> Return type of condition
     * @return Result of condition
     */
    public static <T> T waitForCondition(ExpectedCondition<T> condition) {
        try {
            return getWait().until(condition);
        } catch (TimeoutException e) {
            LOGGER.error("Condition not met after waiting: {}", condition, e);
            throw e;
        }
    }

    /**
     * Wait for page to load completely
     */
    public static void waitForPageLoad() {
        try {
            getWait().until(webDriver -> ((JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState").equals("complete"));
        } catch (TimeoutException e) {
            LOGGER.error("Page did not load completely after waiting", e);
            throw e;
        }
    }

    /**
     * Wait using fluent wait with custom function
     * @param timeoutInSeconds Timeout in seconds
     * @param function Function to evaluate
     * @param <T> Return type of function
     * @return Result of function
     */
    public static <T> T fluentWait(int timeoutInSeconds, Function<WebDriver, T> function) {
        try {
            return getFluentWait(timeoutInSeconds).until(function);
        } catch (TimeoutException e) {
            LOGGER.error("Fluent wait condition not met after {} seconds", timeoutInSeconds, e);
            throw e;
        }
    }

    /**
     * Wait for specified time
     * @param milliseconds Time to wait in milliseconds
     */
    public static void staticWait(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Thread interrupted during static wait", e);
        }
    }
}