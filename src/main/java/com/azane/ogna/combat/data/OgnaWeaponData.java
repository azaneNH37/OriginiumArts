package com.azane.ogna.combat.data;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@Getter
public class OgnaWeaponData
{
    @SerializedName("can_attack")
    private boolean canAttack = true;
    @SerializedName("can_reload")
    private boolean canReload = true;
    @SerializedName("cooldown")
    private double cooldownTime = 3000;
    @SerializedName("max_charge_time")
    private double maxChargeTime = 3000;
    @SerializedName("reload_time")
    private double reloadTime = 5000;
    @SerializedName("energy_max")
    private double maxEnergy = 100;
    @SerializedName("energy_consume")
    private double consumption = 10;
    @SerializedName("attr_modifiers")
    private List<AttrModifier> attrModifiers = new ArrayList<>();

    public double getBaseValue(Attribute attribute)
    {
        ResourceLocation rl = ForgeRegistries.ATTRIBUTES.getKey(attribute);
        if(rl == null)
            return Double.NaN;
        switch (rl.getPath())
        {
            case "weapon.attack.cd" -> {return cooldownTime;}
            case "weapon.reload.cd" -> {return reloadTime;}
            case "weapon.charge.max" -> {return maxChargeTime;}
            case "weapon.energy.store" -> {return maxEnergy;}
            case "weapon.energy.consume" -> {return consumption;}
        }
        return Double.NaN;
    }
}
