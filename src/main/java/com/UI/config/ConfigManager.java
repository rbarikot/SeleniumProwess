package com.UI.config;

import org.apache.logging.log4j.Logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import com.UI.utils.LoggerUtil;
import static com.UI.constants.FrameworkConstants.CONFIG_FILE_PATH;

/**
 * Configuration manager to read properties from config file
 */
public class ConfigManager {
    private static final Logger LOGGER = LoggerUtil.getLogger(ConfigManager.class);
    private static final Properties PROPERTIES = new Properties();
    private static ConfigManager instance;

    private ConfigManager() {
        loadProperties();
    }

    /**
     * Singleton instance getter
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    /**
     * Load properties from the configuration file
     */
    private void loadProperties() {
        try (InputStream inputStream = new FileInputStream(CONFIG_FILE_PATH)) {
            PROPERTIES.load(inputStream);
            LOGGER.info("Configuration properties loaded successfully");
        } catch (IOException e) {
            LOGGER.error("Failed to load configuration properties", e);
            throw new RuntimeException("Failed to load configuration properties", e);
        }
    }

    /**
     * Get a property value by key
     */
    public String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }

    /**
     * Get a property value with a default fallback
     */
    public String getProperty(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    /**
     * Get integer property
     */
    public int getIntProperty(String key) {
        String value = getProperty(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LOGGER.error("Failed to parse integer property: " + key, e);
            throw new RuntimeException("Failed to parse integer property: " + key, e);
        }
    }

    /**
     * Get boolean property
     */
    public boolean getBooleanProperty(String key) {
        String value = getProperty(key);
        return Boolean.parseBoolean(value);
    }
}