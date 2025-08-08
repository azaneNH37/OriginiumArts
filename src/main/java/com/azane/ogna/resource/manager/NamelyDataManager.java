package com.azane.ogna.resource.manager;

import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.resource.helper.ResourceScanner;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class NamelyDataManager<T> extends CommonDataManager<T>
{
    private final Predicate<ResourceLocation> fileNameFilter;
    private final boolean forceApply;

    public NamelyDataManager(Class<T> dataClass, Gson pGson, String directory, String marker, Predicate<ResourceLocation> fileNameFilter, boolean forceApply, Consumer<JsonDataManager<T>> onDataMapInit)
    {
        super(dataClass, pGson, directory, marker, onDataMapInit);
        this.fileNameFilter = fileNameFilter;
        this.forceApply = forceApply;
    }

    public NamelyDataManager(Class<T> dataClass, Gson pGson, FileToIdConverter fileToIdConverter, String marker, Predicate<ResourceLocation> fileNameFilter,boolean forceApply,Consumer<JsonDataManager<T>> onDataMapInit)
    {
        super(dataClass, pGson, fileToIdConverter, marker, onDataMapInit);
        this.fileNameFilter = fileNameFilter;
        this.forceApply = forceApply;
    }

    @Override
    protected @NotNull Map<ResourceLocation, JsonElement> prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler)
    {
        //DebugLogger.error("!!!Preparing NamelyDataManager for {} with filter: {}", getName(), fileNameFilter);
        Map<ResourceLocation,JsonElement> tmp = ResourceScanner.scanDirectory(pResourceManager, getFileToIdConverter(), getGson(), fileNameFilter);
        if(forceApply)
        {
            this.apply(tmp, pResourceManager, pProfiler);
            return Map.of(); // 返回空的Map，表示已经应用了数据
        }
        return tmp;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler)
    {
        if(pObject.isEmpty())
        {
            DebugLogger.warn("[{}] it seems that we have no data to apply, check whether forceApply is true", getDataClass().getName());
            return;
        }
        super.apply(pObject, pResourceManager, pProfiler);
    }
}
