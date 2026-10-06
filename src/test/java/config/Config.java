package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = loadProperties();

    private Config() {
    }

    public static String getEnv() {
        return getSystemOrConfigValue("env", "local");
    }

    public static String getBaseUrl() {
        String env = getEnv();
        return getSystemOrConfigValue("base.url", getProperty("base.url." + env, ""));
    }

    public static String getRequiredRoute(String key) {
        return getProperty(key, "");
    }

    public static boolean isBaseUrlConfigured() {
        String baseUrl = getBaseUrl();
        return baseUrl != null && !baseUrl.isBlank();
    }

    public static boolean isRelaxedHttpsValidationEnabled() {
        return Boolean.parseBoolean(getSystemOrConfigValue("ssl.relaxed", "false"));
    }

    private static String getSystemOrConfigValue(String key, String defaultValue) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        String environmentValue = System.getenv(toEnvironmentVariableName(key));
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }

        return getProperty(key, defaultValue);
    }

    private static String getProperty(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    private static String toEnvironmentVariableName(String key) {
        return key.toUpperCase().replace('.', '_');
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load " + CONFIG_FILE, exception);
        }
        return properties;
    }
}
