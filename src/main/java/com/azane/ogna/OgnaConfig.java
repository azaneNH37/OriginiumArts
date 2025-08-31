package com.azane.ogna;

import lombok.Getter;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/**
 * @author azaneNH37 (2025-07-13)
 */
@Mod.EventBusSubscriber(modid = OriginiumArts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OgnaConfig
{
    private static ForgeConfigSpec.BooleanValue DEBUG_HITBOX;

    private static ForgeConfigSpec.DoubleValue WORLDGEN_OGNM_DENSITY;

    public static void register(ModLoadingContext context)
    {
        context.registerConfig(ModConfig.Type.COMMON,init(), OriginiumArts.MOD_ID+"/common.toml");
    }

    @Getter
    private static boolean debughitbox;
    @Getter
    private static double worldgenOgnmDensity;


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        debughitbox = DEBUG_HITBOX.get();
        worldgenOgnmDensity = WORLDGEN_OGNM_DENSITY.get();
    }

    private static ForgeConfigSpec init()
    {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        DEBUG_HITBOX = builder.comment("Whether to render hitboxes in the client").define("debug.hitbox", false);

        WORLDGEN_OGNM_DENSITY = builder.comment("Density of Originium Feature in the world").defineInRange("worldgen.ognm_density", 1.0, 0.0, 1.0);
        return builder.build();
    }

}
