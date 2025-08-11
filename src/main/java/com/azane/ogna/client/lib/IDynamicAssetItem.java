package com.azane.ogna.client.lib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * @author azaneNH37 (2025-07-29)
 */
public interface IDynamicAssetItem extends IExtraModel
{
    ResourceLocation getModel(ItemStack stack);
    ResourceLocation getTexture(ItemStack stack);
    ResourceLocation getAnimation(ItemStack stack);
}