package com.azane.ogna.genable.item;

import com.azane.ogna.client.lib.Datums;
import com.azane.ogna.genable.data.GeckoAssetData;
import com.azane.ogna.genable.data.WeaponAtkEntityData;
import com.azane.ogna.genable.item.base.IGenItemDatabase;
import com.azane.ogna.resource.helper.IresourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

public interface IStaffDataBase extends IGenItemDatabase
{
    @Nullable
    GeckoAssetData getGeckoAsset();

    Datums getAnimeDatum(ItemDisplayContext context,String animeName);

    WeaponAtkEntityData getAtkEntities();
}