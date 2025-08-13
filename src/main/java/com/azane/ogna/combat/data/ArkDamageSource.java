package com.azane.ogna.combat.data;

import com.azane.ogna.combat.attr.AttrMatrix;
import com.azane.ogna.combat.util.DmgCategory;
import com.azane.ogna.registry.ModAttribute;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author azaneNH37 (2025-07-20)
 */
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
        AttrMatrix matrix = AttrMatrix.combine(true,
            unit.getMatrices().get(Attributes.ATTACK_DAMAGE),
            unit.getCategory() == DmgCategory.PHYSICS ? unit.getMatrices().get(ModAttribute.DAMAGE_PHYSICS.get()) :
            unit.getCategory() == DmgCategory.ARTS ? unit.getMatrices().get(ModAttribute.DAMAGE_ARTS.get()) :
                AttrMatrix.UNIT_MATRIX
        );
        return (float) matrix.submit(unit.getBaseVal());
    }
}
