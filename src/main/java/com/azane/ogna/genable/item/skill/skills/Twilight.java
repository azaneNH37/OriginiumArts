package com.azane.ogna.genable.item.skill.skills;

import com.azane.cjsop.annotation.JsonClassTypeBinder;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.combat.data.ArkDamageSource;
import com.azane.ogna.combat.data.CombatUnit;
import com.azane.ogna.combat.data.SelectorUnit;
import com.azane.ogna.genable.item.skill.DefaultSkillDataBase;
import com.azane.ogna.item.weapon.AttackType;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;

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
    public void onSkillStart(Level level, Player player, IOgnaWeapon weapon, ItemStack stack)
    {
        super.onSkillStart(level, player, weapon, stack);
        if(player instanceof LocalPlayer localPlayer)
            localPlayer.flashOnSetHealth = false;
    }

    @Override
    public void onSkillEnd(Level level, Player player, IOgnaWeapon weapon, ItemStack stack)
    {
        super.onSkillEnd(level, player, weapon, stack);
        if(player instanceof LocalPlayer localPlayer)
            localPlayer.flashOnSetHealth = true;
    }

    @Override
    public void onSkillTick(Level level, Player player, IOgnaWeapon weapon, ItemStack stack, boolean isOpen)
    {
        if(!isOpen)
            return;
        if(player instanceof ServerPlayer serverPlayer)
        {
            if(serverPlayer.gameMode.isCreative() || serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR)
                return;
            if(serverPlayer.tickCount % 20 == 0)
            {
                serverPlayer.setHealth(player.getHealth()-player.getMaxHealth()*0.01f);
            }
        }
        player.hurtTime = 0;
    }
}
