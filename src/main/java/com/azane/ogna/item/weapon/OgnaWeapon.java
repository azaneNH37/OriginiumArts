package com.azane.ogna.item.weapon;

import com.azane.ogna.client.lib.IDynamicAssetItem;
import com.azane.ogna.client.lib.IOffHandItem;
import com.azane.ogna.genable.item.base.IGenItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;

import java.util.List;

public abstract class OgnaWeapon extends Item implements GeoItem, IOffHandItem, IDynamicAssetItem, IGenItem,IOgnaWeapon
{
    public OgnaWeapon(Properties pProperties)
    {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced)
    {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.literal(this.getStackUUID(pStack)));
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
