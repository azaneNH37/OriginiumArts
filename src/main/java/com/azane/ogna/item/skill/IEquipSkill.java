package com.azane.ogna.item.skill;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;


/**
 * @author azaneNH37 (2025-08-01)
 */
public interface IEquipSkill
{
    boolean hasSkill(ItemStack stack);

    @Nullable
    ResourceLocation getSkillId(ItemStack stack);

    void tickSP(Level level, Player player, ItemStack stack);

    void tickDuration(Level level, Player player, ItemStack stack);

    void onItemStackTransform();

    boolean onSkillInvoke(Level level, Player player, ItemStack stack);

    void onSkillEquip(ItemStack stack, ResourceLocation rl);

    void onSkillUnequip(ItemStack stack);
}
