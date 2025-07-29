package com.azane.ogna.genable.data;

import com.azane.ogna.lib.RlHelper;
import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;

public class SkillDisplayContext
{
    @SerializedName("icon")
    private ResourceLocation icon;
    @SerializedName("model")
    private ResourceLocation model;

    public ResourceLocation getIcon()
    {
        return RlHelper.build(icon.getNamespace(), "textures/item/skill/%s.png".formatted(icon.getPath()));
    }

    public ResourceLocation getModel()
    {
        return RlHelper.build(model.getNamespace(),"skill/%s".formatted(model.getPath()));
    }
}
