package org.richard.utils;

public class IntegerUtils {

    public static int safeParse(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(value);
    }

}
