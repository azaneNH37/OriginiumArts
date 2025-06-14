package com.azane.ogna.genable.item.weapon;

import com.azane.ogna.client.lib.Datums;
import com.azane.ogna.genable.data.GeckoAssetData;
import com.azane.ogna.genable.data.OgnaWeaponData;
import com.azane.ogna.genable.data.WeaponAtkEntityData;
import com.azane.ogna.genable.item.base.IGenItemDatabase;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

public interface IDefaultOgnaWeaponDataBase extends IGenItemDatabase
{
    @Nullable
    GeckoAssetData getGeckoAsset();

    Datums getAnimeDatum(ItemDisplayContext context, String animeName);

    WeaponAtkEntityData getAtkEntities();

    OgnaWeaponData getOgnaWeaponData();
}
