package com.azane.ogna.block.entity;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.gui.ldlib.helper.UiHelper;
import com.azane.ogna.craft.catalyst.CatalystRequirement;
import com.azane.ogna.craft.catalyst.CatalystScanner;
import com.azane.ogna.craft.oe.OECRecipe;
import com.azane.ogna.craft.oe.OEGRecipe;
import com.azane.ogna.craft.oe.RecipeFinder;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.inventory.ArrayContainer;
import com.azane.ogna.inventory.ArrayItemHandler;
import com.azane.ogna.inventory.SlotType;
import com.azane.ogna.lib.NumStrHelper;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.registry.ModBlockEntity;
import com.azane.ogna.registry.ModFluid;
import com.azane.ogna.util.GeoAnimations;
import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.ProgressWidget;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
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
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

import static com.azane.ogna.lib.RegexHelper.*;

/**
 * 增强的能量方块实体，支持催化剂系统
 * @author azaneNH37 (2025-09-12)
 */
public class EnergyEHBlockEntity extends BlockEntity implements IUIHolder.BlockEntityUI, IAsyncAutoSyncBlockEntity, IAutoPersistBlockEntity, IManaged, GeoBlockEntity {

    //===== LDLIB start ======
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(EnergyEHBlockEntity.class);
    @Getter
    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);
    @Override
    public IManagedStorage getRootStorage() {return getSyncStorage();}
    @Override
    public ManagedFieldHolder getFieldHolder() {return MANAGED_FIELD_HOLDER;}
    @Override
    public void onChanged() {setChanged();}
    //===== LDLIB end =======

    //===== GeckoLib start ======
    @Getter
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private boolean isOpen;
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this,"misc", state -> {
            if(isOpen)
                return state.setAndContinue(GeoAnimations.MISC_WORK);
            else
                return state.setAndContinue(GeoAnimations.MISC_IDLE);
        }));
    }
    //===== GeckoLib end =======

    public static final int MAX_ENERGY = 10000;

    @Getter
    private final FluidTank energyTank = new FluidTank(MAX_ENERGY) {
        @Override
        public boolean isFluidValid(FluidStack stack) {return stack.getFluid() == ModFluid.SOURCE_ORIGINIUM_ENERGY.get();}
        @Override
        protected void onContentsChanged() {EnergyEHBlockEntity.this.setChanged();}
    };

    public double getEnergy() {return syncEnergy;}

    @DescSynced
    private double syncEnergy = 0D;

    @DropSaved @DescSynced @Persisted
    private ItemStack[] stacks = new ItemStack[2];

    // 工作状态
    @Getter @DescSynced @Persisted
    private WorkMode currentMode = WorkMode.IDLE;
    @Getter @DescSynced @Persisted
    private int processTime = 0;
    @Getter @DescSynced @Persisted
    private int maxProcessTime = 0;

    // 配方缓存
    private OEGRecipe cachedOEGRecipe;
    private OECRecipe cachedOECRecipe;
    private CatalystRequirement cachedCatalystProvide = new CatalystRequirement();
    private ItemStack lastInputStack = ItemStack.EMPTY;

    // 缓存失效标记
    private boolean recipeCacheInvalid = true;

    private ProgressWidget energyBar;

    public enum WorkMode {
        IDLE,       // 空闲
        GENERATING, // 发电模式
        CRAFTING    // 制造模式
    }

    public EnergyEHBlockEntity(BlockPos pPos, BlockState pBlockState)
    {
        super(ModBlockEntity.ENERGY_EH_ENTITY.get(), pPos, pBlockState);
        Arrays.fill(stacks, ItemStack.EMPTY);
    }

    @Override
    public ModularUI createUI(Player player) {
        var mui = new ModularUI(doCreateUI(player),this,player);
        isOpen = true;
        mui.registerCloseListener(()->isOpen = false);
        return mui;
    }

    public void onPlayerUse(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            if(isOpen) return;
            BlockEntityUIFactory.INSTANCE.openUI(this, serverPlayer);
        }
    }

    private WidgetGroup doCreateUI(Player player) {
        boolean isClient = player.level().isClientSide();
        WidgetGroup ui = Optional.ofNullable(UiHelper.getUISupplier(RlHelper.build(OriginiumArts.MOD_ID,"energy"),isClient)).orElseThrow().get();

        var inSlot = UiHelper.getAsNonnull(SlotWidget.class,startWith("in"),ui.widgets);
        var outSlot = UiHelper.getAsNonnull(SlotWidget.class,startWith("out"),ui.widgets);
        energyBar = UiHelper.getAsNonnull(ProgressWidget.class,startWith("energy"),ui.widgets);
        var progressBar = UiHelper.getAsNonnull(ProgressWidget.class,startWith("progress"),ui.widgets);

        inSlot.setContainerSlot(container,0);
        outSlot.setContainerSlot(container,1);
        energyBar.setDynamicHoverTips(d->Component.translatable("ogna.gui.energy.energy",
            NumStrHelper.FORMAT2.format(syncEnergy),
            NumStrHelper.FORMAT2.format(MAX_ENERGY)).getString());
        energyBar.setProgressSupplier(()-> (double) energyTank.getFluidAmount() / MAX_ENERGY);
        progressBar.setProgressSupplier(()-> maxProcessTime > 0 ? (double)processTime / maxProcessTime : 0.0);

        return ui;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, EnergyEHBlockEntity blockEntity){}

    // ===== 主要逻辑 =====
    public static void serverTick(Level level, BlockPos pos, BlockState state, EnergyEHBlockEntity blockEntity) {
        if (level.isClientSide) return;
        blockEntity.tick();
    }

    private void tick() {
        boolean dirty = false;

        // 检查配方缓存是否需要刷新
        if (shouldRefreshRecipeCache()) {
            refreshRecipeCache();
            dirty = true;
        }

        // 确定工作模式
        WorkMode newMode = determineWorkMode();
        if (newMode != currentMode) {
            currentMode = newMode;
            processTime = 0;
            maxProcessTime = 0;
            dirty = true;
        }

        // 根据模式执行相应逻辑
        switch (currentMode) {
            case GENERATING:
                dirty |= processEnergyGeneration();
                break;
            case CRAFTING:
                dirty |= processEnergyCrafting();
                break;
            case IDLE:
                if (processTime > 0 || maxProcessTime > 0) {
                    processTime = 0;
                    maxProcessTime = 0;
                    dirty = true;
                }
                break;
        }

        syncEnergy = energyTank.getFluidAmount();
        if (dirty) {
            setChanged();
        }
    }

    private boolean shouldRefreshRecipeCache() {
        ItemStack inputStack = stacks[0];

        if (recipeCacheInvalid || !ItemStack.isSameItem(inputStack, lastInputStack)) {
            return true;
        }

        return level != null && level.getGameTime() % 20 == 0;
    }

    private void refreshRecipeCache() {
        ItemStack inputStack = stacks[0];
        lastInputStack = inputStack.copy();
        recipeCacheInvalid = false;

        if (inputStack.isEmpty() || level == null) {
            cachedOEGRecipe = null;
            cachedOECRecipe = null;
            return;
        }

        // 查找最佳配方（考虑催化剂）
        cachedCatalystProvide = new CatalystRequirement(CatalystScanner.scanCatalysts(level, getBlockPos()));
        cachedOEGRecipe = RecipeFinder.findBestOEGRecipe(level, new OEGRecipe.ProcessContext(stacks[0], cachedCatalystProvide));
        cachedOECRecipe = RecipeFinder.findBestOECRecipe(level, new OECRecipe.ProcessContext(stacks[0], stacks[1], energyTank.getFluidAmount(), cachedCatalystProvide));
    }

    private WorkMode determineWorkMode() {
        ItemStack inputStack = stacks[0];

        if (inputStack.isEmpty()) {
            return WorkMode.IDLE;
        }

        // 优先级：制造 > 发电 > 空闲
        if (cachedOECRecipe != null && canStartCrafting()) {
            return WorkMode.CRAFTING;
        } else if (cachedOEGRecipe != null && canStartGenerating()) {
            return WorkMode.GENERATING;
        } else {
            return WorkMode.IDLE;
        }
    }

    private boolean canStartGenerating() {
        return cachedOEGRecipe != null &&
            energyTank.getFluidAmount() < MAX_ENERGY &&
            cachedOEGRecipe.canProcess(new OEGRecipe.ProcessContext(stacks[0], cachedCatalystProvide));
    }

    private boolean canStartCrafting() {
        return cachedOECRecipe != null &&
            cachedOECRecipe.canProcess(new OECRecipe.ProcessContext(stacks[0], stacks[1], energyTank.getFluidAmount(), cachedCatalystProvide));
    }

    private boolean processEnergyGeneration() {
        if (cachedOEGRecipe == null || !canStartGenerating()) {
            return false;
        }

        if (maxProcessTime == 0) {
            maxProcessTime = cachedOEGRecipe.getProcessingTime();
        }

        processTime++;

        if (processTime >= maxProcessTime) {
            // 完成发电过程
            stacks[0].shrink(cachedOEGRecipe.getIngredient().getCount());

            // 注入流体
            FluidStack energyFluid = new FluidStack(ModFluid.SOURCE_ORIGINIUM_ENERGY.get(),
                (int) cachedOEGRecipe.getEnergyOutput());
            energyTank.fill(energyFluid, IFluidHandler.FluidAction.EXECUTE);

            processTime = 0;
            maxProcessTime = 0;
            recipeCacheInvalid = true; // 标记需要重新检查配方
            return true;
        }

        return processTime % 5 == 0; // 每5tick同步一次进度
    }

    private boolean processEnergyCrafting() {
        if (cachedOECRecipe == null || !canStartCrafting()) {
            return false;
        }

        if (maxProcessTime == 0) {
            maxProcessTime = cachedOECRecipe.getProcessingTime();
        }

        processTime++;

        if (processTime >= maxProcessTime) {
            // 完成制造过程
            stacks[0].shrink(cachedOECRecipe.getIngredient().getCount());

            // 消耗流体
            energyTank.drain((int) cachedOECRecipe.getEnergyCost(), IFluidHandler.FluidAction.EXECUTE);

            // 输出结果
            ItemStack result = cachedOECRecipe.getResult().copy();
            if (stacks[1].isEmpty()) {
                stacks[1] = result;
            } else {
                stacks[1].grow(result.getCount());
            }

            processTime = 0;
            maxProcessTime = 0;
            recipeCacheInvalid = true; // 标记需要重新检查配方
            return true;
        }

        return processTime % 5 == 0; // 每5tick同步一次进度
    }

    // ===== Custom Persist Methods =====
    @Override
    public void saveCustomPersistedData(CompoundTag tag, boolean forDrop) {
        CompoundTag fluidTag = new CompoundTag();
        energyTank.writeToNBT(fluidTag);
        tag.put("energy", fluidTag);
    }

    @Override
    public void loadCustomPersistedData(CompoundTag tag) {
        if (tag.contains("energy")) {
            CompoundTag fluidTag = tag.getCompound("energy");
            energyTank.readFromNBT(fluidTag);
        }
        recipeCacheInvalid = true; // 加载后需要重新检查配方
    }

    //===== Container methods =====
    public final Container container = new ArrayContainer(this, stacks);

    // ==== Forge ItemHandler methods ====
    private final IItemHandler itemHandler = new ArrayItemHandler(this, stacks)
        .setType(0, SlotType.INPUT)
        .setType(1, SlotType.OUTPUT);

    // ==== Forge capabilities methods ====
    private final LazyOptional<IItemHandler> itemHandlerLazy = LazyOptional.of(() -> itemHandler);
    private final LazyOptional<IFluidHandler> fluidHandlerLazy = LazyOptional.of(() -> energyTank);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerLazy.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandlerLazy.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandlerLazy.invalidate();
        fluidHandlerLazy.invalidate();
    }
}