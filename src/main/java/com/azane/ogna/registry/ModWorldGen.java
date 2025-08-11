package com.azane.ogna.registry;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.world.feature.OgnmFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author azaneNH37 (2025-08-03)
 */
public class ModWorldGen
{
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, OriginiumArts.MOD_ID);

    public static final RegistryObject<OgnmFeature> OGNM_WORLD = FEATURES.register("ognm_world", OgnmFeature::new);
}
