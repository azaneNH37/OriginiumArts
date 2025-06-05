package com.azane.ogna.resource.manager;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface INetworkCacheReloadListener extends PreparableReloadListener {
    Map<ResourceLocation, String> getNetworkCache();

    void applyNetworkCache(@Nullable Map<ResourceLocation, String> cache);
}
