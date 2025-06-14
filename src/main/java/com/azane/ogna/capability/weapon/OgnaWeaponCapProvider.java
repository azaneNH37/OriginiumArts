package com.azane.ogna.capability.weapon;

import com.azane.ogna.genable.data.OgnaWeaponData;
import com.azane.ogna.registry.CapabilityRegistry;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class OgnaWeaponCapProvider implements ICapabilitySerializable<CompoundTag>
{
    private final OgnaWeaponCap capability;
    private final LazyOptional<IOgnaWeaponCap> optional;

    public OgnaWeaponCapProvider(OgnaWeaponData data,CompoundTag tag)
    {
        this.capability = new OgnaWeaponCap(data);
        this.optional = LazyOptional.of(() -> capability);
        if (tag != null) {
            capability.deserializeNBT(tag);
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityRegistry.OGNA_WEAPON ? optional.cast() : LazyOptional.empty();
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