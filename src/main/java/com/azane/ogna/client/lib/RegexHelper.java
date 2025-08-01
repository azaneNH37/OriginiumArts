package com.azane.ogna.client.lib;

import java.util.regex.Pattern;

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