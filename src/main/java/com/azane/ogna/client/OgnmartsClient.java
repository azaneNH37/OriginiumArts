package com.azane.ogna.client;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.gameplay.AttackInputHandler;
import com.azane.ogna.client.lib.OffHandItemTransform;
import com.azane.ogna.client.renderer.atkentity.BladeEffectRenderer;
import com.azane.ogna.client.renderer.atkentity.BulletRenderer;
import com.azane.ogna.registry.ModEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
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
                .ifPresent(iModInfo -> OffHandItemTransform.datumBasisTransform = OffHandItemTransform.YSM_BASIS);
        //OriginiumMod.LOGGER.warn("clientSetup");
    }

    @SubscribeEvent
    public static void onClientSetup(RegisterKeyMappingsEvent event) {
        // 注册键位
        event.register(AttackInputHandler.RELOAD_KEY);
    }
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(ModEntity.BLADE_EFFECT.get(), BladeEffectRenderer::new);
        event.registerEntityRenderer(ModEntity.BULLET.get(), BulletRenderer::new);
    }
}
