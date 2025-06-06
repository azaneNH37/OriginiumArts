package com.azane.ogna.genable.data;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class WeaponAtkEntityData
{
    @Getter
    @SerializedName("normal")
    private ResourceLocation normal;
    @SerializedName("skill")
    private List<ResourceLocation> skill;

    public ResourceLocation getSkillAtkEntity(int index)
    {
        if (index < 0 || index >= skill.size())
            return normal;
        return skill.get(index);
    }
}