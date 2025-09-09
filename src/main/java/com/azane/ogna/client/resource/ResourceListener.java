package com.azane.ogna.client.resource;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.debug.log.LogLv;
import com.azane.ogna.lib.RlHelper;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author azaneNH37 (2025-07-29)
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ResourceListener
{
    public static final Marker MARKER = MarkerManager.getMarker("ClientResourceListener");

    @SubscribeEvent
    public static void onClientResourceReload(RegisterClientReloadListenersEvent event)
    {
        DebugLogger.log(LogLv.INFO, MARKER, "Client resource reload event fired. ");
        ClientAssetsService.INSTANCE.reloadAndRegister(event::registerReloadListener);
    }
    //TODO: 收集所有命名空间下的该文件
    @SubscribeEvent
    public static void onModelRegistry(ModelEvent.RegisterAdditional event)
    {
        DebugLogger.log(LogLv.INFO, MARKER, "Model registry event fired. Registering models...");
        ResourceLocation rl = RlHelper.build(OriginiumArts.MOD_ID,"ogna/config/model_addition.json");
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        try (InputStream inputStream = resourceManager.open(rl)) {
            InputStreamReader reader = new InputStreamReader(inputStream);
            JsonParser.parseReader(reader).getAsJsonObject().getAsJsonArray("item_models").asList()
                .forEach(je-> event.register(RlHelper.parse(je.getAsString())));
        } catch (Exception e) {DebugLogger.error("{}",e);}
        /*
        ClientAssetsService.INSTANCE.getAllModelAdditions().stream()
            .map(Map.Entry::getValue)
            .map(ModelAdditionManager::getItemModels)
            .filter(Objects::nonNull)
            .flatMap(List::stream)
            .filter(Objects::nonNull)
            .forEach(event::register);
         */
    }
}
