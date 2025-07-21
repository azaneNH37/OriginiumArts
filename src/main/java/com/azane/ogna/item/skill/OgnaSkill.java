package com.azane.ogna.item.skill;

import com.azane.ogna.genable.item.base.IGenItem;
import com.azane.ogna.genable.item.base.IPolyItemDataBase;
import com.azane.ogna.genable.item.skill.ISkill;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OgnaSkill extends Item implements IGenItem, IPolyItemDataBase<ISkill>
{
    @Getter
    private final Class<ISkill> dataBaseType = ISkill.class;
    @Getter
    private final Map<ResourceLocation,ISkill> databaseCache = new ConcurrentHashMap<>();

    public OgnaSkill() {super(new Properties().stacksTo(1));}

    @Override
    @SuppressWarnings("unchecked")
    public OgnaSkill getItem() {return this;}

    @Override
    public boolean isDataBaseForStack(ItemStack itemStack)
    {
        return isThisGenItem(itemStack);
    }
}
