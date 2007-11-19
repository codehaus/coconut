package org.coconut.internal.util;

public class StringUtil {
    public static String capitalize(String capitalizeMe) {
        if (capitalizeMe == null) {
            throw new NullPointerException("capitalizeMe is null");
        } else if (capitalizeMe.length() == 0) {
            return capitalizeMe;
        }
        return capitalizeMe.substring(0, 1).toUpperCase() + capitalizeMe.substring(1);
    }
}
