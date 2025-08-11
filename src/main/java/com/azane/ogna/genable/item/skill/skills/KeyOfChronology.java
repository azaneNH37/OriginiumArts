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
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,600,4,true,true));
    }
}
