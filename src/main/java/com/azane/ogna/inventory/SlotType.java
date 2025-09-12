package com.azane.ogna.inventory;

/**
 * @author azaneNH37 (2025/9/12)
 */
public enum SlotType
{
    INPUT,
    OUTPUT,
    ANY,
    NONE;

    public static boolean canInsert(SlotType type)
    {
        return type == INPUT || type == ANY;
    }
    public static boolean canExtract(SlotType type)
    {
        return type == OUTPUT || type == ANY;
    }
}
