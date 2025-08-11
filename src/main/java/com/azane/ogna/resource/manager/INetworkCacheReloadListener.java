package com.azane.ogna.resource.manager;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author tacz
 * @author azaneNH37 (2025-06-05)
 */
public interface INetworkCacheReloadListener extends PreparableReloadListener {
    Map<ResourceLocation, String> getNetworkCache();

    void applyNetworkCache(@Nullable Map<ResourceLocation, String> cache);
}
