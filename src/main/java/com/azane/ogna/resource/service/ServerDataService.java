package com.azane.ogna.resource.service;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author azaneNH37 (2025-06-05)
 */
public class ServerDataService extends CommonDataService
{
    //当且仅当执行发包相关操作时调用
    @Getter
    private static ServerDataService S_INSTANCE;


    public static void onDataPackReload(Consumer<PreparableReloadListener> register)
    {
        var sService = new ServerDataService();
        sService.reloadAndBind();
        S_INSTANCE = sService;
        S_INSTANCE.listeners.values().forEach(register);
    }

    public static void onServerStop()
    {
        S_INSTANCE = null;
    }

    public Map<ResourceLocation, Map<ResourceLocation, String>> getNetworkCache()
    {
        ImmutableMap.Builder<ResourceLocation, Map<ResourceLocation, String>> builder = ImmutableMap.builder();
        listeners.forEach((rl,I)->builder.put(rl,I.getNetworkCache()));
        return builder.build();
    }

    public static Optional<Map<ResourceLocation, Map<ResourceLocation, String>>> getNetworkCacheOptional()
    {
        return Optional.ofNullable(S_INSTANCE).map(ServerDataService::getNetworkCache);
    }

    //在合适的位置执行发包(因平台而异)
}