package com.azane.ogna.util;

import com.azane.ogna.registry.ModAttribute;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.Set;

/**
 * @author azaneNH37 (2025/8/26)
 */
public class SyncUtil
{
    public static final Set<Attribute> SYNC_ATTRIBUTES = Set.of(
        ModAttribute.WEAPON_ENERGY_CONSUME.get(),
        ModAttribute.WEAPON_ENERGY_STORE.get(),
        ModAttribute.WEAPON_RELOAD_CD.get(),
        ModAttribute.WEAPON_ATTACK_CD.get()
    );
}
