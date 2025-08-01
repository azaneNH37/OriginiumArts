package com.azane.ogna.block.entity;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.gui.ldlib.extra.PredicateSlotWidget;
import com.azane.ogna.client.gui.ldlib.helper.UiHelper;
import com.azane.ogna.item.skill.OgnaSkill;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.registry.ModBlockEntity;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.azane.ogna.client.lib.RegexHelper.*;

public class InjectEPTBlockEntity extends BlockEntity implements Container,IUIHolder.BlockEntityUI, IAsyncAutoSyncBlockEntity, IAutoPersistBlockEntity, IManaged
{
    //===== LDLIB start ======
    //记得改MANAGED_FIELD_HOLDER的classType
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

    public static final Pattern TAB_GROUP_PT = startWith("tab.");
    public static final Pattern SKILL_GROUP_PT = startWith("skill.");

    @AllArgsConstructor
    public enum EPTOp
    {
        SKILL_IN(30, ProgressTexture.FillDirection.RIGHT_TO_LEFT),
        SKILL_OUT(30, ProgressTexture.FillDirection.LEFT_TO_RIGHT);

        public final int baseTick;
        public final ProgressTexture.FillDirection fillDirection;

        public static boolean isSkill(EPTOp op)
        {
            return op.equals(SKILL_IN) || op.equals(SKILL_OUT);
        }
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

    @Nullable
    private ProgressWidget skill_progressWidget;
    @Nullable
    private PredicateSlotWidget skill_weaponSlot;
    @Nullable
    private PredicateSlotWidget skill_skillSlot;

    private final List<SlotWidget> slots = new ArrayList<>();

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

        var tab = group.buttonGroup;
        var content = group.containerGroup;

        createSkillUIGroup(content,player);

        return ui;
    }

    private void createSkillUIGroup(WidgetGroup group,Player player)
    {
        var widgets = group.getWidgetsById(SKILL_GROUP_PT);

        skill_progressWidget = UiHelper.getAsNonnull(ProgressWidget.class,endWith("progress"),widgets);
        skill_progressWidget.setProgressSupplier(()-> inInject ?(double) curTick / (double) curOp.baseTick : 0);

        skill_weaponSlot = UiHelper.getAsNonnull(PredicateSlotWidget.class,endWith("slot.weapon"),widgets);
        skill_skillSlot = UiHelper.getAsNonnull(PredicateSlotWidget.class,endWith("slot.skill"),widgets);
        skill_weaponSlot.setContainerSlot(this, 0);
        skill_skillSlot.setContainerSlot(this,1);
        skill_weaponSlot.setPutPredicate(IOgnaWeapon::isWeapon);
        skill_skillSlot.setPutPredicate(OgnaSkill::isSkill);

        slots.add(skill_skillSlot);
        slots.add(skill_weaponSlot);

        var inButton = UiHelper.getAsNonnull(ButtonWidget.class,endWith("in"),widgets);
        var outButton = UiHelper.getAsNonnull(ButtonWidget.class,endWith("out"),widgets);
        inButton.setOnPressCallback(cd->{
            ItemStack weapon = skill_weaponSlot.getItem();
            ItemStack skill = skill_skillSlot.getItem();
            if(!inInject && IOgnaWeapon.isWeapon(weapon) && OgnaSkill.isSkill(skill))
            {
                IOgnaWeapon iOgnaWeapon = (IOgnaWeapon) weapon.getItem();
                if(!iOgnaWeapon.hasSkill(weapon))
                {
                    inInject = true;curOp = EPTOp.SKILL_IN;curTick = 0;
                    slots.forEach(slotWidget -> {
                        slotWidget.setCanPutItems(false);
                        slotWidget.setCanTakeItems(false);
                    });
                    if(skill_progressWidget != null)
                        skill_progressWidget.setFillDirection(curOp.fillDirection);
                }
            }
        });
        outButton.setOnPressCallback(cd->{
            ItemStack weapon = skill_weaponSlot.getItem();
            ItemStack skill = skill_skillSlot.getItem();
            if(!inInject && IOgnaWeapon.isWeapon(weapon) && skill.isEmpty())
            {
                IOgnaWeapon iOgnaWeapon = (IOgnaWeapon) weapon.getItem();
                if(iOgnaWeapon.hasSkill(weapon))
                {
                    inInject = true;curOp = EPTOp.SKILL_OUT;curTick = 0;
                    slots.forEach(slotWidget -> {
                        slotWidget.setCanPutItems(false);
                        slotWidget.setCanTakeItems(false);
                    });
                    if(skill_progressWidget != null)
                        skill_progressWidget.setFillDirection(curOp.fillDirection);
                }
            }
        });
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, InjectEPTBlockEntity pBlockEntity)
    {
        if(pBlockEntity.inInject)
        {
            pBlockEntity.curTick++;
            if(pBlockEntity.curTick >= pBlockEntity.curOp.baseTick)
            {
                if(EPTOp.isSkill(pBlockEntity.curOp))
                    onSkillOpEnd(pBlockEntity);
                onProgressEnd(pBlockEntity);
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
                onProgressEnd(pBlockEntity);
            }
        }
    }

    private static void onProgressEnd(InjectEPTBlockEntity pBlockEntity)
    {
        pBlockEntity.inInject = false;
        pBlockEntity.curTick = 0;
        pBlockEntity.slots.forEach(slotWidget -> {
            slotWidget.setCanPutItems(true);
            slotWidget.setCanTakeItems(true);
        });
    }

    private static void onSkillOpEnd(InjectEPTBlockEntity pBlockEntity)
    {
        if(pBlockEntity.skill_weaponSlot == null || pBlockEntity.skill_skillSlot == null)
            return;
        ItemStack weapon = pBlockEntity.skill_weaponSlot.getItem();
        ItemStack skill = pBlockEntity.skill_skillSlot.getItem();
        boolean available = IOgnaWeapon.isWeapon(weapon) && (pBlockEntity.curOp == EPTOp.SKILL_IN ? OgnaSkill.isSkill(skill) : skill.isEmpty());
        if(!available)
            return;
        IOgnaWeapon iOgnaWeapon = (IOgnaWeapon) weapon.getItem();
        if(pBlockEntity.curOp == EPTOp.SKILL_IN)
        {
            iOgnaWeapon.onSkillEquip(weapon,OgnaSkill.getSkillId(skill));
            pBlockEntity.skill_skillSlot.setItem(ItemStack.EMPTY);
        }
        else
        {
            pBlockEntity.skill_skillSlot.setItem(OgnaSkill.buildSkillStack(iOgnaWeapon.getSkillId(weapon)));
            iOgnaWeapon.onSkillUnequip(weapon);
        }
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
    public ItemStack getItem(int pSlot) {return stacks[pSlot];}

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {return ContainerHelper.removeItem(Arrays.stream(this.stacks).toList(), pSlot, pAmount);}

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {return ContainerHelper.takeItem(Arrays.stream(this.stacks).toList(), pSlot);}

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
