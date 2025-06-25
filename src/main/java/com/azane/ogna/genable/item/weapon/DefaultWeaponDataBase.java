package com.azane.ogna.genable.item.weapon;

import com.azane.ogna.client.lib.Datums;
import com.azane.ogna.genable.data.GeckoAssetData;
import com.azane.ogna.genable.data.OgnaWeaponData;
import com.azane.ogna.genable.data.WeaponAtkEntityData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DefaultWeaponDataBase
{
    //TODO: a fail-safe id
    @Expose(deserialize = false)
    @Setter
    @Getter
    private ResourceLocation id;

    @SerializedName("gecko_asset")
    @Getter
    @Nullable
    private GeckoAssetData geckoAsset;

    @SerializedName("anime_datum_first")
    private Map<String, Datums> animeDatumMapFirst;
    @SerializedName("anime_datum_third")
    private Map<String,Datums> animeDatumMapThird;

    @SerializedName("atk_delay")
    @Getter
    private int atkDelay = 0;

    @SerializedName("attack_entities")
    @Getter
    private WeaponAtkEntityData atkEntities;

    @SerializedName("weapon_data")
    @Getter
    private OgnaWeaponData ognaWeaponData;

    public Datums getAnimeDatum(ItemDisplayContext context, String animeName)
    {
        return context.firstPerson() ?
            animeDatumMapFirst.getOrDefault(animeName, Datums.FIRST_PLAYER_HAND) :
            animeDatumMapThird.getOrDefault(animeName, Datums.NONE);
    }
}