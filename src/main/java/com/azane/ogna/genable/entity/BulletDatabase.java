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

/**
 * @author azaneNH37 (2025-08-06)
 */
@Getter
@JsonClassTypeBinder(fullName = "bullet",namespace = OriginiumArts.MOD_ID)
public class BulletDatabase implements IBullet
{
    @Expose(deserialize = false)
    @Setter
    private ResourceLocation id;

    @SerializedName("life")
    private int life = 10;

    @SerializedName("range")
    private float range = 196.0f;

    @SerializedName("speed")
    private float speed = 1.0f;

    @SerializedName("gravity")
    private boolean gravity = true;

    @SerializedName("penetrate")
    private boolean penetrate = false;

    @SerializedName("gecko_asset")
    @Nullable
    private GeckoAssetData geckoAsset;

    @SerializedName("fx")
    @Nullable
    private FxData fxData;
}
