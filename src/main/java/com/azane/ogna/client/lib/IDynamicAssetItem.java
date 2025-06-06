package com.azane.ogna.client.lib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface IDynamicAssetItem
{
    ResourceLocation getModel(ItemStack stack);
    ResourceLocation getTexture(ItemStack stack);
    ResourceLocation getAnimation(ItemStack stack);
    ResourceLocation getGuiModel(ItemStack stack);
}