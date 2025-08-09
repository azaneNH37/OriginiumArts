package com.azane.ogna.lib;

import java.text.CompactNumberFormat;
import java.text.NumberFormat;
import java.util.Locale;

public final class NumStrHelper
{
    public static NumberFormat FORMAT1 = CompactNumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);
    public static NumberFormat FORMAT2 = CompactNumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);

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
}
