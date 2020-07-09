package com.lifengdi.gateway.utils;

public class EnvironmentUtils {

    private static String applicationName;

    private static String env;

    public static String getApplicationName() {
        return applicationName;
    }

    public static void setApplicationName(String applicationName) {
        EnvironmentUtils.applicationName = applicationName;
    }

    public static String getEnv() {
        return env;
    }

    public static void setEnv(String env) {
        EnvironmentUtils.env = env;
    }

    public static String getAppEnv() {
        return applicationName + "[" + env + "]";
    }
}
