package com.azane.ogna.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Arrays;

/**
 * @author azaneNH37 (2025/9/12)
 */
public class ArrayContainer implements Container
{
    private final ItemStack[] stacks;
    private final BlockEntity blockEntity;

    public ArrayContainer(BlockEntity blockEntity, ItemStack[] stacks)
    {
        this.blockEntity = blockEntity;
        this.stacks = stacks;
    }

    @Override
    public int getContainerSize() {return stacks.length;}
    @Override
    public boolean isEmpty()
    {
        for(ItemStack itemstack : stacks)
            if (!itemstack.isEmpty())
                return false;
        return true;
    }
    @Override
    public ItemStack getItem(int pSlot) {
        return pSlot >= 0 && pSlot < stacks.length ? stacks[pSlot] : ItemStack.EMPTY;
    }
    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        ItemStack result = ContainerHelper.removeItem(Arrays.asList(stacks), pSlot, pAmount);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }
    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(Arrays.asList(stacks), pSlot);
    }
    @Override
    public void setItem(int pSlot, ItemStack pStack)
    {
        if (pSlot >= 0 && pSlot < stacks.length) {
            stacks[pSlot] = pStack;
            if (pStack.getCount() > this.getMaxStackSize()) {
                pStack.setCount(this.getMaxStackSize());
            }
            setChanged();
        }
    }
    @Override
    public void setChanged() {blockEntity.setChanged();}
    @Override
    public boolean stillValid(Player pPlayer) {
        return pPlayer.distanceToSqr(blockEntity.getBlockPos().getX() + 0.5, blockEntity.getBlockPos().getY() + 0.5, blockEntity.getBlockPos().getZ() + 0.5) <= 64.0;
    }
    @Override
    public void clearContent() {
        Arrays.fill(stacks, ItemStack.EMPTY);
        setChanged();
    }
}
