package com.azane.ogna.util;

import com.azane.ogna.registry.ModAttribute;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Set;

/**
 * @author azaneNH37 (2025/8/26)
 */
public class ModUtil
{
    public static final Set<Attribute> SYNC_ATTRIBUTES = Set.of(
        ModAttribute.WEAPON_ENERGY_CONSUME.get(),
        ModAttribute.WEAPON_ENERGY_STORE.get(),
        ModAttribute.WEAPON_RELOAD_CD.get(),
        ModAttribute.WEAPON_ATTACK_CD.get()
    );

    public static final Set<Attribute> COMBAT_ATTRIBUTES = Set.of(
        Attributes.ATTACK_DAMAGE,
        ModAttribute.EFFECT_LEVEL.get(),
        ModAttribute.EFFECT_TICK.get(),
        ModAttribute.DAMAGE_PHYSICS.get(),
        ModAttribute.DAMAGE_ARTS.get()
    );
}
