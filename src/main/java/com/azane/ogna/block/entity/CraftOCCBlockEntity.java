package com.azane.ogna.block.entity;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.gui.ldlib.custom.MaterialWidgetGroup;
import com.azane.ogna.client.gui.ldlib.helper.UiHelper;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.registry.ModBlockEntity;
import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class CraftOCCBlockEntity extends BlockEntity implements IUIHolder.BlockEntityUI
{
    public CraftOCCBlockEntity(BlockPos pPos, BlockState pBlockState)
    {
        super(ModBlockEntity.CRAFT_OCC_ENTITY.get(), pPos, pBlockState);
    }

    @Override
    public ModularUI createUI(Player player)
    {
        return new ModularUI(createUI(player.level().isClientSide),this,player);
    }

    public void onPlayerUse(Player player)
    {
        if(player instanceof ServerPlayer serverPlayer)
        {
            BlockEntityUIFactory.INSTANCE.openUI(this,serverPlayer);
        }
    }

    private WidgetGroup createUI(boolean isClient)
    {
        WidgetGroup ui = Optional.ofNullable(UiHelper.getUISupplier(RlHelper.build(OriginiumArts.MOD_ID,"craft"),isClient)).orElseThrow().get();
        var button = (ButtonWidget)ui.getFirstWidgetById("craft");
        var mtrGroup = (MaterialWidgetGroup)ui.getFirstWidgetById("mtr.group");
        if (button != null && mtrGroup != null) {
            button.setOnPressCallback(cld-> {
                mtrGroup.setMaterialAmt(mtrGroup.getMaterialAmt()+1);
                DebugLogger.log("CraftOCCBlockEntity","Material amount increased to: " + mtrGroup.getMaterialAmt());
            });
        }
        return ui;
    }
}
