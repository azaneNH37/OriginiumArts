package com.azane.ogna.client.lib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * @author azaneNH37 (2025-07-29)
 */
public interface IExtraModel
{
    ResourceLocation getGuiModel(ItemStack stack);
}
