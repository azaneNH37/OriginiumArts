package com.azane.ogna;

import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.lib.EdataSerializer;
import com.azane.ogna.network.OgnmChannel;
import com.azane.ogna.registry.EntityRegistry;
import com.azane.ogna.registry.ItemRegistry;
import com.azane.ogna.registry.ModCreativeTabRegistry;
import com.azane.ogna.resource.service.JsonTypeManagers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(OriginiumArts.MOD_ID)
public class OriginiumArts
{

    public static final String MOD_ID = "ognmarts";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public OriginiumArts()
    {
        this(FMLJavaModLoadingContext.get());
    }

    public OriginiumArts(FMLJavaModLoadingContext context)
    {
        OgnaConfig.register(ModLoadingContext.get());
        DebugLogger.init();

        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);


        EntityRegistry.ENTITIES.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);
        ModCreativeTabRegistry.TABS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(()->{
            JsonTypeManagers.loadJsonTypeManagers();
            OgnmChannel.DEFAULT.initialize();

            EdataSerializer.registerES();
            }
        );
    }
}
