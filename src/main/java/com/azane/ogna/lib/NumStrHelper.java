package com.azane.ogna.lib;

import java.text.CompactNumberFormat;
import java.text.NumberFormat;
import java.util.Locale;

public final class NumStrHelper
{
    public static NumberFormat FORMAT = CompactNumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);

    static {
        FORMAT.setMinimumFractionDigits(1);
        FORMAT.setMaximumFractionDigits(1);
    }

    public static String raw(long num)
    {
        return String.valueOf(num);
    }
    public static String format(long num)
    {
        return FORMAT.format(num);
    }
}
