package com.azane.ogna.genable.data;

import com.azane.ogna.lib.RlHelper;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@Getter
public class FxData
{
    @Nullable
    @SerializedName("awake")
    private FxUnit awakeFx;
    @Nullable
    @SerializedName("end")
    private FxUnit endFx;
    @Nullable
    @SerializedName("hit")
    private FxUnit hitFx;


    @Getter
    public static class FxUnit
    {
        @SerializedName("id")
        private ResourceLocation id = RlHelper.EMPTY;
    }
}
