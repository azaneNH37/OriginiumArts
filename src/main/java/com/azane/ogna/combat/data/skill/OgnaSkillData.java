package com.azane.ogna.combat.data.skill;

import com.azane.ogna.combat.data.AttrModifier;
import com.azane.ogna.combat.data.DmgDataSet;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author azaneNH37 (2025-07-24)
 */
@Getter
public class OgnaSkillData
{
    @SerializedName("sp")
    private int SP;
    @SerializedName("duration")
    private int duration;
    @SerializedName("storage")
    private int storage = 1;
    @SerializedName("base_attr_modifiers")
    private List<AttrModifier> baseAttrModifiers = new ArrayList<>();
    @SerializedName("skill_attr_modifiers")
    private List<AttrModifier> skillAttrModifiers = new ArrayList<>();
    @SerializedName("dmg_dataset")
    private DmgDataSet dmgDataSet = new DmgDataSet();
}
