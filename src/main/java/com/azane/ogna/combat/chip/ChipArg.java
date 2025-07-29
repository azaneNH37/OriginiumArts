package com.azane.ogna.combat.chip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNullableByDefault;

@Getter
@AllArgsConstructor(staticName = "of")
@ParametersAreNullableByDefault
public class ChipArg
{
    private final LivingEntity entity;
    private final ItemStack weaponStack;
}
