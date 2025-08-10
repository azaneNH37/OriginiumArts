package com.azane.ogna.genable.data.display;

import com.azane.ogna.genable.data.TriDDisplayData;
import com.azane.ogna.lib.HexColorTypeAdapter;
import com.azane.ogna.lib.RlHelper;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

public class SkillDisplayContext implements IDisplayContext
{
    @Getter
    private final String codeName = "";

    @SerializedName("name")
    @Getter
    private String name = "ogna.genable.skill.missing.name";
    @SerializedName("description")
    @Getter
    private String description = "ogna.genable.skill.missing.name";
    @SerializedName("color")
    @Getter
    @JsonAdapter(HexColorTypeAdapter.class)
    private int color;
    @SerializedName("type_icon")
    private ResourceLocation typeIcon;
    @SerializedName("icon")
    private ResourceLocation icon;
    @SerializedName("model")
    private ResourceLocation model;
    @SerializedName("3d")
    @Getter
    private TriDDisplayData triDDisplayData = new TriDDisplayData(6f,4f,new float[]{0f,10f,0f});

    public ResourceLocation getIcon()
    {
        return RlHelper.build(icon.getNamespace(), "textures/item/skill/%s.png".formatted(icon.getPath()));
    }

    public ResourceLocation getModel()
    {
        return RlHelper.build(model.getNamespace(),"skill/%s".formatted(model.getPath()));
    }
    public ResourceLocation getTypeIcon()
    {
        return RlHelper.build(typeIcon.getNamespace(),"textures/gui/type_icon/" + typeIcon.getPath() + ".png");
    }
}
