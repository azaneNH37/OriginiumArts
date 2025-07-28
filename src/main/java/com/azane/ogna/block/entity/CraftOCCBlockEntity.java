package com.azane.ogna.block.entity;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.gui.ldlib.custom.MaterialWidget;
import com.azane.ogna.client.gui.ldlib.custom.MaterialWidgetGroup;
import com.azane.ogna.client.gui.ldlib.custom.MenuItemWidget;
import com.azane.ogna.client.gui.ldlib.helper.MenuItemDisplay;
import com.azane.ogna.client.gui.ldlib.helper.UiHelper;
import com.azane.ogna.craft.CraftHelper;
import com.azane.ogna.craft.RlResultRecipe;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.registry.ModBlockEntity;
import com.azane.ogna.registry.ModRecipe;
import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.layout.Layout;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class CraftOCCBlockEntity extends BlockEntity implements IUIHolder.BlockEntityUI
{
    private final MenuItemDisplay menuItemDisplay = new MenuItemDisplay();

    public CraftOCCBlockEntity(BlockPos pPos, BlockState pBlockState)
    {
        super(ModBlockEntity.CRAFT_OCC_ENTITY.get(), pPos, pBlockState);
    }

    @Override
    public ModularUI createUI(Player player)
    {
        return new ModularUI(doCreateUI(player),this,player);
    }

    public void onPlayerUse(Player player)
    {
        if(player instanceof ServerPlayer serverPlayer)
        {
            BlockEntityUIFactory.INSTANCE.openUI(this,serverPlayer);
        }
    }

    private WidgetGroup doCreateUI(Player player)
    {
        boolean isClient = player.level().isClientSide();
        WidgetGroup ui = Optional.ofNullable(UiHelper.getUISupplier(RlHelper.build(OriginiumArts.MOD_ID,"craft"),isClient)).orElseThrow().get();
        var button = (ButtonWidget)ui.getFirstWidgetById("craft");
        var mtrGroup = (MaterialWidgetGroup)ui.getFirstWidgetById("mtr.group");
        var resGroup = (DraggableScrollableWidgetGroup)ui.getFirstWidgetById("result.list");
        if(this.level != null && resGroup != null)
        {
            // DebugLogger.log("{}",this.level.getRecipeManager().getAllRecipesFor(ModRecipe.RL_RESULT_TYPE.get()).size());
            menuItemDisplay.clearContent();
            AtomicInteger index = new AtomicInteger(0);
            this.level.getRecipeManager().getAllRecipesFor(ModRecipe.RL_RESULT_TYPE.get())
                .forEach(rlrr-> {
                    var wg = new MenuItemWidget();
                    if(wg.injectRecipe(rlrr,menuItemDisplay,index.get()))
                    {
                        setMenuItemCallback(ui,wg,rlrr,player);
                        resGroup.addWidget(wg);
                        index.incrementAndGet();
                    }
                });
            resGroup.setLayout(Layout.VERTICAL_CENTER);
            resGroup.setLayout(Layout.NONE);
        }
        return ui;
    }

    private void setMenuItemCallback(WidgetGroup root, MenuItemWidget target, RlResultRecipe recipe,Player player)
    {
        var mtrGroup = (MaterialWidgetGroup)root.getFirstWidgetById("mtr.group");

        var ingredients = recipe.getRlrIngredients();

        var button = (ButtonWidget)target.getFirstWidgetById("button");
        if(button != null)
        {
            button.setOnPressCallback(cdt -> {
                mtrGroup.setMaterialAmt(ingredients.size());
                var lis = mtrGroup.getWidgetsById(MaterialWidgetGroup.CHILD_ID);
                for(int i=0;i<Math.min(ingredients.size(),lis.size());i++)
                {
                    int amt = CraftHelper.getIngredientCount(player,ingredients.get(i));
                    ((MaterialWidget)lis.get(i)).injectIngredient(ingredients.get(i),amt);
                }
            });
        }
    }
}
