package com.UI.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class LoggerUtil {
    private static final ThreadLocal<Logger> threadLocalLogger = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> testLogs = new ThreadLocal<>();

    private LoggerUtil() {
        // Private constructor to prevent instantiation
    }

    public static Logger getLogger() {
        if (threadLocalLogger.get() == null) {
            String className = Thread.currentThread().getStackTrace()[2].getClassName();
            threadLocalLogger.set(LogManager.getLogger(className));
            testLogs.set(new ArrayList<>());
        }
        return threadLocalLogger.get();
    }

    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }

    public static void info(String message) {
        getLogger().info(message);
        addToTestLogs("INFO: " + message);
    }

    public static void warn(String message) {
        getLogger().warn(message);
        addToTestLogs("WARN: " + message);
    }

    public static void error(String message) {
        getLogger().error(message);
        addToTestLogs("ERROR: " + message);
    }

    public static void error(String message, Throwable throwable) {
        getLogger().error(message, throwable);
        addToTestLogs("ERROR: " + message + "\n" + throwable.toString());
    }

    public static void debug(String message) {
        getLogger().debug(message);
        addToTestLogs("DEBUG: " + message);
    }

    private static void addToTestLogs(String message) {
        if (testLogs.get() == null) {
            testLogs.set(new ArrayList<>());
        }
        testLogs.get().add(message);
    }

    public static String getTestLogs() {
        if (testLogs.get() == null) {
            return "";
        }
        return String.join("\n", testLogs.get());
    }

    public static void clearThreadLocalLogger() {
        threadLocalLogger.remove();
        testLogs.remove();
    }

}
