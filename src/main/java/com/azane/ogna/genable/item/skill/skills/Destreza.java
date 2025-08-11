package com.azane.ogna.genable.item.skill.skills;

import com.azane.cjsop.annotation.JsonClassTypeBinder;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.capability.skill.ISkillCap;
import com.azane.ogna.combat.data.ArkDamageSource;
import com.azane.ogna.combat.data.CombatUnit;
import com.azane.ogna.combat.data.MoveUnit;
import com.azane.ogna.combat.data.SelectorUnit;
import com.azane.ogna.combat.util.CombatFirer;
import com.azane.ogna.combat.util.SelectRule;
import com.azane.ogna.genable.item.skill.DefaultSkillDataBase;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.network.OgnmChannel;
import com.azane.ogna.network.to_client.FxEntityEffectTriggerPacket;
import com.azane.ogna.registry.ModEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;

/**
 * @author azaneNH37 (2025-08-09)
 */
@JsonClassTypeBinder(fullName = "skill.destreza", namespace = OriginiumArts.MOD_ID)
public class Destreza extends DefaultSkillDataBase
{
    @Override
    public void onImpactEntity(ServerLevel level, LivingEntity entity, CombatUnit combatUnit, SelectorUnit selectorUnit, ArkDamageSource damageSource)
    {
        entity.addEffect(new MobEffectInstance(ModEffect.SAND_POTION.get(),63,1));
        OgnmChannel.DEFAULT.sendToWithinRange(
            new FxEntityEffectTriggerPacket(RlHelper.parse("ognmarts:sand_poison"),entity.getId(),false),
            level,
            entity.getOnPos(),
            128
        );
    }
}
