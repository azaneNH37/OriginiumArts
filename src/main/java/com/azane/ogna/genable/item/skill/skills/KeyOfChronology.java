package com.azane.ogna.genable.item.skill.skills;

import com.azane.cjsop.annotation.JsonClassTypeBinder;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.combat.data.ArkDamageSource;
import com.azane.ogna.combat.data.CombatUnit;
import com.azane.ogna.combat.data.SelectorUnit;
import com.azane.ogna.genable.item.skill.DefaultSkillDataBase;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * @author azaneNH37 (2025-07-29)
 */
@JsonClassTypeBinder(fullName = "skill.key_of_chronology", namespace = OriginiumArts.MOD_ID)
public class KeyOfChronology extends DefaultSkillDataBase
{
    @Override
    public void onImpactEntity(ServerLevel level, LivingEntity entity, CombatUnit combatUnit, SelectorUnit selectorUnit, ArkDamageSource damageSource)
    {
        var attacker = damageSource.getEntity();
        if (attacker != null)
        {
            double deltaX = entity.getX() - attacker.getX();
            double deltaZ = entity.getZ() - attacker.getZ();
            double knockbackStrength = 0.2D;
            entity.knockback(knockbackStrength, deltaX, deltaZ);
        }
    }
}
