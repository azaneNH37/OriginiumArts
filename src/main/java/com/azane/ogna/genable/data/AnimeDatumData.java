package com.azane.ogna.genable.data;

import com.azane.ogna.client.lib.Datums;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minecraft.world.item.ItemDisplayContext;

import java.util.Map;

/**
 * @author azaneNH37 (2025-07-18)
 */
@Getter
public class AnimeDatumData
{
    public static final DatumUnit DEFAULT_UNIT = new DatumUnit();

    @SerializedName("map")
    private Map<String, DatumUnit> animeDatumMap;

    @Getter
    public static class DatumUnit
    {
        @SerializedName("first")
        private Datums first = Datums.hand1;
        @SerializedName("third")
        private Datums third = Datums.none;

        public Datums getDatum(ItemDisplayContext context)
        {
            return context.firstPerson() ? first : third;
        }
    }
}
