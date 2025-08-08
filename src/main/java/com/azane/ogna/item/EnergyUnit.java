package com.azane.ogna.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class EnergyUnit extends Item
{
    public final int energyValue;

    public EnergyUnit(int energyValue)
    {
        this(new Properties().stacksTo(64).rarity(Rarity.RARE),energyValue);
    }

    public EnergyUnit(Properties pProperties,int energyValue)
    {
        super(pProperties);
        this.energyValue = energyValue;
    }
}
