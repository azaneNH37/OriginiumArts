package com.azane.ogna.lib;

import java.util.regex.Pattern;

/**
 * @author azaneNH37 (2025-08-02)
 */
public final class RegexHelper
{
    public static Pattern endWith(String raw)
    {
        return Pattern.compile(Pattern.quote(raw)+"$");
    }
    public static Pattern startWith(String raw)
    {
        return Pattern.compile("^"+Pattern.quote(raw));
    }
}