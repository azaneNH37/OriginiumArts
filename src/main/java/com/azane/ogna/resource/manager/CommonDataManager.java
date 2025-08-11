package com.azane.ogna.resource.manager;

import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.debug.log.LogLv;
import com.azane.ogna.resource.helper.ParserHelper;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import lombok.Getter;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

/**
 * 服务端侧数据管理器<br>
 * 该类型的数据管理器用于服务端数据加载并向客户端同步
 * @param <T> 数据类型
 * @author tacz
 * @author azaneNH37 (2025-07-13)
 */
public class CommonDataManager<T> extends JsonDataManager<T> implements INetworkCacheReloadListener
{
    @Getter
    protected Map<ResourceLocation, String> networkCache;

    public CommonDataManager(Class<T> dataClass, Gson pGson, String directory, String marker, Consumer<JsonDataManager<T>> onDataMapInit) {
        super(dataClass, pGson, directory, marker,onDataMapInit);
    }

    public CommonDataManager(Class<T> dataClass, Gson pGson, FileToIdConverter directory, String marker, Consumer<JsonDataManager<T>> onDataMapInit) {
        super(dataClass, pGson, directory, marker,onDataMapInit);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        super.apply(pObject, pResourceManager, pProfiler);

        ImmutableMap.Builder<ResourceLocation, String> builder = ImmutableMap.builder();
        pObject.forEach((id, element) -> builder.put(id, element.toString()));
        this.networkCache = builder.build();
    }

    protected T parseJson(ResourceLocation rl,String element){ return ParserHelper.parseJsonStatic(getGson(),element,getDataClass());}

    @Override
    public void applyNetworkCache(@Nullable Map<ResourceLocation, String> cache)
    {
        if(cache == null)
            return;
        clear();
        for (Map.Entry<ResourceLocation, String> entry : cache.entrySet())
        {
            ResourceLocation id = entry.getKey();
            String element = entry.getValue();
            try {
                T data = parseJson(id,element);
                generateUnitData(id,data);
            } catch (JsonParseException | IllegalArgumentException e) {
                DebugLogger.error(getMarker(), "Failed to load data file {} {}", id, e);
            }
        }
        DebugLogger.log(LogLv.INFO, getMarker(), "Accepting Data Cache from Network: %s".formatted(cache.size()));
        getOnDataMapInit().accept(this);
        //debugLogAllData();
    }
}
