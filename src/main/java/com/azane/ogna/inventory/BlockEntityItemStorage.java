package com.azane.ogna.inventory;

import com.azane.ogna.debug.log.DebugLogger;
import com.lowdragmc.lowdraglib.syncdata.IManaged;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.DropSaved;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.FieldManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntityItemStorage implements Container, IManaged
{
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(BlockEntityItemStorage.class);
    @Getter
    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);
    @Override
    public ManagedFieldHolder getFieldHolder() {return MANAGED_FIELD_HOLDER;}
    @Override
    public void onChanged() {
        DebugLogger.log("itemStorage changed.");
        blockEntity.setChanged();}

    protected final BlockEntity blockEntity;
    @Persisted @DropSaved @DescSynced
    protected NonNullList<ItemStack> stacks;

    public BlockEntityItemStorage(BlockEntity blockEntity, int size)
    {
        this.blockEntity = blockEntity;
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    public int getContainerSize() {return stacks.size();}

    @Override
    public boolean isEmpty()
    {
        for(ItemStack itemstack : this.stacks)
            if (!itemstack.isEmpty())
                return false;
        return true;
    }

    @Override
    public ItemStack getItem(int pSlot)
    {
        return stacks.get(pSlot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount)
    {
        //DebugLogger.log("Remove item from slot " + pSlot + " with amount " + pAmount);
        return ContainerHelper.removeItem(this.stacks, pSlot, pAmount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot)
    {
        //DebugLogger.log("Removing item noupdate from slot " + pSlot);
        return ContainerHelper.takeItem(this.stacks, pSlot);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack)
    {
        //DebugLogger.log("Setting item in slot " + pSlot + ": " + pStack);
        this.stacks.set(pSlot, pStack);
        if (pStack.getCount() > this.getMaxStackSize()) {
            pStack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    public void setChanged() {
        blockEntity.setChanged();
    }
    @Override
    public boolean stillValid(Player pPlayer) {return true;}
    @Override
    public void clearContent()
    {
        stacks.replaceAll(ignored -> ItemStack.EMPTY);
    }
}
