package com.azane.ogna.block.entity;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.gui.ldlib.helper.UiHelper;
import com.azane.ogna.craft.oe.OECRecipe;
import com.azane.ogna.craft.oe.OEGRecipe;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.lib.NumStrHelper;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.registry.ModBlockEntity;
import com.azane.ogna.registry.ModRecipe;
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
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
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

public class EnergyEHBlockEntity extends BlockEntity implements IUIHolder.BlockEntityUI, IAsyncAutoSyncBlockEntity, IAutoPersistBlockEntity, IManaged, GeoBlockEntity
{
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

    public static final double MAX_ENERGY = 1e4;

    // Getter方法供UI使用
    @Getter
    @DropSaved
    @DescSynced
    @Persisted
    private double energy;
    @DropSaved
    @DescSynced
    @Persisted
    private ItemStack[] stacks = new ItemStack[2];

    // 工作状态
    @Getter
    @DescSynced @Persisted
    private WorkMode currentMode = WorkMode.IDLE;
    @Getter
    @DescSynced @Persisted
    private int processTime = 0;
    @Getter
    @DescSynced @Persisted
    private int maxProcessTime = 0;

    // 配方缓存
    private OEGRecipe cachedOEGRecipe;
    private OECRecipe cachedOECRecipe;
    private ItemStack lastInputStack = ItemStack.EMPTY;

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
    public ModularUI createUI(Player player)
    {
        var mui = new ModularUI(doCreateUI(player),this,player);
        isOpen = true;
        mui.registerCloseListener(()->isOpen = false);
        return mui;
    }

    public void onPlayerUse(Player player)
    {
        if (player instanceof ServerPlayer serverPlayer) {
            if(isOpen)
                return;
            BlockEntityUIFactory.INSTANCE.openUI(this, serverPlayer);
        }
    }

    private WidgetGroup doCreateUI(Player player)
    {
        boolean isClient = player.level().isClientSide();
        WidgetGroup ui = Optional.ofNullable(UiHelper.getUISupplier(RlHelper.build(OriginiumArts.MOD_ID,"energy"),isClient)).orElseThrow().get();

        var inSlot = UiHelper.getAsNonnull(SlotWidget.class,startWith("in"),ui.widgets);
        var outSlot = UiHelper.getAsNonnull(SlotWidget.class,startWith("out"),ui.widgets);
        energyBar = UiHelper.getAsNonnull(ProgressWidget.class,startWith("energy"),ui.widgets);
        var progressBar = UiHelper.getAsNonnull(ProgressWidget.class,startWith("progress"),ui.widgets);

        inSlot.setContainerSlot(container,0);
        outSlot.setContainerSlot(container,1);
        energyBar.setDynamicHoverTips(p->Component.translatable("ogna.gui.energy.energy", NumStrHelper.FORMAT2.format(energy), NumStrHelper.FORMAT2.format(MAX_ENERGY)).getString());
        energyBar.setProgressSupplier(()-> energy / MAX_ENERGY);
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

        // 1. 确定工作模式
        WorkMode newMode = determineWorkMode();
        if (newMode != currentMode) {
            currentMode = newMode;
            processTime = 0;
            maxProcessTime = 0;
            dirty = true;
        }

        // 2. 根据模式执行相应逻辑
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

        if (dirty) {
            setChanged();
        }
    }

    private WorkMode determineWorkMode() {
        ItemStack inputStack = stacks[0];
        ItemStack outputStack = stacks[1];

        // 优先级：制造 > 发电 > 空闲
        if (canStartCrafting(inputStack, outputStack)) {
            return WorkMode.CRAFTING;
        } else if (canStartGenerating(inputStack)) {
            return WorkMode.GENERATING;
        } else {
            return WorkMode.IDLE;
        }
    }

    private boolean canStartGenerating(ItemStack inputStack) {
        if (inputStack.isEmpty()) return false;

        OEGRecipe recipe = findOEGRecipe(inputStack);
        return recipe != null && recipe.canProcess(inputStack) && energy < MAX_ENERGY;
    }

    private boolean canStartCrafting(ItemStack inputStack, ItemStack outputStack) {
        if (inputStack.isEmpty()) return false;

        OECRecipe recipe = findOECRecipe(inputStack);
        return recipe != null && recipe.canProcess(inputStack, outputStack, energy);
    }

