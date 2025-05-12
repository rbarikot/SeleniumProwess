package com.UI.utils;

import com.UI.drivers.DriverManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

public class ElementUtil {

    private static final Logger LOGGER = LoggerUtil.getLogger(ElementUtil.class);

    private ElementUtil() {
        // Private constructor to prevent instantiation
    }
    /**
     * Click on element with wait and retry
     * @param element WebElement to click
     */
    public static void click(WebElement element) {
        try {
            WaitUtil.waitForElementClickable(element).click();
            LOGGER.debug("Clicked on element: {}", element);
        } catch (StaleElementReferenceException e) {
            LOGGER.warn("StaleElementReferenceException on click, retrying...");
            WebElement refreshedElement = WaitUtil.waitForElementClickable(
                    DriverManager.getDriver().findElement(getByFromElement(element)));
            refreshedElement.click();
        } catch (ElementClickInterceptedException e) {
            LOGGER.warn("ElementClickInterceptedException, trying JavaScript click");
            clickUsingJavaScript(element);
        }
    }

    /**
     * Click element using JavaScript
     * @param element WebElement to click
     */
    public static void clickUsingJavaScript(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
            js.executeScript("arguments[0].click();", WaitUtil.waitForElementVisible(element));
            LOGGER.debug("Clicked on element using JavaScript: {}", element);
        } catch (Exception e) {
            LOGGER.error("Failed to click element using JavaScript", e);
            throw e;
        }
    }

    /**
     * Type text into element
     * @param element WebElement to type into
     * @param text Text to type
     */
    public static void sendKeys(WebElement element, String text) {
        try {
            WebElement visibleElement = WaitUtil.waitForElementVisible(element);
            visibleElement.clear();
            visibleElement.sendKeys(text);
            LOGGER.debug("Entered text '{}' into element: {}", text, element);
        } catch (StaleElementReferenceException e) {
            LOGGER.warn("StaleElementReferenceException on sendKeys, retrying...");
            WebElement refreshedElement = WaitUtil.waitForElementVisible(
                    DriverManager.getDriver().findElement(getByFromElement(element)));
            refreshedElement.clear();
            refreshedElement.sendKeys(text);
        }
    }

    /**
     * Clear element and type text using JavaScript
     * @param element WebElement to type into
     * @param text Text to type
     */
    public static void sendKeysUsingJavaScript(WebElement element, String text) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
            js.executeScript("arguments[0].value='';", element);
            js.executeScript("arguments[0].value=arguments[1];", element, text);
            LOGGER.debug("Entered text '{}' into element using JavaScript: {}", text, element);
        } catch (Exception e) {
            LOGGER.error("Failed to enter text using JavaScript", e);
            throw e;
        }
    }

    /**
     * Get text from element
     * @param element WebElement to get text from
     * @return Text of element
     */
    public static String getText(WebElement element) {
        try {
            return WaitUtil.waitForElementVisible(element).getText();
        } catch (StaleElementReferenceException e) {
            LOGGER.warn("StaleElementReferenceException on getText, retrying...");
            WebElement refreshedElement = WaitUtil.waitForElementVisible(
                    DriverManager.getDriver().findElement(getByFromElement(element)));
            return refreshedElement.getText();
        }
    }

    /**
     * Get value attribute from element
     * @param element WebElement to get value from
     * @return Value attribute of element
     */
    public static String getValue(WebElement element) {
        try {
            return WaitUtil.waitForElementVisible(element).getAttribute("value");
        } catch (StaleElementReferenceException e) {
            LOGGER.warn("StaleElementReferenceException on getValue, retrying...");
            WebElement refreshedElement = WaitUtil.waitForElementVisible(
                    DriverManager.getDriver().findElement(getByFromElement(element)));
            return refreshedElement.getAttribute("value");
        }
    }

    /**
     * Check if element is displayed
     * @param element WebElement to check
     * @return true if element is displayed, false otherwise
     */
    public static boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Check if element is enabled
     * @param element WebElement to check
     * @return true if element is enabled, false otherwise
     */
    public static boolean isEnabled(WebElement element) {
        try {
            return WaitUtil.waitForElementVisible(element).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if element is selected
     * @param element WebElement to check
     * @return true if element is selected, false otherwise
     */
    public static boolean isSelected(WebElement element) {
        try {
            return WaitUtil.waitForElementVisible(element).isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Select option by visible text
     * @param element Select element
     * @param visibleText Text to select
     */
    public static void selectByVisibleText(WebElement element, String visibleText) {
        try {
            Select select = new Select(WaitUtil.waitForElementVisible(element));
            select.selectByVisibleText(visibleText);
            LOGGER.debug("Selected option with text '{}' from dropdown", visibleText);
        } catch (Exception e) {
            LOGGER.error("Failed to select by visible text: {}", visibleText, e);
            throw e;
        }
    }

    /**
     * Select option by value
     * @param element Select element
     * @param value Value to select
     */
    public static void selectByValue(WebElement element, String value) {
        try {
            Select select = new Select(WaitUtil.waitForElementVisible(element));
            select.selectByValue(value);
            LOGGER.debug("Selected option with value '{}' from dropdown", value);
        } catch (Exception e) {
            LOGGER.error("Failed to select by value: {}", value, e);
            throw e;
        }
    }

    /**
     * Select option by index
     * @param element Select element
     * @param index Index to select
     */
    public static void selectByIndex(WebElement element, int index) {
        try {
            Select select = new Select(WaitUtil.waitForElementVisible(element));
            select.selectByIndex(index);
            LOGGER.debug("Selected option with index {} from dropdown", index);
        } catch (Exception e) {
            LOGGER.error("Failed to select by index: {}", index, e);
            throw e;
        }
    }

    /**
     * Hover over element
     * @param element WebElement to hover over
     */
    public static void hoverOver(WebElement element) {
        try {
            Actions actions = new Actions(DriverManager.getDriver());
            actions.moveToElement(WaitUtil.waitForElementVisible(element)).perform();
            LOGGER.debug("Hovered over element: {}", element);
        } catch (Exception e) {
            LOGGER.error("Failed to hover over element", e);
            throw e;
        }
    }

    /**
     * Scroll to element
     * @param element WebElement to scroll to
     */
    public static void scrollToElement(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
            js.executeScript("arguments[0].scrollIntoView(true);", element);
            WaitUtil.staticWait(300); // Small wait for scroll to complete
            LOGGER.debug("Scrolled to element: {}", element);
        } catch (Exception e) {
            LOGGER.error("Failed to scroll to element", e);
            throw e;
        }
    }

    /**
     * Get By locator from WebElement
     * This is a hacky way to get the By locator used for an element
     * @param element WebElement to get By from
     * @return By locator
     */
    private static By getByFromElement(WebElement element) {
        // This is a hacky way to get the By locator used for an element
        // It's not perfect but works in most cases
        String elementString = element.toString();
        if (elementString.contains("-> xpath:")) {
            String xpath = elementString.split("-> xpath: ")[1];
            xpath = xpath.substring(0, xpath.length() - 1); // Remove trailing ]
            return By.xpath(xpath);
        } else if (elementString.contains("-> css selector:")) {
            String css = elementString.split("-> css selector: ")[1];
            css = css.substring(0, css.length() - 1); // Remove trailing ]
            return By.cssSelector(css);
        } else if (elementString.contains("-> id:")) {
            String id = elementString.split("-> id: ")[1];
            id = id.substring(0, id.length() - 1); // Remove trailing ]
            return By.id(id);
        } else if (elementString.contains("-> name:")) {
            String name = elementString.split("-> name: ")[1];
            name = name.substring(0, name.length() - 1); // Remove trailing ]
            return By.name(name);
        } else if (elementString.contains("-> class name:")) {
            String className = elementString.split("-> class name: ")[1];
            className = className.substring(0, className.length() - 1); // Remove trailing ]
            return By.className(className);
        } else if (elementString.contains("-> link text:")) {
            String linkText = elementString.split("-> link text: ")[1];
            linkText = linkText.substring(0, linkText.length() - 1); // Remove trailing ]
            return By.linkText(linkText);
        } else if (elementString.contains("-> partial link text:")) {
            String partialLinkText = elementString.split("-> partial link text: ")[1];
            partialLinkText = partialLinkText.substring(0, partialLinkText.length() - 1); // Remove trailing ]
            return By.partialLinkText(partialLinkText);
        } else if (elementString.contains("-> tag name:")) {
            String tagName = elementString.split("-> tag name: ")[1];
            tagName = tagName.substring(0, tagName.length() - 1); // Remove trailing ]
            return By.tagName(tagName);
        } else {
            // Fallback - may not work in all cases
            LOGGER.warn("Could not determine By locator from element: {}", elementString);
            throw new IllegalArgumentException("Could not determine By locator from element: " + elementString);
        }
    }

    /**
     * Get all options from select element
     * @param element Select element
     * @return List of option texts
     */
    public static List<String> getSelectOptions(WebElement element) {
        Select select = new Select(WaitUtil.waitForElementVisible(element));
        List<WebElement> options = select.getOptions();
        List<String> optionTexts = new ArrayList<>();
        for (WebElement option : options) {
            optionTexts.add(option.getText());
        }
        return optionTexts;
    }


}
