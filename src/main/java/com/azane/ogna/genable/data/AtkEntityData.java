package com.azane.ogna.genable.data;

import com.azane.ogna.resource.helper.ExtractHelper;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AtkEntityData
{
    @SerializedName("fallback")
    private String fallback;
    @SerializedName("map")
    private Map<String,AtkUnit> map = new HashMap<>();

    public boolean hasAtkUnit(String key)
    {
        return map.containsKey(key);
    }

    @NotNull
    public AtkUnit getAtkUnit(String key)
    {
        AtkUnit unit = map.get(key);
        if(unit == null && (fallback == null || map.get(fallback) == null))
            throw new IllegalArgumentException("AtkUnit not found for key: " + key);
        return unit != null ? unit : map.get(fallback);
    }

    @Getter
    public static class AtkUnit
    {
        @SerializedName("id")
        private ResourceLocation id;
        @SerializedName("delay")
        private int delay = 0;

        public String getAtkEntityType()
        {
            return id == null ? "" : ExtractHelper.extractTypePrefix(id);
        }
    }
}