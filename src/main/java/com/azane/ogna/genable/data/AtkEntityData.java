package com.azane.ogna.genable.data;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class AtkEntityData
{
    @Getter
    @SerializedName("normal")
    private AtkUnit normal;
    @SerializedName("skill")
    private List<AtkUnit> skill;

    public AtkUnit getSkillAtkUnit(int index)
    {
        if (index < 0 || index >= skill.size())
            return normal;
        return skill.get(index);
    }

    @Getter
    public static class AtkUnit
    {
        @SerializedName("id")
        private ResourceLocation id;
        @SerializedName("delay")
        private int delay = 0;
    }
}