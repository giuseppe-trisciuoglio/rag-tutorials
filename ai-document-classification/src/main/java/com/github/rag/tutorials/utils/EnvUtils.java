package com.github.rag.tutorials.utils;

public final class EnvUtils {
    private EnvUtils() {
        throw new IllegalStateException("Utility class");
    }
    
    public static String getEnv(String key) {
        String value = System.getenv(key);
        if (isEmpty(value)) {
            throw new IllegalArgumentException("Environment variable " + key + " is not set");
        }
        return value;
    }

    public static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return isEmpty(value) ? defaultValue : value;
    }
    
    public static boolean getEnv(String key, boolean defaultValue) {
        String value = System.getenv(key);
        return isEmpty(value) ? defaultValue : Boolean.parseBoolean(value);
    }

    public static boolean getBooleanEnv(String key) {
        String value = System.getenv(key);
        if (isEmpty(value)) {
            throw new IllegalArgumentException("Environment variable " + key + " is not set");
        }
        return Boolean.parseBoolean(value);
    }

    private static boolean isEmpty(String value) {
        return value == null || value.isEmpty() || value.isBlank();
    }
}
