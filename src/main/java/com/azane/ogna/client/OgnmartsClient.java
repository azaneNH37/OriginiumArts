package com.azane.ogna.client;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.renderer.SlashRenderer;
import com.azane.ogna.registry.EntityRegistry;
import com.azane.ogna.util.GeoExtendUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import software.bernie.geckolib.core.molang.LazyVariable;
import software.bernie.geckolib.core.molang.MolangParser;
import software.bernie.geckolib.core.molang.MolangQueries;

@Mod.EventBusSubscriber(modid = OriginiumArts.MOD_ID,value = Dist.CLIENT,bus=Mod.EventBusSubscriber.Bus.MOD)
public class OgnmartsClient
{
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event)
    {
        //OriginiumMod.LOGGER.warn("clientSetup");
    }
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(EntityRegistry.SLASH.get(), SlashRenderer::new);
    }
}
