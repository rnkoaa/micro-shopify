package org.richard;

public class IntegerUtils {

    static int safeParse(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(value);
    }

}
