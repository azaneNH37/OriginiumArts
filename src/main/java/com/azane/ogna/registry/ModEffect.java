package com.azane.ogna.registry;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.world.effect.SandPotionEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffect
{
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, OriginiumArts.MOD_ID);

    public static final RegistryObject<SandPotionEffect> SAND_POTION = EFFECTS.register("sand_potion", SandPotionEffect::new);
}