    private boolean processEnergyGeneration() {
        ItemStack inputStack = stacks[0];
        OEGRecipe recipe = findOEGRecipe(inputStack);

        if (recipe == null || !recipe.canProcess(inputStack) || energy >= MAX_ENERGY) {
            return false;
        }

        if (maxProcessTime == 0) {
            maxProcessTime = recipe.getProcessingTime();
        }

        processTime++;

        if (processTime >= maxProcessTime) {
            // 完成发电过程
            inputStack.shrink(recipe.getIngredient().getCount());
            energy = Math.min(energy + recipe.getEnergyOutput(), MAX_ENERGY);
            //refreshEnergyBar();

            processTime = 0;
            maxProcessTime = 0;
            return true;
        }

        return processTime % 5 == 0; // 每5tick同步一次进度
    }

    private boolean processEnergyCrafting() {
        ItemStack inputStack = stacks[0];
        ItemStack outputStack = stacks[1];
        OECRecipe recipe = findOECRecipe(inputStack);

        if (recipe == null || !recipe.canProcess(inputStack, outputStack, energy)) {
            return false;
        }

        if (maxProcessTime == 0) {
            maxProcessTime = recipe.getProcessingTime();
        }

        processTime++;

        if (processTime >= maxProcessTime) {
            // 完成制造过程
            inputStack.shrink(recipe.getIngredient().getCount());
            energy -= recipe.getEnergyCost();
            //refreshEnergyBar();

            ItemStack result = recipe.getResult().copy();
            if (outputStack.isEmpty()) {
                stacks[1] = result;
            } else {
                outputStack.grow(result.getCount());
            }

            processTime = 0;
            maxProcessTime = 0;
            return true;
        }

        return processTime % 5 == 0; // 每5tick同步一次进度
    }

    private OEGRecipe findOEGRecipe(ItemStack inputStack) {
        if (!ItemStack.isSameItem(inputStack, lastInputStack)) {
            cachedOEGRecipe = null;
            cachedOECRecipe = null;
            lastInputStack = inputStack.copy();
        }

        if (cachedOEGRecipe == null && level != null) {
            RecipeManager recipeManager = level.getRecipeManager();
            cachedOEGRecipe = recipeManager.getAllRecipesFor(ModRecipe.OEG_TYPE.get())
                .stream()
                .filter(recipe -> recipe.canProcess(inputStack))
                .findFirst()
                .orElse(null);
        }

        return cachedOEGRecipe;
    }

    private OECRecipe findOECRecipe(ItemStack inputStack) {
        if (!ItemStack.isSameItem(inputStack, lastInputStack)) {
            cachedOEGRecipe = null;
            cachedOECRecipe = null;
            lastInputStack = inputStack.copy();
        }

        if (cachedOECRecipe == null && level != null) {
            RecipeManager recipeManager = level.getRecipeManager();
            cachedOECRecipe = recipeManager.getAllRecipesFor(ModRecipe.OEC_TYPE.get())
                .stream()
                .filter(recipe -> recipe.canProcess(inputStack, stacks[1], energy))
                .findFirst()
                .orElse(null);
        }

        return cachedOECRecipe;
    }

    //===== Container methods =====
    public final Container container = new Container()
    {
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
        public void setChanged() {EnergyEHBlockEntity.this.setChanged();}
        @Override
        public boolean stillValid(Player pPlayer) {
            return pPlayer.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
        }
        @Override
        public void clearContent() {
            Arrays.fill(stacks, ItemStack.EMPTY);
            setChanged();
        }
    };

    // ==== Container methods end =====

    // ==== Forge ItemHandler methods ====
    //TODO:等把ldlib的自动持久化修了一切都会好起来的
    private final IItemHandler itemHandler = new IItemHandler()
    {
        @Override
        public int getSlots() {return 2;}
        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {return stacks[slot];}
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
        {
            DebugLogger.log("slot:{}, stack:{},simulate:{}", slot, stack,simulate);
            if(slot == 1)
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
                setChanged();
            }
            return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            if(slot == 0)
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
                    setChanged();
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
                    setChanged();
                }
                return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
            }
        }
        @Override
        public int getSlotLimit(int slot) {return Math.min(stacks[slot].getMaxStackSize(), 64);}
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {return true;}
    };
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        handler.invalidate();
    }
    // ==== Forge ItemHandler methods end ====
}