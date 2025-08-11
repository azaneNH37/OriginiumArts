package com.azane.ogna.genable.data.display;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.genable.data.TriDDisplayData;
import com.azane.ogna.lib.HexColorTypeAdapter;
import com.azane.ogna.lib.RlHelper;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

/**
 * @author azaneNH37 (2025-08-11)
 */
public class ChipDisplayContext implements IDisplayContext
{
    public static final ChipDisplayContext EMPTY = new ChipDisplayContext();

    @Getter
    private final ResourceLocation typeIcon = RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/type_icon/chip.png");
    @Getter
    private final String codeName = "";

    @SerializedName("name")
    @Getter
    private String name = "ogna.genable.chip.missing.name";
    @SerializedName("description")
    @Getter
    private String description = "ogna.genable.chip.missing.name";
    @SerializedName("color")
    @Getter
    @JsonAdapter(HexColorTypeAdapter.class)
    private int color = 0xFFFFFF;
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
