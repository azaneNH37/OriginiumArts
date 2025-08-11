package com.azane.ogna.capability.weapon;

import com.azane.ogna.combat.data.weapon.OgnaWeaponData;
import com.azane.ogna.registry.ModCapability;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author azaneNH37 (2025-07-28)
 */
public class OgnaWeaponCapProvider implements ICapabilitySerializable<CompoundTag>
{
    private final OgnaWeaponCap capability;
    private final LazyOptional<IOgnaWeaponCap> optional;

    public OgnaWeaponCapProvider(OgnaWeaponData data,CompoundTag tag)
    {
        this.capability = new OgnaWeaponCap(data,tag);
        this.optional = LazyOptional.of(() -> capability);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ModCapability.OGNA_WEAPON ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return capability.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        capability.deserializeNBT(tag);
    }
}