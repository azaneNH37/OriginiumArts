package com.azane.ogna.genable.item.base;

import com.azane.ogna.OriginiumArts;
import lombok.AllArgsConstructor;

/**
 * 抄板子记得改这个，防mod间tag键冲突
 * @author azaneNH37 (2025-08-11)
 */
@AllArgsConstructor
public enum ModGenIdentifier
{
    MOD_ID(OriginiumArts.MOD_ID);

    private final String id;

    public static String getId() {return MOD_ID.id;}
}