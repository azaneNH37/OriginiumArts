package com.azane.ogna.debug.log;

import lombok.Getter;

@Getter
public enum LogLv
{
    DEBUG("DEBUG"),
    INFO("INFO"),
    WARN("WARN"),
    ERROR("ERROR"),
    FATAL("FATAL"),
    NULL("NULL"),
    REDUCED("REDUCED");

    private final String level;

    LogLv(String level)
    {
        this.level = level;
    }

    @Override
    public String toString()
    {
        return level;
    }
}
