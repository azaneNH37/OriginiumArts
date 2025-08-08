package com.azane.ogna.genable.data;

import com.azane.ogna.lib.HexColorTypeAdapter;
import com.azane.ogna.lib.RlHelper;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

public class ChipDisplayContext
{
    public static final ChipDisplayContext EMPTY = new ChipDisplayContext();

    @SerializedName("name")
    @Getter
    private String name = "ogna.genable.skill.missing.name";
    @SerializedName("color")
    @Getter
    @JsonAdapter(HexColorTypeAdapter.class)
    private int color;
    @SerializedName("model")
    private ResourceLocation model = RlHelper.EMPTY;
    @SerializedName("3d")
    @Getter
    private TriDDisplayData triDDisplayData = new TriDDisplayData(6f,3f,new float[]{0f,10f,0f});

    public ResourceLocation getModel()
    {
        return RlHelper.build(model.getNamespace(),"chip/%s".formatted(model.getPath()));
    }
}
