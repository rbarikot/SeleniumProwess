package com.UI.pages;

import com.UI.config.ConfigManager;
import com.UI.drivers.DriverManager;
import com.UI.utils.ElementUtil;
import com.UI.utils.LoggerUtil;
import com.UI.utils.WaitUtil;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Base page class for all page objects
 */
public abstract class BasePage {
    protected WebDriver driver;
    protected final Logger LOGGER = LoggerUtil.getLogger(this.getClass());
    protected final ConfigManager CONFIG_MANAGER = ConfigManager.getInstance();
    protected final String BASE_URL = CONFIG_MANAGER.getProperty("base.url");
    /**
     * Constructor for BasePage
     */
    public BasePage() {
        this.driver = DriverManager.getDriver();
        //PageFactory.initElements(driver, this);
        LOGGER.debug("Initialized page object: {}", this.getClass().getSimpleName());
    }

    /**
     * Get page title
     * @return Page title
     */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Get current URL
     * @return Current URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Navigate to URL
     * @param url URL to navigate to
     */
    public void navigateTo(String url) {
        driver.get(url);
        WaitUtil.waitForPageLoad();
        LOGGER.info("Navigated to URL: {}", url);
    }

    /**
     * Navigate to base URL
     */
    public void navigateToBaseUrl() {
        navigateTo(BASE_URL);
    }

    /**
     * Find element by locator
     * @param locator By locator
     * @return WebElement
     */
    protected WebElement findElement(By locator) {
        return WaitUtil.waitForElementPresent(locator);
    }

    /**
     * Find elements by locator
     * @param locator By locator
     * @return List of WebElements
     */
    protected List<WebElement> findElements(By locator) {
        WaitUtil.waitForCondition(ExpectedConditions.presenceOfElementLocated(locator));
        return driver.findElements(locator);
    }

    /**
     * Click on element
     * @param element WebElement to click
     */
    protected void click(WebElement element) {
        ElementUtil.click(element);
    }

    /**
     * Click on element by locator
     * @param locator By locator for element to click
     */
    protected void click(By locator) {
        click(findElement(locator));
    }

    /**
     * Type text into element
     * @param element WebElement to type into
     * @param text Text to type
     */
    protected void sendKeys(WebElement element, String text) {
        ElementUtil.sendKeys(element, text);
    }

    /**
     * Type text into element by locator
     * @param locator By locator for element to type into
     * @param text Text to type
     */
    protected void sendKeys(By locator, String text) {
        sendKeys(findElement(locator), text);
    }

    /**
     * Get text from element
     * @param element WebElement to get text from
     * @return Text of element
     */
    protected String getText(WebElement element) {
        return ElementUtil.getText(element);
    }

    /**
     * Get text from element by locator
     * @param locator By locator for element to get text from
     * @return Text of element
     */
    protected String getText(By locator) {
        return getText(findElement(locator));
    }

    /**
     * Check if element is displayed
     * @param element WebElement to check
     * @return true if element is displayed, false otherwise
     */
    protected boolean isDisplayed(WebElement element) {
        return ElementUtil.isDisplayed(element);
    }

    /**
     * Check if element is displayed by locator
     * @param locator By locator for element to check
     * @return true if element is displayed, false otherwise
     */
    protected boolean isDisplayed(By locator) {
        try {
            return findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Wait for element to be visible
     * @param element WebElement to wait for
     * @return WebElement that is visible
     */
    protected WebElement waitForElementVisible(WebElement element) {
        return WaitUtil.waitForElementVisible(element);
    }

    /**
     * Wait for element to be visible by locator
     * @param locator By locator for element to wait for
     * @return WebElement that is visible
     */
    protected WebElement waitForElementVisible(By locator) {
        return WaitUtil.waitForElementVisible(locator);
    }

    /**
     * Wait for element to be clickable
     * @param element WebElement to wait for
     * @return WebElement that is clickable
     */
    protected WebElement waitForElementClickable(WebElement element) {
        return WaitUtil.waitForElementClickable(element);
    }

    /**
     * Wait for element to be clickable by locator
     * @param locator By locator for element to wait for
     * @return WebElement that is clickable
     */
    protected WebElement waitForElementClickable(By locator) {
        return WaitUtil.waitForElementClickable(locator);
    }
}