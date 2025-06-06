package com.azane.ogna.item.genable;

import com.azane.ogna.client.lib.IDynamicAssetItem;
import com.azane.ogna.client.lib.IOffHandItem;
import com.azane.ogna.genable.item.base.IGenItem;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;

public abstract class OgnaWeapon extends Item implements GeoItem, IOffHandItem, IDynamicAssetItem, IGenItem
{
    public OgnaWeapon(Properties pProperties)
    {
        super(pProperties);
    }
}
