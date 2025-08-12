package com.azane.ogna.block.entity;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.gui.ldlib.custom.MaterialWidget;
import com.azane.ogna.client.gui.ldlib.custom.MaterialWidgetGroup;
import com.azane.ogna.client.gui.ldlib.custom.MenuItemWidget;
import com.azane.ogna.craft.rlr.RLRRecipe;
import com.azane.ogna.craft.rlr.RLRRecipes;
import com.azane.ogna.inventory.MenuItemDisplay;
import com.azane.ogna.client.gui.ldlib.helper.UiHelper;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.registry.ModBlockEntity;
import com.azane.ogna.registry.ModRecipe;
import com.azane.ogna.util.GeoAnimations;
import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.gui.widget.layout.Layout;
import com.lowdragmc.lowdraglib.syncdata.IManaged;
import com.lowdragmc.lowdraglib.syncdata.IManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.blockentity.IAsyncAutoSyncBlockEntity;
import com.lowdragmc.lowdraglib.syncdata.blockentity.IAutoPersistBlockEntity;
import com.lowdragmc.lowdraglib.syncdata.field.FieldManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.azane.ogna.lib.RegexHelper.*;

/**
 * @author azaneNH37 (2025-08-10)
 */
public class CraftOCCBlockEntity extends BlockEntity implements IUIHolder.BlockEntityUI, IAsyncAutoSyncBlockEntity, IAutoPersistBlockEntity, IManaged, GeoBlockEntity
{
    //===== LDLIB start ======
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(CraftOCCBlockEntity.class);
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
        controllers.add(new AnimationController<>(this,"misc",state -> {
            if(isOpen)
                return state.setAndContinue(GeoAnimations.MISC_WORK);
            else
                return state.setAndContinue(GeoAnimations.MISC_IDLE);
        }));
        controllers.add(new AnimationController<>(this,"move",state -> PlayState.STOP)
            .triggerableAnim("op.open",GeoAnimations.OP_OPEN)
            .triggerableAnim("op.close",GeoAnimations.OP_CLOSE));
    }
    //===== GeckoLib end =======

    private final MenuItemDisplay menuItemDisplay = new MenuItemDisplay();

    public CraftOCCBlockEntity(BlockPos pPos, BlockState pBlockState)
    {
        super(ModBlockEntity.CRAFT_OCC_ENTITY.get(), pPos, pBlockState);
    }

    @Override
    public ModularUI createUI(Player player)
    {
        var mui = new ModularUI(doCreateUI(player),this,player);
        if(player instanceof ServerPlayer serverPlayer)
        {
            triggerAnim("move","op.open");
            mui.registerCloseListener(()->triggerAnim("move","op.close"));
        }
        isOpen = true;
        mui.registerCloseListener(()->isOpen = false);
        return mui;
    }

    public void onPlayerUse(Player player)
    {
        if(player instanceof ServerPlayer serverPlayer)
        {
            if(isOpen)
                return;
            BlockEntityUIFactory.INSTANCE.openUI(this,serverPlayer);
        }
    }

    private WidgetGroup doCreateUI(Player player)
    {
        boolean isClient = player.level().isClientSide();
        WidgetGroup ui = Optional.ofNullable(UiHelper.getUISupplier(RlHelper.build(OriginiumArts.MOD_ID,"craft"),isClient)).orElseThrow().get();
        var craftButton = UiHelper.getAsNonnull(ButtonWidget.class, endWith("craft"), ui.widgets);
        craftButton.setOnPressCallback(cd->{});
        var mtrGroup = (MaterialWidgetGroup)ui.getFirstWidgetById("mtr.group");
        var resGroup = (DraggableScrollableWidgetGroup)ui.getFirstWidgetById("result.list");
        if(this.level != null && resGroup != null)
        {
            // DebugLogger.log("{}",this.level.getRecipeManager().getAllRecipesFor(ModRecipe.RL_RESULT_TYPE.get()).size());
            menuItemDisplay.clearContent();
            AtomicInteger index = new AtomicInteger(0);
            RLRRecipes.getRLRRecipeList(this.level)
                .forEach(rlrr-> {
                    var wg = new MenuItemWidget();
                    if(wg.injectRecipe(rlrr,menuItemDisplay,index.get()))
                    {
                        setMenuItemCallback(ui,wg,rlrr,player,menuItemDisplay,index.get());
                        resGroup.addWidget(wg);
                        index.incrementAndGet();
                    }
                });
            resGroup.setLayout(Layout.VERTICAL_CENTER);
            resGroup.setLayout(Layout.NONE);
            resGroup.computeMax();//TODO:WHY?????????
        }
        return ui;
    }

    private void refreshMtrGroup(MaterialWidgetGroup group, RLRRecipe recipe, Player player)
    {
        if(group == null || recipe == null || player == null) return;
        group.setMaterialAmt(recipe.getQuantifiedIngredients().size());
        var lis = group.getWidgetsById(MaterialWidgetGroup.CHILD_ID);
        for(int i = 0; i<Math.min(recipe.getQuantifiedIngredients().size(),lis.size()); i++)
        {
            int amt = recipe.getQuantifiedIngredients().get(i).getIngredientCount(player);
            ((MaterialWidget)lis.get(i)).injectIngredient(recipe.getQuantifiedIngredients().get(i),amt);
        }
    }

    private void setMenuItemCallback(WidgetGroup root, MenuItemWidget target, RLRRecipe recipe, Player player, Container displayContainer,int index)
    {
        var mtrGroup = (MaterialWidgetGroup)root.getFirstWidgetById("mtr.group");
        var craftButton = UiHelper.getAsNonnull(ButtonWidget.class, endWith("craft"), root.widgets);

        var button = UiHelper.getAsNonnull(ButtonWidget.class, endWith("button"), target.widgets);
        button.setOnPressCallback(cdt -> {
            target.displayInMainUI(root.getWidgetsById(startWith("display.item")),recipe,displayContainer,index);
            setCraftButtonCallback(root, craftButton, recipe, player);
            refreshMtrGroup(mtrGroup, recipe, player);
        });
    }

    private void setCraftButtonCallback(WidgetGroup root, ButtonWidget target, RLRRecipe recipe,Player player)
    {
        var mtrGroup = (MaterialWidgetGroup)root.getFirstWidgetById("mtr.group");
        target.setOnPressCallback(cd->{
            if(!recipe.canCraft(player))
                return;
            if(!cd.isRemote)
                recipe.craft(player);
            refreshMtrGroup(mtrGroup,recipe, player);
        });
    }
}
