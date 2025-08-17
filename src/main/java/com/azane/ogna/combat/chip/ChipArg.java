package com.azane.ogna.combat.chip;

import com.azane.ogna.capability.weapon.IOgnaWeaponCap;
import com.azane.ogna.item.weapon.IOgnaWeapon;
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
    public static final ChipArg EMPTY = ChipArg.of(null,null,null);

    public static ChipArg of(@Nullable LivingEntity entity, @Nullable ItemStack weaponStack)
    {
        if(IOgnaWeapon.isWeapon(weaponStack))
        {
            IOgnaWeapon weapon = (IOgnaWeapon) weaponStack.getItem();
            IOgnaWeaponCap weaponCap = weapon.getWeaponCap(weaponStack);
            return ChipArg.of(entity, weaponStack, weaponCap);
        }
        return ChipArg.of(entity, weaponStack, null);
    }

    @Nullable
    private final LivingEntity entity;
    @Nullable
    private final ItemStack weaponStack;
    @Nullable
    private final IOgnaWeaponCap weaponCap;
}
