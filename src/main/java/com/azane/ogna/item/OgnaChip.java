package com.azane.ogna.item;

import com.azane.ogna.genable.item.base.IGenItem;
import com.azane.ogna.genable.item.base.IPolyItemDataBase;
import com.azane.ogna.genable.item.chip.IChip;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OgnaChip extends Item implements IGenItem, IPolyItemDataBase<IChip>
{
    @Getter
    private final Class<IChip> dataBaseType = IChip.class;
    @Getter
    private final Map<ResourceLocation,IChip> databaseCache = new ConcurrentHashMap<>();

    public OgnaChip() {super(new Properties().stacksTo(64));}

    @Override
    @SuppressWarnings("unchecked")
    public OgnaChip getItem() {return this;}

    @Override
    public boolean isDataBaseForStack(ItemStack itemStack)
    {
        return isThisGenItem(itemStack);
    }
}
