package com.azane.ogna.genable.item.chip;

import com.azane.ogna.combat.chip.ChipArg;
import com.azane.ogna.combat.chip.ChipSet;
import com.azane.ogna.genable.data.display.ChipDisplayContext;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * @author azaneNH37 (2025-08-11)
 */
public abstract class NonItemChip implements IChip
{
    @Getter
    @Setter
    @Expose(serialize = false)
    private ResourceLocation id;

    @Getter
    @SerializedName("display_context")
    protected ChipDisplayContext displayContext = new ChipDisplayContext();

    @Override
    public boolean isItem() {return false;}

    @Override
    public int getVolumeConsume(ChipSet chipSet, ChipArg arg) {return 0;}

    @Override
    @Deprecated
    public ItemStack buildItemStack(int count) {return ItemStack.EMPTY;}

    @Override
    public void registerDataBase() {}

    @Override
    public void appendHoverText(ItemStack stack, List<Component> tooltip, TooltipFlag flag)
    {

    }
}
