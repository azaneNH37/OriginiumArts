package com.azane.ogna.combat.data;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * @author azaneNH37 (2025-07-20)
 */
public class ArkDamageSource extends DamageSource
{
    public ArkDamageSource(Holder<DamageType> damageTypeHolder, @Nullable Entity pDirectEntity, @Nullable Entity pCausingEntity, @Nullable Vec3 pDamageSourcePosition)
    {
        super(damageTypeHolder, pDirectEntity, pCausingEntity, pDamageSourcePosition);
    }
}
