package com.azane.ogna.combat.data.skill;

import com.azane.ogna.combat.data.AttrModifier;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OgnaSkillData
{
    @SerializedName("cd")
    private int cooldown;
    @SerializedName("duration")
    private int duration;
    @SerializedName("storage")
    private int storage = 1;
    @SerializedName("attr_modifiers")
    private List<AttrModifier> attrModifiers = new ArrayList<>();
}
