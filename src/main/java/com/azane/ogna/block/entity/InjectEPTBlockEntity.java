package com.azane.ogna.block.entity;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.gui.ldlib.custom.ChipWidget;
import com.azane.ogna.client.gui.ldlib.extra.PredicateSlotWidget;
import com.azane.ogna.client.gui.ldlib.helper.UiHelper;
import com.azane.ogna.combat.chip.ChipArg;
import com.azane.ogna.combat.chip.ChipSet;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.genable.item.chip.IChip;
import com.azane.ogna.genable.item.chip.ItemChip;
import com.azane.ogna.inventory.MenuItemDisplay;
import com.azane.ogna.item.OgnaChip;
import com.azane.ogna.item.skill.OgnaSkill;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.network.OgnmChannel;
import com.azane.ogna.network.to_client.SyncEPTWeaponStackCapPacket;
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
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Pattern;

import static com.azane.ogna.lib.RegexHelper.*;

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
    public static final Pattern CHIP_GROUP_PT = startWith("chip.");

    @AllArgsConstructor
    public enum EPTOp
    {
        SKILL_IN(30, ProgressTexture.FillDirection.RIGHT_TO_LEFT),
        SKILL_OUT(30, ProgressTexture.FillDirection.LEFT_TO_RIGHT),
        CHIP_IN(5, ProgressTexture.FillDirection.RIGHT_TO_LEFT),
        CHIP_OUT(5, ProgressTexture.FillDirection.LEFT_TO_RIGHT);

        public final int baseTick;
        public final ProgressTexture.FillDirection fillDirection;

        public static boolean isSkill(EPTOp op)
        {
            return op.equals(SKILL_IN) || op.equals(SKILL_OUT);
        }
        public static boolean isChip(EPTOp op)
        {
            return op.equals(CHIP_IN) || op.equals(CHIP_OUT);
        }
    }

    private final MenuItemDisplay chipListDisplay = new MenuItemDisplay();

    //TODO:不要用List不要用List不要用List不要用List不要用List不要用List!!!!!!!! 很好静默处理干掉我半天
    @DropSaved @DescSynced @Persisted
    private ItemStack[] stacks = new ItemStack[4];
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
    @Nullable
    private ProgressWidget chip_progressWidget;
    @Nullable
    private PredicateSlotWidget chip_weaponSlot;
    @Nullable
    private PredicateSlotWidget chip_chipSlot;

    @Nullable
    private DraggableScrollableWidgetGroup chipListWidget;
    @Nullable
    @Setter
    private ChipWidget selectedChipWidget;
    @Setter
    @Getter
    private boolean chipListDirty = true;

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
        createChipUIGroup(content,player);

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

    private void createChipUIGroup(WidgetGroup group,Player player)
    {
        var chipWidgets = group.getWidgetsById(CHIP_GROUP_PT);

        chip_progressWidget = UiHelper.getAsNonnull(ProgressWidget.class,endWith("progress"),chipWidgets);
        chip_progressWidget.setProgressSupplier(()-> inInject ?(double) curTick / (double) curOp.baseTick : 0);
        chip_weaponSlot = UiHelper.getAsNonnull(PredicateSlotWidget.class,endWith("slot.weapon"),chipWidgets);
        chip_chipSlot = UiHelper.getAsNonnull(PredicateSlotWidget.class,endWith("slot.chip"),chipWidgets);
        chip_weaponSlot.setContainerSlot(this, 2);
        chip_chipSlot.setContainerSlot(this,3);
        chip_weaponSlot.setPutPredicate(IOgnaWeapon::isWeapon);
        chip_chipSlot.setPutPredicate(OgnaChip::isChip);

        slots.add(chip_chipSlot);
        slots.add(chip_weaponSlot);

        var volumeIcon = UiHelper.getAsNonnull(ProgressWidget.class,endWith("volume.icon"),chipWidgets);
        var volumeText = UiHelper.getAsNonnull(TextTextureWidget.class,endWith("volume.text"),chipWidgets);
        chipListWidget = UiHelper.getAsNonnull(DraggableScrollableWidgetGroup.class,endWith("chip.list"),chipWidgets);

        volumeIcon.setProgressSupplier(()-> {
            if(chip_weaponSlot == null || chip_weaponSlot.getItem().isEmpty())
                return 0.0;
            int take = ChipSet.getVolumeTake(ChipArg.of(player, chip_weaponSlot.getItem()));
            int limit = ChipSet.getVolumeLimit(ChipArg.of(player, chip_weaponSlot.getItem()));
            volumeText.setText("%d/%d".formatted(take, limit));
            return limit == 0 ? 0.0 : (double) take / (double) limit;
        });

        var inButton = UiHelper.getAsNonnull(ButtonWidget.class,endWith("in"),chipWidgets);
        var outButton = UiHelper.getAsNonnull(ButtonWidget.class,endWith("out"),chipWidgets);

        inButton.setOnPressCallback(cd->{
            if(inInject)
                return;
            inInject = true;curOp = EPTOp.CHIP_IN;curTick = 0;
            slots.forEach(slotWidget -> {
                slotWidget.setCanPutItems(false);
                slotWidget.setCanTakeItems(false);
            });
            if(chip_progressWidget != null)
                chip_progressWidget.setFillDirection(curOp.fillDirection);

            if(cd.isRemote)
                return;

            ItemStack weapon = chip_weaponSlot.getItem();
            ItemStack chip = chip_chipSlot.getItem();
            if(IOgnaWeapon.isWeapon(weapon) && OgnaChip.isChip(chip))
            {
                IOgnaWeapon iOgnaWeapon = (IOgnaWeapon) weapon.getItem();
                ChipSet chipSet = iOgnaWeapon.getWeaponCap(weapon).getChipSet();
                IChip iChip = ((OgnaChip) chip.getItem()).getDataBaseForStack(chip);
                ChipArg arg = ChipArg.of(player, weapon);
                if(iChip.canPlugIn(chipSet,arg))
                {
                    DebugLogger.log("can Plug in");
                    chipSet.insertChip(iChip,arg);
                    chip.shrink(1);
                    setChipListDirty(true);
                }
            }
        });
        outButton.setOnPressCallback(cd->{
            if(inInject)
                return;
            inInject = true;curOp = EPTOp.CHIP_OUT;curTick = 0;
            slots.forEach(slotWidget -> {
                slotWidget.setCanPutItems(false);
                slotWidget.setCanTakeItems(false);
            });
            if(chip_progressWidget != null)
                chip_progressWidget.setFillDirection(curOp.fillDirection);

            if(cd.isRemote)
                return;

            ItemStack weapon = chip_weaponSlot.getItem();
            ItemStack chip = chip_chipSlot.getItem();
            if(IOgnaWeapon.isWeapon(weapon) && chip.isEmpty())
            {
                ChipSet chipSet = ((IOgnaWeapon) weapon.getItem()).getWeaponCap(weapon).getChipSet();
                ChipArg arg = ChipArg.of(player, weapon);
                if(chipListWidget != null && selectedChipWidget != null)
                {
                    IChip iChip = selectedChipWidget.getChip();
                    if(iChip != null)
                    {
                        chipSet.removeChip(iChip,arg);
                        chipSet.cleanUp();
                        chip_chipSlot.setItem(iChip.buildItemStack(1));
                        setChipListDirty(true);
                    }
                }
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void handleSync(SyncEPTWeaponStackCapPacket packet)
    {
        ItemStack weaponStack = stacks[packet.getSlotIndex()];
        if(IOgnaWeapon.isWeapon(weaponStack))
        {
            IOgnaWeapon iOgnaWeapon = (IOgnaWeapon) weaponStack.getItem();
            if(iOgnaWeapon.getStackUUID(weaponStack).equals(packet.getStackUUID().toString()))
            {
                iOgnaWeapon.getWeaponCap(weaponStack).deserializeNBT(packet.getCapNBT());
                DebugLogger.log("client sync weapon cap: {}, {}", iOgnaWeapon.getStackUUID(weaponStack), packet.getCapNBT().toString());
                cleanUpChipList();
            }
        }
    }

    private void cleanUpChipList()
    {
        if(chipListWidget == null)
            return;
        if(chip_weaponSlot == null || chip_weaponSlot.getItem().isEmpty())
            chipListWidget.clearAllWidgets();
        selectedChipWidget = null;
        ItemStack weaponStack = chip_weaponSlot.getItem();
        if(IOgnaWeapon.isWeapon(weaponStack))
        {
            IOgnaWeapon iOgnaWeapon = (IOgnaWeapon) weaponStack.getItem();
            ChipSet chipSet = iOgnaWeapon.getWeaponCap(weaponStack).getChipSet();
            if(chipSet != null)
            {
                DebugLogger.log("env:{},nbt:{}",this.level.isClientSide,chipSet.serializeNBT().toString());
                ChipWidget.refreshChipList(chipListWidget, chipSet, chipListDisplay, this::setSelectedChipWidget);
            }
            if(!this.level.isClientSide())
            {
                OgnmChannel.DEFAULT.sendToWithinRange(new SyncEPTWeaponStackCapPacket(
                    UUID.fromString(iOgnaWeapon.getOrCreateStackUUID(weaponStack)),
                    iOgnaWeapon.getWeaponCap(weaponStack).serializeNBT(),this.getBlockPos(), 2), (ServerLevel) this.level, this.getBlockPos(),16);
            }
        }
        else
            chipListWidget.clearAllWidgets();
        setChipListDirty(false);
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, InjectEPTBlockEntity pBlockEntity)
    {
        if(pBlockEntity.isChipListDirty())
            pBlockEntity.cleanUpChipList();
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
        if(pBlockEntity.isChipListDirty())
            pBlockEntity.cleanUpChipList();
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
    public ItemStack removeItem(int pSlot, int pAmount)
    {
        return ContainerHelper.removeItem(Arrays.stream(this.stacks).toList(), pSlot, pAmount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {return ContainerHelper.takeItem(Arrays.stream(this.stacks).toList(), pSlot);}

    @Override
    public void setItem(int pSlot, ItemStack pStack)
    {
        if(pSlot == 2 && !this.level.isClientSide())
        {
            setChipListDirty(true);
        }
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
