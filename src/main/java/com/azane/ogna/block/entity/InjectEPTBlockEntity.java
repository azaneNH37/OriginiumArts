package com.azane.ogna.block.entity;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.gui.ldlib.helper.UiHelper;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.registry.ModBlockEntity;
import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.TabContainer;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.IManaged;
import com.lowdragmc.lowdraglib.syncdata.IManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.DropSaved;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.blockentity.IAsyncAutoSyncBlockEntity;
import com.lowdragmc.lowdraglib.syncdata.blockentity.IAutoPersistBlockEntity;
import com.lowdragmc.lowdraglib.syncdata.field.FieldManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.Optional;

public class InjectEPTBlockEntity extends BlockEntity implements Container,IUIHolder.BlockEntityUI, IAsyncAutoSyncBlockEntity, IAutoPersistBlockEntity, IManaged
{
    //===== LDLIB start ======
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(InjectEPTBlockEntity.class);
    @Getter
    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);
    @Override
    public IManagedStorage getRootStorage() {return getSyncStorage();}
    @Override
    public ManagedFieldHolder getFieldHolder() {return MANAGED_FIELD_HOLDER;}
    @Override
    public void onChanged() {setChanged();}
    //===== LDLIB end =======

    //TODO:不要用List不要用List不要用List不要用List不要用List不要用List!!!!!!!! 很好静默处理干掉我半天
    @DropSaved @DescSynced @Persisted
    private ItemStack[] stacks = new ItemStack[2];

    public InjectEPTBlockEntity(BlockPos pPos, BlockState pBlockState)
    {
        super(ModBlockEntity.INJECT_EPT_ENTITY.get(), pPos, pBlockState);
        Arrays.fill(stacks,ItemStack.EMPTY);
    }

    @Override
    public ModularUI createUI(Player player) {return new ModularUI(doCreateUI(player),this,player);}

    public void onPlayerUse(Player player)
    {
        if (player instanceof ServerPlayer serverPlayer) {
            BlockEntityUIFactory.INSTANCE.openUI(this, serverPlayer);
        }
    }

    private WidgetGroup doCreateUI(Player player)
    {
        boolean isClient = player.level().isClientSide();
        WidgetGroup ui = Optional.ofNullable(UiHelper.getUISupplier(RlHelper.build(OriginiumArts.MOD_ID,"inject"),isClient)).orElseThrow().get();
        var group = Optional.ofNullable((TabContainer)ui.getFirstWidgetById("group")).orElseThrow();
        var contents = group.containerGroup;
        var weaponSlot = Optional.ofNullable((SlotWidget)contents.getFirstWidgetById("slot.weapon")).orElseThrow();
        var skillSlot = Optional.ofNullable((SlotWidget)contents.getFirstWidgetById("slot.skill")).orElseThrow();
        weaponSlot.setContainerSlot(this, 0);
        skillSlot.setContainerSlot(this,1);
        return ui;
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
    public ItemStack getItem(int pSlot)
    {
        return stacks[pSlot];
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount)
    {
        return ContainerHelper.removeItem(Arrays.stream(this.stacks).toList(), pSlot, pAmount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot)
    {
        return ContainerHelper.takeItem(Arrays.stream(this.stacks).toList(), pSlot);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack)
    {
        this.stacks[pSlot] =  pStack;
        if (pStack.getCount() > this.getMaxStackSize()) {
            pStack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {return true;}
    @Override
    public void clearContent() {}
}
