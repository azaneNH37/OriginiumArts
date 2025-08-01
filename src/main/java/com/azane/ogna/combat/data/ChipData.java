package com.azane.ogna.combat.data;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class ChipData
{
    @SerializedName("limitVolume")
    private boolean limitVolume = true;
    @SerializedName("limit_stack")
    private boolean limitSize = false;
    @SerializedName("stack_size")
    private int stackSize = 1;
    @SerializedName("volume")
    private int volume = 5;
}
