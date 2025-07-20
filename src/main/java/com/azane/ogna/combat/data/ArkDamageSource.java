package com.azane.ogna.combat.data;

import com.azane.ogna.combat.attr.AttrMatrix;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ArkDamageSource extends DamageSource
{
    private final CombatUnit unit;

    public ArkDamageSource(CombatUnit unit, @Nullable Entity pDirectEntity, @Nullable Entity pCausingEntity, @Nullable Vec3 pDamageSourcePosition)
    {
        super(unit.getDamageType(), pDirectEntity, pCausingEntity, pDamageSourcePosition);
        this.unit = unit;
    }

    public float submitSidedVal()
    {
        return (float) Optional.ofNullable(unit.getMatrices().get(Attributes.ATTACK_DAMAGE))
            .orElse(AttrMatrix.UNIT_MATRIX).submit(unit.getBaseVal());
    }
}
