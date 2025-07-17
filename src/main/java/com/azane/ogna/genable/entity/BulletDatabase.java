package com.azane.ogna.genable.entity;

import com.azane.cjsop.annotation.JsonClassTypeBinder;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.genable.data.FxData;
import com.azane.ogna.genable.data.GeckoAssetData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@JsonClassTypeBinder(fullName = "bullet",namespace = OriginiumArts.MOD_ID)
public class BulletDatabase implements IBullet
{
    @Expose(deserialize = false)
    @Setter
    @Getter
    private ResourceLocation id;

    @SerializedName("life")
    @Getter
    private int life = 10;

    @SerializedName("range")
    @Getter
    private float range = 196.0f;

    @SerializedName("speed")
    @Getter
    private float speed = 1.0f;

    @SerializedName("gecko_asset")
    @Getter
    @Nullable
    private GeckoAssetData geckoAsset;

    @SerializedName("fx")
    @Getter
    @Nullable
    private FxData fxData;
}
