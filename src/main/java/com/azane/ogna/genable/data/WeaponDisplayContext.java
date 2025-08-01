package com.azane.ogna.genable.data;

import com.azane.ogna.lib.HexColorTypeAdapter;
import com.azane.ogna.lib.RlHelper;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

public class WeaponDisplayContext
{
    @SerializedName("name")
    @Getter
    private String name = "ogna.genable.weapon.missing.name";
    @SerializedName("type_icon")
    private ResourceLocation typeIcon;
    @SerializedName("code_name")
    @Getter
    private String codeName;
    @SerializedName("color")
    @JsonAdapter(HexColorTypeAdapter.class)
    @Getter
    private int color;

    public ResourceLocation getTypeIcon()
    {
        return RlHelper.build(typeIcon.getNamespace(),"textures/gui/weapon_type/" + typeIcon.getPath() + ".png");
    }
}
