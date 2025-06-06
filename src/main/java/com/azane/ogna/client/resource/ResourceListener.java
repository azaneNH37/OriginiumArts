package com.azane.ogna.client.resource;

import com.azane.ogna.client.resource.manager.ModelAdditionManager;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.debug.log.LogLv;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    @SubscribeEvent
    public static void onModelRegistry(ModelEvent.RegisterAdditional event)
    {
        DebugLogger.log(LogLv.INFO, MARKER, "Model registry event fired. Registering models...");
        ClientAssetsService.INSTANCE.getAllModelAdditions().stream()
            .map(Map.Entry::getValue)
            .map(ModelAdditionManager::getItemModels)
            .filter(Objects::nonNull)
            .flatMap(List::stream)
            .map(rl -> ResourceLocation.tryBuild(rl.getNamespace(), "item_gui/" + rl.getPath()))
            .filter(Objects::nonNull)
            .forEach(event::register);
    }
}
