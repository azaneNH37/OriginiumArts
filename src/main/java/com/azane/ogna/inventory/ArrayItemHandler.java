package com.azane.ogna.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @author azaneNH37 (2025/9/12)
 */
public class ArrayItemHandler implements IItemHandler
{
    private final ItemStack[] stacks;
    private final SlotType[] slotTypes;
    private final BlockEntity blockEntity;

    public ArrayItemHandler(BlockEntity blockEntity, ItemStack[] stacks)
    {
        this.blockEntity = blockEntity;
        this.stacks = stacks;
        this.slotTypes = new SlotType[stacks.length];
        Arrays.fill(slotTypes,SlotType.ANY);
    }

    public ArrayItemHandler setType(int slot, SlotType type)
    {
        if(slot >= 0 && slot < slotTypes.length)
            slotTypes[slot] = type;
        return this;
    }

    @Override
    public int getSlots() {return stacks.length;}
    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {return stacks[slot];}
    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        if(!SlotType.canInsert(slotTypes[slot]))
            return stack;
        if (stack.isEmpty())
            return ItemStack.EMPTY;
        ItemStack existing = stacks[slot];
        int limit = getSlotLimit(slot);
        if (!existing.isEmpty())
        {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;
            limit -= existing.getCount();
        }
        if (limit <= 0)
            return stack;
        boolean reachedLimit = stack.getCount() > limit;
        if (!simulate)
        {
            if (existing.isEmpty())
                stacks[slot] = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
            else
                existing.grow(reachedLimit ? limit : stack.getCount());
            blockEntity.setChanged();
        }
        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if(!SlotType.canExtract(slotTypes[slot]))
            return ItemStack.EMPTY;
        ItemStack existing = stacks[slot];
        if (existing.isEmpty())
            return ItemStack.EMPTY;
        int toExtract = Math.min(amount, existing.getMaxStackSize());
        if (existing.getCount() <= toExtract)
        {
            if (!simulate)
            {
                stacks[slot] = ItemStack.EMPTY;
                blockEntity.setChanged();
                return existing;
            }
            else
                return existing.copy();
        }
        else
        {
            if (!simulate)
            {
                stacks[slot] = ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract);
                blockEntity.setChanged();
            }
            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }
    @Override
    public int getSlotLimit(int slot) {return Math.min(stacks[slot].getMaxStackSize(), 64);}
    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {return true;}
}
