package com.azane.ogna.combat.data;

import com.azane.ogna.combat.attr.AttrMap;
import com.azane.ogna.combat.util.DmgCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageType;

@AllArgsConstructor(staticName = "of")
@Getter
public class CombatUnit
{
    private final Holder<DamageType> damageType;
    private final double baseVal;
    private final AttrMap.Matrices matrices;
    private final DmgCategory category;
}
