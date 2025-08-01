package com.azane.ogna.combat.data;

import com.azane.ogna.combat.util.ArkDmgTypes;
import com.azane.ogna.combat.util.DmgCategory;
import com.azane.ogna.combat.util.SelectorType;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class DmgDataSet
{
    @SerializedName("fallback")
    private String fallback;
    @SerializedName("map")
    private Map<String, DamageData> map = new HashMap<>();

    public boolean hasDamageData(String key)
    {
        return map.containsKey(key);
    }

    @NotNull
    public DamageData getDamageData(String key)
    {
        DamageData data = map.get(key);
        if(data == null && (fallback == null || map.get(fallback) == null))
            throw new IllegalArgumentException("DamageData not found for key: " + key);
        return data != null ? data : map.get(fallback);
    }

    @Getter
    @NoArgsConstructor
    public static class DamageData
    {
        @SerializedName("dmg_type")
        private ResourceLocation dmgType;
        @SerializedName("dmg_category")
        private DmgCategory dmgCategory;
        @SerializedName("selector")
        private SelectorType selectorType;
        @SerializedName("range")
        private double range = 0;
        @SerializedName("hit_count")
        private int hitCount = 1;

        public Holder<DamageType> getDmgTypeHolder(boolean isClient)
        {
            if(dmgType == null)
                return null;
            return ArkDmgTypes.getHolder(ResourceKey.create(Registries.DAMAGE_TYPE,dmgType),isClient);
        }

        public static DamageData combine(DamageData higher,DamageData lower)
        {
            if(higher == null && lower == null)
                return null;
            if(higher == null)
                return lower;
            if(lower == null)
                return higher;
            DamageData combined = new DamageData();
            combined.dmgType = higher.dmgType != null ? higher.dmgType : lower.dmgType;
            combined.dmgCategory = higher.dmgCategory != null ? higher.dmgCategory : lower.dmgCategory;
            combined.selectorType = higher.selectorType != null ? higher.selectorType : lower.selectorType;
            combined.range = Math.max(higher.range, lower.range);
            combined.hitCount = Math.max(higher.hitCount, lower.hitCount);
            return combined;
        }
    }
}
