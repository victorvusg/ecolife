package com.ecolife.utils;

import java.text.DecimalFormat;

public class Common {

    /**
     * Convert given double to string
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

    /**
     * This method uses to calculate percentage of a value
     * @param current Current value to calculate percentage
     * @param max Maximum value of value
     * @return integer value of percentage
     */
    public static int percentOf(double current, double max) {
        // If max is equal to 0, a invalid value then return 0
        if (max == 0) return 0;
        return (int) ((current / max) * 100);
    }
}
