package com.azane.ogna.resource.manager.specific;

import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.debug.log.LogLv;
import com.azane.ogna.genable.item.base.IGenItemDatabase;
import com.azane.ogna.resource.manager.DynamicDataManager;
import com.azane.ogna.resource.manager.JsonDataManager;
import com.azane.ogna.resource.manager.JsonDataTypeManager;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

public class DyItemDataManager<T extends IGenItemDatabase> extends DynamicDataManager<T>
{
    public DyItemDataManager(Class<T> dataClass, Gson pGson, String directory, String marker, JsonDataTypeManager manager, Consumer<JsonDataManager<T>> onDataMapInit)
    {
        super(dataClass, pGson, directory, marker,manager, onDataMapInit);
    }

    public DyItemDataManager(Class<T> dataClass, Gson pGson, FileToIdConverter fileToIdConverter, String marker,JsonDataTypeManager manager,Consumer<JsonDataManager<T>> onDataMapInit)
    {
        super(dataClass, pGson, fileToIdConverter, marker,manager, onDataMapInit);
    }

    /**
     * 在服务端注册DataBase表
     * @param pObject
     * @param pResourceManager
     * @param pProfiler
     */
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler)
    {
        super.apply(pObject, pResourceManager, pProfiler);
        DebugLogger.log(LogLv.INFO, getMarker(), "Registering ItemDataBase Server Side of class type %s with %d entries".formatted(getDataClass().getName(),getAllData().size()));
        getAllData().forEach((rl,t)->t.registerDataBase());
    }

    /**
     * 在客户端注册DataBase表
     *
     * @param cache
     */
    @Override
    public void applyNetworkCache(@Nullable Map<ResourceLocation, String> cache)
    {
        super.applyNetworkCache(cache);
        DebugLogger.log(LogLv.INFO, getMarker(), "Registering ItemDataBase Client Side of class type %s with %d entries".formatted(getDataClass().getName(),getAllData().size()));
        getAllData().forEach((rl,t)->t.registerDataBase());
    }
}
