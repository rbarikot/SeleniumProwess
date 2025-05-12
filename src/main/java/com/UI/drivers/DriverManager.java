package com.UI.drivers;

import com.UI.config.ConfigManager;
import com.UI.utils.LoggerUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Manages WebDriver instances for test execution
 */
public class DriverManager {
    private static final Logger LOGGER = LoggerUtil.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();
    private static final ConfigManager CONFIG_MANAGER = ConfigManager.getInstance();

    private DriverManager() {
        // Private constructor to prevent instantiation
    }

    /**
     * Initialize WebDriver based on configuration
     * @return WebDriver instance
     */
    public static WebDriver initializeDriver() {
        String browser = CONFIG_MANAGER.getProperty("browser", "chrome").toLowerCase();
        String executionMode = CONFIG_MANAGER.getProperty("execution.mode", "local").toLowerCase();
        boolean headless = CONFIG_MANAGER.getBooleanProperty("headless");
        WebDriver driver;

        LOGGER.info("Initializing WebDriver: Browser={}, Mode={}, Headless={}", 
                    browser, executionMode, headless);

        try {
            switch (executionMode) {
                case "grid":
                case "remote":
                    driver = initializeRemoteDriver(browser, headless, executionMode);
                    break;
                default:
                    driver = initializeLocalDriver(browser, headless);
            }

            configureDriverTimeouts(driver);
            DRIVER_THREAD_LOCAL.set(driver);
            LOGGER.info("WebDriver initialized successfully");
            return driver;
        } catch (Exception e) {
            LOGGER.error("Failed to initialize WebDriver", e);
            throw new RuntimeException("Failed to initialize WebDriver", e);
        }
    }

    /**
     * Initialize local WebDriver
     * @param browser Browser name
     * @param headless Headless mode flag
     * @return WebDriver instance
     */
    private static WebDriver initializeLocalDriver(String browser, boolean headless) {
        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                }
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                return new ChromeDriver(chromeOptions);
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("--headless");
                }
                return new FirefoxDriver(firefoxOptions);
            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) {
                    edgeOptions.addArguments("--headless");
                }
                return new EdgeDriver(edgeOptions);
            case "safari":
                WebDriverManager.safaridriver().setup();
                return new SafariDriver();
            default:
                LOGGER.warn("Unsupported browser: {}. Using Chrome instead.", browser);
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver();
        }
    }

    /**
     * Initialize remote WebDriver
     * @param browser Browser name
     * @param headless Headless mode flag
     * @param executionMode Execution mode (grid or remote)
     * @return WebDriver instance
     * @throws MalformedURLException If remote URL is invalid
     */
    private static WebDriver initializeRemoteDriver(String browser, boolean headless, String executionMode) 
            throws MalformedURLException {
        String remoteUrl = executionMode.equals("grid") 
                ? CONFIG_MANAGER.getProperty("grid.url") 
                : CONFIG_MANAGER.getProperty("remote.url");
        System.out.println("+++++++++++++++++Remote URL+++++"+remoteUrl);
        switch (browser) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                }
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                return new RemoteWebDriver(new URL(remoteUrl), chromeOptions);
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("--headless");
                }
                return new RemoteWebDriver(new URL(remoteUrl), firefoxOptions);
            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) {
                    edgeOptions.addArguments("--headless");
                }
                return new RemoteWebDriver(new URL(remoteUrl), edgeOptions);
            default:
                LOGGER.warn("Unsupported browser for remote execution: {}. Using Chrome instead.", browser);
                ChromeOptions defaultOptions = new ChromeOptions();
                if (headless) {
                    defaultOptions.addArguments("--headless=new");
                }
                return new RemoteWebDriver(new URL(remoteUrl), defaultOptions);
        }
    }

    /**
     * Configure driver timeouts
     * @param driver WebDriver instance
     */
    private static void configureDriverTimeouts(WebDriver driver) {
        int implicitWait = CONFIG_MANAGER.getIntProperty("implicit.wait");
        int pageLoadTimeout = CONFIG_MANAGER.getIntProperty("page.load.timeout");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        driver.manage().window().maximize();
    }

    /**
     * Get current WebDriver instance
     * @return WebDriver instance
     */
    public static WebDriver getDriver() {
        return DRIVER_THREAD_LOCAL.get();
    }

    /**
     * Quit WebDriver and clear thread local
     */
    public static void quitDriver() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver != null) {
            driver.quit();
            DRIVER_THREAD_LOCAL.remove();
            LOGGER.info("WebDriver closed and removed from ThreadLocal");
        }
    }
}