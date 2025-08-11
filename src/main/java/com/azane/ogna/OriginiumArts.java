package com.azane.ogna;

import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.lib.EdataSerializer;
import com.azane.ogna.network.OgnmChannel;
import com.azane.ogna.registry.*;
import com.azane.ogna.resource.service.JsonTypeManagers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @author azaneNH37 (2025-08-09)
 */
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

        ModAttribute.ATTRIBUTES.register(modEventBus);
        ModEntity.ENTITIES.register(modEventBus);
        ModBlock.BLOCKS.register(modEventBus);
        ModBlockEntity.BLOCK_ENTITIES.register(modEventBus);
        ModItem.ITEMS.register(modEventBus);
        ModCreativeTab.TABS.register(modEventBus);
        ModWorldGen.FEATURES.register(modEventBus);
        ModEffect.EFFECTS.register(modEventBus);
        ModRecipe.register(modEventBus);

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
