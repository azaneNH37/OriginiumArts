package com.azane.ogna.lib;

import com.azane.ogna.OriginiumArts;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Objects;

/**
 * @author azaneNH37 (2025-07-20)
 */
public class RegistryAccessHelper
{
    public static RegistryAccess serverRegistryAccess()
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if(server == null)
        {
            OriginiumArts.LOGGER.error("Wrongly invoke server logic about RegistryAccess!");
            throw new IllegalStateException("未在服务端环境中调用！");
        }
        return server.registryAccess();
    }
    @OnlyIn(Dist.CLIENT)
    public static RegistryAccess clientRegistryAccess()
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level == null)
        {
            OriginiumArts.LOGGER.error("Client World not loaded!");
            throw new IllegalStateException("未在客户端环境中调用/客户端维度未生成！");
        }
        return minecraft.level.registryAccess();
    }

    public static Holder<Biome> serverBiomeRegistry(ResourceKey<Biome> key)
    {
        return serverRegistryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(key);
    }
    public static Holder<Biome> clientBiomeRegistry(ResourceKey<Biome> key)
    {
        return clientRegistryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(key);
    }
    public static ResourceKey<Biome> buildBiomeKey(Biome biome)
    {
        return ResourceKey.create(Registries.BIOME, Objects.requireNonNull(clientRegistryAccess().registryOrThrow(Registries.BIOME).getKey(biome)));
    }
}
