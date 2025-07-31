package com.azane.ogna.block.entity;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.gui.ldlib.helper.UiHelper;
import com.azane.ogna.item.skill.OgnaSkill;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.registry.ModBlockEntity;
import com.azane.ogna.resource.service.ServerDataService;
import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.syncdata.IManaged;
import com.lowdragmc.lowdraglib.syncdata.IManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.DropSaved;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.blockentity.IAsyncAutoSyncBlockEntity;
import com.lowdragmc.lowdraglib.syncdata.blockentity.IAutoPersistBlockEntity;
import com.lowdragmc.lowdraglib.syncdata.field.FieldManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    @AllArgsConstructor
    public enum EPTOp
    {
        SKILL(30);

        public final int baseTick;
    }

    //TODO:不要用List不要用List不要用List不要用List不要用List不要用List!!!!!!!! 很好静默处理干掉我半天
    @DropSaved @DescSynced @Persisted
    private ItemStack[] stacks = new ItemStack[2];
    @DropSaved @Persisted
    private boolean inInject;
    @DropSaved @Persisted
    private EPTOp curOp;
    @DropSaved @Persisted
    private int curTick;
    @DropSaved @Persisted
    private ProgressTexture.FillDirection fillDirection;

    @Nullable
    private ProgressTexture progressTexture;
    private List<SlotWidget> activeSlots = new ArrayList<>();

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
        var progress = Optional.ofNullable((ProgressWidget)contents.getFirstWidgetById("progress")).orElseThrow();
        progressTexture = (ProgressTexture)progress.getBackgroundTexture();
        var weaponSlot = Optional.ofNullable((SlotWidget)contents.getFirstWidgetById("slot.weapon")).orElseThrow();
        var skillSlot = Optional.ofNullable((SlotWidget)contents.getFirstWidgetById("slot.skill")).orElseThrow();
        weaponSlot.setContainerSlot(this, 0);
        skillSlot.setContainerSlot(this,1);
        activeSlots.add(weaponSlot);
        activeSlots.add(skillSlot);
        var inButton = Optional.ofNullable((ButtonWidget)contents.getFirstWidgetById("in")).orElseThrow();
        inButton.setOnPressCallback(cd->{
           ItemStack weapon = weaponSlot.getItem();
           ItemStack skill = skillSlot.getItem();
           if(!inInject && IOgnaWeapon.isWeapon(weapon) && OgnaSkill.isSkill(skill))
           {
               IOgnaWeapon iOgnaWeapon = (IOgnaWeapon) weapon.getItem();
               if(iOgnaWeapon.getWeaponCap(weapon).getSkillCap().getSkill() == null)
               {
                   inInject = true;
                   curOp = EPTOp.SKILL;
                   curTick = 0;
                   fillDirection = ProgressTexture.FillDirection.RIGHT_TO_LEFT;
                   activeSlots.forEach(slotWidget -> {
                       slotWidget.setCanPutItems(false);
                       slotWidget.setCanTakeItems(false);
                   });
               }
           }
        });
        var outButton = Optional.ofNullable((ButtonWidget)contents.getFirstWidgetById("out")).orElseThrow();
        outButton.setOnPressCallback(cd->{
            ItemStack weapon = weaponSlot.getItem();
            ItemStack skill = skillSlot.getItem();
            if(!inInject && IOgnaWeapon.isWeapon(weapon) && skill.isEmpty())
            {
                IOgnaWeapon iOgnaWeapon = (IOgnaWeapon) weapon.getItem();
                if(iOgnaWeapon.getWeaponCap(weapon).getSkillCap().getSkill() != null)
                {
                    inInject = true;
                    curOp = EPTOp.SKILL;
                    curTick = 0;
                    fillDirection = ProgressTexture.FillDirection.LEFT_TO_RIGHT;
                    activeSlots.forEach(slotWidget -> {
                        slotWidget.setCanPutItems(false);
                        slotWidget.setCanTakeItems(false);
                    });
                }
            }
        });
        return ui;
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, InjectEPTBlockEntity pBlockEntity)
    {
        if(pBlockEntity.inInject)
        {
            pBlockEntity.curTick++;
            if(pBlockEntity.curTick >= pBlockEntity.curOp.baseTick)
            {
                if(pBlockEntity.curOp == EPTOp.SKILL)
                {
                    ItemStack weapon = pBlockEntity.stacks[0];
                    ItemStack skill = pBlockEntity.stacks[1];
                    if(pBlockEntity.fillDirection.equals(ProgressTexture.FillDirection.RIGHT_TO_LEFT))
                    {
                        if(IOgnaWeapon.isWeapon(weapon) && OgnaSkill.isSkill(skill))
                        {
                            IOgnaWeapon iOgnaWeapon = (IOgnaWeapon) weapon.getItem();
                            iOgnaWeapon.onSkillEquip(weapon,((OgnaSkill)skill.getItem()).getDataBaseForStack(skill).getId());
                            pBlockEntity.stacks[0] = weapon;
                            pBlockEntity.stacks[1] = ItemStack.EMPTY;
                        }
                    }
                    else
                    {
                        if(IOgnaWeapon.isWeapon(weapon) && skill.isEmpty())
                        {
                            IOgnaWeapon iOgnaWeapon = (IOgnaWeapon) weapon.getItem();
                            ResourceLocation skillRl = iOgnaWeapon.getWeaponCap(weapon).getSkillCap().getSkill().getId();
                            iOgnaWeapon.onSkillUnequip(weapon);
                            pBlockEntity.stacks[0] = weapon;
                            pBlockEntity.stacks[1] = ServerDataService.get().getSkill(skillRl).buildItemStack(1);
                        }
                    }
                }
                pBlockEntity.inInject = false;
                pBlockEntity.curTick = 0;
                pBlockEntity.activeSlots.forEach(slotWidget -> {
                    slotWidget.setCanPutItems(true);
                    slotWidget.setCanTakeItems(true);
                });
            }
            if(pBlockEntity.progressTexture != null)
            {
                pBlockEntity.progressTexture.setFillDirection(pBlockEntity.fillDirection);
                pBlockEntity.progressTexture.setProgress((float)pBlockEntity.curTick / (float)pBlockEntity.curOp.baseTick);
            }
        }
    }

    public static void clientTick(Level pLevel, BlockPos pPos, BlockState pState, InjectEPTBlockEntity pBlockEntity)
    {
        if(pBlockEntity.inInject)
        {
            pBlockEntity.curTick++;
            if(pBlockEntity.curTick >= pBlockEntity.curOp.baseTick)
            {
                pBlockEntity.inInject = false;
                pBlockEntity.curTick = 0;
                pBlockEntity.activeSlots.forEach(slotWidget -> {
                    slotWidget.setCanPutItems(true);
                    slotWidget.setCanTakeItems(true);
                });
            }
            if(pBlockEntity.progressTexture != null)
            {
                pBlockEntity.progressTexture.setFillDirection(pBlockEntity.fillDirection);
                pBlockEntity.progressTexture.setProgress((float)pBlockEntity.curTick / (float)pBlockEntity.curOp.baseTick);
            }
        }
    }


    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack)
    {
        return switch (pIndex)
        {
            case 0 -> IOgnaWeapon.isWeapon(pStack);
            case 1 -> OgnaSkill.isSkill(pStack);
            default -> false;
        };
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
