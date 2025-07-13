package com.azane.ogna;

import lombok.Getter;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = OriginiumArts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OgnaConfig
{
    private static ForgeConfigSpec.BooleanValue DEBUG_HITBOX;

    public static void register(ModLoadingContext context)
    {
        context.registerConfig(ModConfig.Type.COMMON,init(), OriginiumArts.MOD_ID+"/common.toml");
    }

    @Getter
    private static boolean debughitbox;


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        debughitbox = DEBUG_HITBOX.get();
    }

    private static ForgeConfigSpec init()
    {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        DEBUG_HITBOX = builder.comment("Whether to render hitboxes in the client").define("debug.hitbox", false);

        return builder.build();
    }

}
