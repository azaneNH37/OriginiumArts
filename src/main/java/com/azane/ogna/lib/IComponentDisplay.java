package com.azane.ogna.lib;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * @author azaneNH37 (2025-07-13)
 */
public interface IComponentDisplay
{
    void appendHoverText(ItemStack stack, List<Component> tooltip, TooltipFlag flag);
}
