package com.azane.ogna.genable.item.chip;

import com.azane.ogna.combat.chip.ChipArg;
import com.azane.ogna.combat.chip.ChipSet;
import com.azane.ogna.combat.data.ChipData;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.genable.data.ChipDisplayContext;
import com.azane.ogna.genable.item.base.IGenItem;
import com.azane.ogna.genable.item.base.IPolyItemDataBase;
import com.azane.ogna.registry.ModItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class ItemChip implements IChip
{
    @Getter
    @Setter
    @Expose(serialize = false,deserialize = false)
    protected ResourceLocation id;

    @Getter
    @SerializedName("display_context")
    protected ChipDisplayContext displayContext = new ChipDisplayContext();

    @Getter
    @SerializedName("base_data")
    protected ChipData chipData;

    @Override
    public boolean isItem() {return true;}

    @Override
    public boolean canPlugIn(ChipSet chipSet, ChipArg arg)
    {
        return (!chipData.isLimitVolume() || chipSet.getVolumeTake() + getVolumeConsume(chipSet,arg) <= chipSet.getVolumeLimit(arg))
                && (!chipData.isLimitSize() || chipSet.getChipCount(id) < chipData.getStackSize());
    }

    @Override
    public int getVolumeConsume(ChipSet chipSet, ChipArg arg)
    {
        return chipData.getVolume();
    }

    @Override
    public ItemStack buildItemStack(int count)
    {
        Item item = ModItem.OGNA_CHIP.get();
        if(item instanceof IGenItem genItem)
        {
            return genItem.templateBuildItemStack(buildTag(),1);
        }
        DebugLogger.error("The item %s is not an instance of IGenItem, cannot build item stack.".formatted(item.getDescriptionId()));
        return null;
    }

    @Override
    public void registerDataBase()
    {
        Item item = ModItem.OGNA_CHIP.get();
        if(item instanceof IPolyItemDataBase<?> polyItem)
        {
            polyItem.castToType(IChip.class).registerDataBase(this);
        }
    }
}
