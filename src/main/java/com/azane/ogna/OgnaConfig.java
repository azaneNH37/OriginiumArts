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
    private static ForgeConfigSpec.BooleanValue ENABLE_GLOWING_TEXTURE;

    public static void register(ModLoadingContext context)
    {
        context.registerConfig(ModConfig.Type.COMMON,init(), OriginiumArts.MOD_ID+"/common.toml");
    }

    @Getter
    private static boolean debughitbox;
    @Getter
    private static boolean enableGlowingTexture;


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        debughitbox = DEBUG_HITBOX.get();
        enableGlowingTexture = ENABLE_GLOWING_TEXTURE.get();
    }

    private static ForgeConfigSpec init()
    {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        DEBUG_HITBOX = builder.comment("Whether to render hitboxes in the client").define("debug.hitbox", false);
        ENABLE_GLOWING_TEXTURE = builder.comment("Whether to enable glowing texture for items(1) & blocks(2) (it can be useful when encountering certain shaderpacks)")
            .define("render.glowingTexture",true);

        return builder.build();
    }

}
