package com.azane.ogna.client;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.renderer.atkentity.BladeEffectRenderer;
import com.azane.ogna.client.renderer.atkentity.SlashRenderer;
import com.azane.ogna.client.renderer.StaffRenderer;
import com.azane.ogna.registry.EntityRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = OriginiumArts.MOD_ID,value = Dist.CLIENT,bus=Mod.EventBusSubscriber.Bus.MOD)
public class OgnmartsClient
{
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event)
    {
        ModList.get().getMods().stream()
                .filter(modInfo -> modInfo.getModId().equals("yes_steve_model"))
                .findFirst()
                .ifPresent(iModInfo -> StaffRenderer.datumBasisTransform = poseStack -> {
                    poseStack.translate(0F,1.75F,0F);
                });
        //OriginiumMod.LOGGER.warn("clientSetup");
    }
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(EntityRegistry.SLASH.get(), SlashRenderer::new);
        event.registerEntityRenderer(EntityRegistry.BLADE_EFFECT.get(), BladeEffectRenderer::new);
    }
}
