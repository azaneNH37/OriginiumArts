package com.azane.ogna.genable.data;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class OgnaWeaponData
{
    @SerializedName("can_attack")
    private boolean canAttack = true;
    @SerializedName("can_reload")
    private boolean canReload = true;
    @SerializedName("cooldown")
    private int cooldownTime = 3000;
    @SerializedName("max_charge_time")
    private int maxChargeTime = 3000;
    @SerializedName("reload_time")
    private int reloadTime = 5000;
    @SerializedName("energy_max")
    private int maxEnergy = 100;
    @SerializedName("energy_consume")
    private int consumption = 10;
}
