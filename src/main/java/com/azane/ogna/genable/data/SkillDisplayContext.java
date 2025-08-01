package com.azane.ogna.genable.data;

import com.azane.ogna.lib.HexColorTypeAdapter;
import com.azane.ogna.lib.RlHelper;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

public class SkillDisplayContext
{
    @SerializedName("name")
    @Getter
    private String name = "ogna.genable.skill.missing.name";
    @SerializedName("color")
    @Getter
    @JsonAdapter(HexColorTypeAdapter.class)
    private int color;
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
