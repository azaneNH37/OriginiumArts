package com.azane.ogna.genable.item.weapon;

import com.azane.ogna.client.lib.Datums;
import com.azane.ogna.genable.data.AnimeDatumData;
import com.azane.ogna.genable.data.GeckoAssetData;
import com.azane.ogna.combat.data.weapon.OgnaWeaponData;
import com.azane.ogna.genable.data.AtkEntityData;
import com.azane.ogna.genable.data.WeaponDisplayContext;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

public class DefaultWeaponDataBase
{
    @Expose(deserialize = false)
    @Setter
    @Getter
    private ResourceLocation id;

    @SerializedName("gecko_asset")
    @Getter
    @Nullable
    private GeckoAssetData geckoAsset;

    @SerializedName("display_context")
    @Getter
    @Nullable
    private WeaponDisplayContext displayContext = new WeaponDisplayContext();

    @SerializedName("anime_datum")
    private AnimeDatumData animeDatumData = new AnimeDatumData();

    @SerializedName("attack_entities")
    @Getter
    private AtkEntityData atkEntities;

    @SerializedName("weapon_data")
    @Getter
    private OgnaWeaponData ognaWeaponData;

    public Datums getAnimeDatum(ItemDisplayContext context, String animeName)
    {
        return animeDatumData.getAnimeDatumMap().getOrDefault(animeName,AnimeDatumData.DEFAULT_UNIT).getDatum(context);
    }
}