package com.azane.ogna.genable.data;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class TriDDisplayData
{
    @SerializedName("rotation_period")
    @Getter
    private float rotationPeriod = 8f;
    @SerializedName("scale")
    @Getter
    private float scale = 1f;
    @SerializedName("offset")
    private float[] offset = {0f, 0f, 0f};

    public float[] getOffset()
    {
        if(offset.length < 3)
            offset = new float[]{0f, 0f, 0f};
        return offset;
    }
}
