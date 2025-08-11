package com.azane.ogna.genable.data;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

/**
 * @author azaneNH37 (2025-06-05)
 */
public class GeckoAssetData
{
    @Getter
    @SerializedName("all")
    private ResourceLocation all;
    @SerializedName("model")
    private ResourceLocation model;
    @SerializedName("texture")
    private ResourceLocation texture;
    @SerializedName("animation")
    private ResourceLocation animation;

    public ResourceLocation getAnimation()
    {
        return animation == null ? all : animation;
    }
    public ResourceLocation getModel()
    {
        return model == null ? all : model;
    }
    public ResourceLocation getTexture()
    {
        return texture == null ? all : texture;
    }
}