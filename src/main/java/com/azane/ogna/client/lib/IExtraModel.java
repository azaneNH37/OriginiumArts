package com.azane.ogna.client.lib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface IExtraModel
{
    ResourceLocation getGuiModel(ItemStack stack);
}
