package com.azane.ogna.item.skill;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


public interface IEquipSkill
{
    void tickSP(Level level, Player player, ItemStack stack);

    void tickDuration(Level level, Player player, ItemStack stack);

    void onItemStackTransform();

    void onSkillInvoke(Level level, Player player, ItemStack stack);

    void onSkillEquip(ItemStack stack, ResourceLocation rl);

    void onSkillUnequip(ItemStack stack);
}
