package com.qa.framework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Central configuration. Lookup order: JVM system property, then environment
 * variable (upper snake case), then config.properties.
 */
public final class ConfigManager {

    private static final Properties PROPS = load();

    private ConfigManager() {
    }

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream in = ConfigManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties not found on classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read config.properties", e);
        }
        return props;
    }

    public static String get(String key) {
        String value = System.getProperty(key);
        if (isBlank(value)) {
            value = System.getenv(toEnvName(key));
        }
        if (isBlank(value)) {
            value = PROPS.getProperty(key);
        }
        if (isBlank(value)) {
            throw new IllegalStateException("Missing configuration key: " + key);
        }
        return value.trim();
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static String baseUrl() {
        return get("baseUrl");
    }

    public static String apiBaseUrl() {
        return get("apiBaseUrl");
    }

    public static String browser() {
        return get("browser").toLowerCase();
    }

    /** gridUrl -> GRID_URL */
    private static String toEnvName(String key) {
        return key.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
