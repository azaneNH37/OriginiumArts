package com.azane.ogna.combat.data;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * @author azaneNH37 (2025-08-02)
 */
@Getter
public class ChipData
{
    @SerializedName("limitVolume")
    private boolean limitVolume = true;
    @SerializedName("limit_stack")
    private boolean limitSize = true;
    @SerializedName("stack_size")
    private int stackSize = 3;
    @SerializedName("volume")
    private int volume = 5;
}
