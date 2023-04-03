package com.ecolife.utils;
import android.graphics.Color;
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
        if (max == 0) return 0;
        return (int) ((current / max) * 100);
    }

    /**
     * Get color by percentage, show color according to range of percentage
     * @param percentage
     * @return
     */
    public static int getColorByPercentage(int percentage) {
        if (percentage < 20) {
            return Color.parseColor("#1DE9B6");
        } else if (percentage < 50) {
            return Color.parseColor("#FFEE58");
        } else if (percentage < 100 ) {
            return Color.parseColor("#F57C00");
        } else {
            return Color.parseColor("#B00020");
        }
    }
}
