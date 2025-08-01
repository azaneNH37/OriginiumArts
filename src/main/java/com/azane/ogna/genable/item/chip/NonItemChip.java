package com.azane.ogna.genable.item.chip;

import com.azane.ogna.combat.chip.ChipArg;
import com.azane.ogna.combat.chip.ChipSet;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class NonItemChip implements IChip
{
    @Getter
    @Setter
    @Expose(serialize = false)
    private ResourceLocation id;

    @Override
    public boolean isItem() {return false;}

    @Override
    public int getVolumeConsume(ChipSet chipSet, ChipArg arg) {return 0;}

    @Override
    @Deprecated
    public ItemStack buildItemStack(int count) {return ItemStack.EMPTY;}

    @Override
    public void registerDataBase() {}
}
