package org.richard.utils;

public class Strings {

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNotNullOrEmpty(String value) {
        return !isNullOrEmpty(value);
    }

}
