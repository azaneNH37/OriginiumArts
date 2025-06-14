package com.azane.ogna.item.genable;

import com.azane.ogna.client.lib.IDynamicAssetItem;
import com.azane.ogna.client.lib.IOffHandItem;
import com.azane.ogna.genable.item.base.IGenItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;

public abstract class OgnaWeapon extends Item implements GeoItem, IOffHandItem, IDynamicAssetItem, IGenItem,IOgnaWeapon
{
    public OgnaWeapon(Properties pProperties)
    {
        super(pProperties);
    }

    @Override
    public @Nullable ItemStack templateBuildItemStack(CompoundTag tag, int count)
    {
        ItemStack stack = IGenItem.super.templateBuildItemStack(tag, count);
        if(stack == null)
            return null;
        this.getOrCreateStackUUID(stack);
        return stack;
    }
}
