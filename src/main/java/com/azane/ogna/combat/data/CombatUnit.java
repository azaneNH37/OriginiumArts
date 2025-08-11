package com.azane.ogna.combat.data;

import com.azane.ogna.combat.attr.AttrMap;
import com.azane.ogna.combat.util.DmgCategory;
import com.azane.ogna.combat.util.OnImpactEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author azaneNH37 (2025-07-24)
 */
@AllArgsConstructor(staticName = "of")
@Getter
public class CombatUnit
{
    private final Holder<DamageType> damageType;
    private final double baseVal;
    private final AttrMap.Matrices matrices;
    private final DmgCategory category;
    private final List<OnImpactEntity> impacts;

    public void onHitEntity(ServerLevel level,LivingEntity entity,SelectorUnit unit,ArkDamageSource damageSource)
    {
        impacts.forEach(on-> {
            if (on != null)
                on.impact(level, entity, this, unit, damageSource);
        });
        entity.hurt(damageSource,damageSource.submitSidedVal());
    }
}
