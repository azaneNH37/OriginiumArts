package com.azane.ogna.genable.item.skill;

import com.azane.ogna.combat.data.ArkDamageSource;
import com.azane.ogna.combat.data.CombatUnit;
import com.azane.ogna.combat.data.SelectorUnit;
import com.azane.ogna.combat.data.skill.OgnaSkillData;
import com.azane.ogna.genable.data.AtkEntityData;
import com.azane.ogna.genable.data.display.SkillDisplayContext;
import com.azane.ogna.genable.item.base.IGenItemDatabase;
import com.azane.ogna.item.weapon.AttackType;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ISkill extends IGenItemDatabase
{
    SkillDisplayContext getDisplayContext();

    int getSkillLevel();

    OgnaSkillData getSkillData();

    AtkEntityData getAtkEntities();

    ResourceLocation getPrefixSkill();
    ResourceLocation getSuffixSkill();

    //双端均调用
    void onSkillTick(Level level, Player player, IOgnaWeapon weapon, ItemStack stack, boolean isOpen);

    void onSkillStart(Level level, Player player, IOgnaWeapon weapon, ItemStack stack);

    void onSkillEnd(Level level, Player player, IOgnaWeapon weapon,ItemStack stack);

    boolean onServerAttack(ServerLevel level, ServerPlayer player, IOgnaWeapon weapon, ItemStack stack, AttackType attackType, long chargeTime, boolean isOpen);

    void onImpactEntity(ServerLevel level, LivingEntity entity, CombatUnit combatUnit, SelectorUnit selectorUnit, ArkDamageSource damageSource);
}
