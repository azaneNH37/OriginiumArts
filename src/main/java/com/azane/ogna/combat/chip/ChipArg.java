package com.azane.ogna.combat.chip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;

/**
 * @author azaneNH37 (2025-08-09)
 */
@Getter
@AllArgsConstructor(staticName = "of")
@ParametersAreNullableByDefault
public class ChipArg
{
    public static final ChipArg EMPTY = ChipArg.of(null,null);

    @Nullable
    private final LivingEntity entity;
    @Nullable
    private final ItemStack weaponStack;
}
