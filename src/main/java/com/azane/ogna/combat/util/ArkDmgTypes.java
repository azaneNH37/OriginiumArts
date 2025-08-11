package com.azane.ogna.combat.util;

import com.azane.ogna.OriginiumArts;
import static com.azane.ogna.lib.RegistryAccessHelper.*;
import com.azane.ogna.lib.RlHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

/**
 * @author azaneNH37 (2025-08-09)
 */
public final class ArkDmgTypes
{
    public static final ResourceKey<DamageType> DEFAULT = ResourceKey.create(Registries.DAMAGE_TYPE, RlHelper.build(OriginiumArts.MOD_ID,"ark_default"));
    public static final ResourceKey<DamageType> DOT = ResourceKey.create(Registries.DAMAGE_TYPE, RlHelper.build(OriginiumArts.MOD_ID,"ark_dot"));


    public static Holder<DamageType> getHolder(ResourceKey<DamageType> type,boolean isClient)
    {
        return (isClient ? clientRegistryAccess() : serverRegistryAccess()).registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type);
    }
}
