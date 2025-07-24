package com.azane.ogna.combat.util;

import com.azane.ogna.combat.data.ArkDamageSource;
import com.azane.ogna.combat.data.CombatUnit;
import com.azane.ogna.combat.data.SelectorUnit;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface OnImpactEntity
{
    void impact(ServerLevel level, LivingEntity entity, CombatUnit combatUnit, SelectorUnit selectorUnit, ArkDamageSource damageSource);
}
