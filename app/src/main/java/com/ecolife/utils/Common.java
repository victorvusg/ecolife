package com.ecolife.utils;

import java.text.DecimalFormat;

public class Common {

    /**
     * Convert given double to strin
     * @param value
     * @return
     */
    public static String convertDataToText(double value) {
        if (value % 1 == 0) {
            return String.valueOf((int) value);
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#####.##");
            return decimalFormat.format(value);
        }
    }
}
