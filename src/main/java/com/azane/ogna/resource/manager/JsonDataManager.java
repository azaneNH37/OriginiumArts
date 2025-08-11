package com.azane.ogna.resource.manager;

import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.debug.log.LogLv;
import com.azane.ogna.resource.helper.ExtractHelper;
import com.azane.ogna.resource.helper.IresourceLocation;
import com.azane.ogna.resource.helper.ParserHelper;
import com.azane.ogna.resource.helper.ResourceScanner;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import lombok.Getter;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 通用数据管理器<br>
 * 从资源包/数据包中读取json文件并解析为数据
 * @param <T> 数据类型
 * @author tacz
 * @author azaneNH37 (2025-07-13)
 */
public class JsonDataManager<T> extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>>
{
    protected final Map<ResourceLocation, T> dataMap = Maps.newHashMap();

    @Getter
    private final Gson gson;
    @Getter
    private final Class<T> dataClass;
    @Getter
    private final Marker marker;
    @Getter
    private final FileToIdConverter fileToIdConverter;
    @Getter
    private final Consumer<JsonDataManager<T>> onDataMapInit;

    public JsonDataManager(Class<T> dataClass, Gson pGson, String directory, String marker, Consumer<JsonDataManager<T>> onDataMapInit)
    {
        this(dataClass, pGson, FileToIdConverter.json(directory), marker, onDataMapInit);
    }
    public JsonDataManager(Class<T> dataClass, Gson pGson, FileToIdConverter fileToIdConverter, String marker, Consumer<JsonDataManager<T>> onDataMapInit) {
        this.gson = pGson;
        this.dataClass = dataClass;
        this.marker = MarkerManager.getMarker(marker);
        this.fileToIdConverter = fileToIdConverter;
        this.onDataMapInit = onDataMapInit;
    }

    @NotNull
    @Override
    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        return ResourceScanner.scanDirectory(pResourceManager, fileToIdConverter, this.gson);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler)
    {
        dataMap.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : pObject.entrySet())
        {
            ResourceLocation id = entry.getKey();
            JsonElement element = entry.getValue();
            try {
                T data = parseJson(id,element);
                generateUnitData(id,data);
            } catch (JsonParseException | IllegalArgumentException e) {
                DebugLogger.log(LogLv.ERROR, marker, "Failed to parse data file %s".formatted(id));
            }
        }
        if(onDataMapInit != null)
            onDataMapInit.accept(this);
        //debugLogAllData();
    }

    protected void generateUnitData(ResourceLocation id, T data)
    {
        if (data != null) {
            if(data instanceof IresourceLocation rlData)
                rlData.setId(ExtractHelper.extractPureId(id));
            dataMap.put(id, data);
        }
    }

    protected T parseJson(ResourceLocation rl, JsonElement element){ return ParserHelper.parseJsonStatic(gson,element,dataClass);}

    public T getData(ResourceLocation id) {
        return dataMap.get(id);
    }

    public Map<ResourceLocation, T> getAllData() {
        return dataMap;
    }

    public Set<Map.Entry<ResourceLocation, T>> getAllDataEntries() { return dataMap.entrySet();}

    public void clear() {
        this.dataMap.clear();
    }

    public void debugLogAllData()
    {
        DebugLogger.log(LogLv.INFO, marker, "Loaded %d data entries with class type %s".formatted(dataMap.size(), dataClass.getName()));
        dataMap.forEach((id, data) -> DebugLogger.log(LogLv.NULL, "Data ID: {}, Data: {}",id,data.toString()));
    }
}
