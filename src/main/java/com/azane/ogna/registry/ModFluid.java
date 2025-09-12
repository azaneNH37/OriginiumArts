package com.azane.ogna.registry;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.fluid.OriginiumEnergyFluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author azaneNH37 (2025/9/12)
 */
public class ModFluid
{
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, OriginiumArts.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, OriginiumArts.MOD_ID);

    public static final RegistryObject<FluidType> ORIGINIUM_ENERGY = FLUID_TYPES.register(OriginiumEnergyFluid.NAME, OriginiumEnergyFluid::new);
    public static final RegistryObject<FlowingFluid> SOURCE_ORIGINIUM_ENERGY = FLUIDS.register(OriginiumEnergyFluid.NAME+"_source", () -> new ForgeFlowingFluid.Source(OriginiumEnergyFluid.PROPERTIES));
    public static final RegistryObject<FlowingFluid> FLOWING_ORIGINIUM_ENERGY = FLUIDS.register(OriginiumEnergyFluid.NAME+"_flowing", () -> new ForgeFlowingFluid.Flowing(OriginiumEnergyFluid.PROPERTIES));
}
