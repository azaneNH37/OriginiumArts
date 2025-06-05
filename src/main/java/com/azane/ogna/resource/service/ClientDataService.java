package com.azane.ogna.resource.service;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ClientDataService extends CommonDataService
{
    //当且仅当需要接收包时调用
    @Getter
    private static final ClientDataService C_INSTANCE = new ClientDataService();

    private ClientDataService()
    {
        reloadAndBind();
    }

    public static void fromNetwork(Map<ResourceLocation, Map<ResourceLocation, String>> cache)
    {
        C_INSTANCE.listeners.forEach((rl,I)->{
            if(cache.containsKey(rl))
                I.applyNetworkCache(cache.get(rl));
        });
    }
}
