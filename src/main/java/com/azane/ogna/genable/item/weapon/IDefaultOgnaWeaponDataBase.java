package com.azane.ogna.genable.item.weapon;

import com.azane.ogna.client.lib.Datums;
import com.azane.ogna.genable.data.GeckoAssetData;
import com.azane.ogna.combat.data.weapon.OgnaWeaponData;
import com.azane.ogna.genable.data.AtkEntityData;
import com.azane.ogna.genable.data.display.WeaponDisplayContext;
import com.azane.ogna.genable.item.base.IGenItemDatabase;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

public interface IDefaultOgnaWeaponDataBase extends IGenItemDatabase
{
    @Nullable
    GeckoAssetData getGeckoAsset();

    WeaponDisplayContext getDisplayContext();

    Datums getAnimeDatum(ItemDisplayContext context, String animeName);

    AtkEntityData getAtkEntities();

    OgnaWeaponData getOgnaWeaponData();
}
