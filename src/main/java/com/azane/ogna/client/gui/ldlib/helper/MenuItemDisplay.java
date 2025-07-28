package com.azane.ogna.client.gui.ldlib.helper;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 一个仅用于存储显示物品的container，不进行任何数据同步和持久化存储
 */
public class MenuItemDisplay implements Container
{
    private final List<ItemStack> display = new ArrayList<>();

    @Override
    public int getContainerSize()
    {
        return display.size();
    }

    @Override
    public boolean isEmpty()
    {
        return display.isEmpty();
    }

    @Override
    public ItemStack getItem(int pSlot)
    {
        return getContainerSize() <= pSlot ? ItemStack.EMPTY : display.get(pSlot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {return ItemStack.EMPTY;}

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {return ItemStack.EMPTY;}

    @Override
    public void setItem(int pSlot, ItemStack pStack)
    {
        while(getContainerSize() <= pSlot)
            display.add(ItemStack.EMPTY);
        display.set(pSlot,pStack);
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(Player pPlayer) {return false;}

    @Override
    public void clearContent()
    {
        display.clear();
    }
}
