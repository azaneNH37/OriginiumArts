package com.azane.ogna.client;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.registry.ModBlock;
import net.minecraft.world.level.ColorResolver;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.azane.ogna.lib.RegistryAccessHelper.buildBiomeKey;
import static com.azane.ogna.lib.RegistryAccessHelper.clientBiomeRegistry;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = OriginiumArts.MOD_ID,value = Dist.CLIENT,bus=Mod.EventBusSubscriber.Bus.MOD)
public class OgnmColors
{
    public static final int DEFAULT_OGNMCC_COLOR = 16637990;
    public static final int DEFAULT_DUST_COLOR = 6645080;
    public static final int DEFAULT_WRAPPER_COLOR = 4473920;
    public static final int DEFAULT_SHADER_COLOR = 16767831;


    public static final ColorResolver OGNM_CIRCUIT_RESOLVER = (biome, px, pz) -> {
        if(clientBiomeRegistry(buildBiomeKey(biome)).is(Tags.Biomes.IS_DRY))return 14359066;
        else if (clientBiomeRegistry(buildBiomeKey(biome)).is(Tags.Biomes.IS_DESERT)) return 13813385;
        else return DEFAULT_OGNMCC_COLOR;
    };

    public static final ColorResolver OGNM_DUST_RESOLVER = (biome, px, pz) -> {
        if(clientBiomeRegistry(buildBiomeKey(biome)).is(Tags.Biomes.IS_DRY))return 7803667;
        else if (clientBiomeRegistry(buildBiomeKey(biome)).is(Tags.Biomes.IS_DESERT)) return 12298052;
        else return DEFAULT_DUST_COLOR;
    };

    public static final ColorResolver OGNM_WRAPPER_RESOLVER = (biome, px, pz) -> {
        if(clientBiomeRegistry(buildBiomeKey(biome)).is(Tags.Biomes.IS_DRY)) return  16740464;
        else if (clientBiomeRegistry(buildBiomeKey(biome)).is(Tags.Biomes.IS_DESERT)) return 13739566;
        else return DEFAULT_WRAPPER_COLOR;
    };

    public static final ColorResolver OGNM_SHADE_RESOLVER = (biome, px, pz) -> {
        if(clientBiomeRegistry(buildBiomeKey(biome)).is(Tags.Biomes.IS_DRY))return 14024704;
        else if (clientBiomeRegistry(buildBiomeKey(biome)).is(Tags.Biomes.IS_DESERT)) return 16770201;
        else return DEFAULT_SHADER_COLOR;
    };

    @SubscribeEvent
    public static void colorResolverRegistry(RegisterColorHandlersEvent.ColorResolvers event)
    {
        event.register(OGNM_CIRCUIT_RESOLVER);
        event.register(OGNM_DUST_RESOLVER);
        event.register(OGNM_WRAPPER_RESOLVER);
        event.register(OGNM_SHADE_RESOLVER);
    }

    @SubscribeEvent
    public static void colorRegistry(RegisterColorHandlersEvent.Block event)
    {
        event.register(((pState, pLevel, pPos, pTintIndex) -> pLevel != null && pPos != null ? pLevel.getBlockTint(pPos,OgnmColors.OGNM_SHADE_RESOLVER) : OgnmColors.DEFAULT_SHADER_COLOR),
            ModBlock.AOGNM_L.block.get(),
            ModBlock.AOGNM_M.block.get(),
            ModBlock.AOGNM_S.block.get(),
            ModBlock.IOGNM.block.get());
    }
}
