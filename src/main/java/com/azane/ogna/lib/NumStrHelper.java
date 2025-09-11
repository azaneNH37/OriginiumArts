package com.azane.ogna.lib;

import java.text.CompactNumberFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author azaneNH37 (2025-08-09)
 */
public final class NumStrHelper
{
    public static final NumberFormat FORMAT1 = CompactNumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);
    public static final NumberFormat FORMAT2 = CompactNumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);

    static {
        FORMAT1.setMinimumFractionDigits(1);
        FORMAT1.setMaximumFractionDigits(1);
        FORMAT2.setMinimumFractionDigits(2);
        FORMAT2.setMaximumFractionDigits(2);
    }

    public static String raw(long num)
    {
        return String.valueOf(num);
    }
    public static String format(long num)
    {
        return FORMAT1.format(num);
    }

    private static final String[] roman_symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
    private static final int[] roman_values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};

    /**
     * 罗马数字转换(范围1-3999)
     * @param num
     * @return
     */
    public static String roman(int num) {
        if (num < 1) {
            return "O";
        } else if (num > 3999) {
            return "ↁ";
        }

        StringBuilder roman = new StringBuilder();

        for (int i = 0; i < roman_values.length && num > 0; i++) {
            while (num >= roman_values[i]) {
                num -= roman_values[i];
                roman.append(roman_symbols[i]);
            }
        }

        return roman.toString();
    }
}
