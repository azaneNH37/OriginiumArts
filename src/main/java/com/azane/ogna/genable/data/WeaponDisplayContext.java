package com.azane.ogna.genable.data;

import com.azane.ogna.lib.RlHelper;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

public class WeaponDisplayContext
{
    @SerializedName("type_icon")
    private ResourceLocation typeIcon;
    @SerializedName("code_name")
    @Getter
    private String codeName;
    @SerializedName("color")
    @Getter
    private int color;

    public ResourceLocation getTypeIcon()
    {
        return RlHelper.build(typeIcon.getNamespace(),"textures/gui/weapon_type/" + typeIcon.getPath() + ".png");
    }
}
