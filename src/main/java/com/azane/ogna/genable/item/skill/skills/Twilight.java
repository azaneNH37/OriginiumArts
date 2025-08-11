package com.azane.ogna.genable.item.skill.skills;

import com.azane.cjsop.annotation.JsonClassTypeBinder;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.combat.data.ArkDamageSource;
import com.azane.ogna.combat.data.CombatUnit;
import com.azane.ogna.combat.data.SelectorUnit;
import com.azane.ogna.genable.item.skill.DefaultSkillDataBase;
import com.azane.ogna.item.weapon.AttackType;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * @author azaneNH37 (2025-08-06)
 */
@JsonClassTypeBinder(fullName = "skill.twilight", namespace = OriginiumArts.MOD_ID)
public class Twilight extends DefaultSkillDataBase
{
    @Override
    public boolean onServerAttack(ServerLevel level, ServerPlayer player, IOgnaWeapon weapon, ItemStack stack, AttackType attackType, long chargeTime, boolean isOpen)
    {
        super.onServerAttack(level, player, weapon, stack, attackType, chargeTime, isOpen);
        return false;
    }

    @Override
    public void onImpactEntity(ServerLevel level, LivingEntity entity, CombatUnit combatUnit, SelectorUnit selectorUnit, ArkDamageSource damageSource)
    {
        //entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,600,4,true,true));
    }
}
