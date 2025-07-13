package com.azane.ogna.resource.manager;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.debug.log.LogLv;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.azane.ogna.resource.helper.ExtractHelper.extractTypePrefix;

public class JsonDataTypeManager
{
    private static final Logger LocalLogger = OriginiumArts.LOGGER;
    private final Map<String,Class<?>> dataClassType = new HashMap<>();

    private final String META_PATH;

    public static final Gson GSON = new GsonBuilder().create();

    public JsonDataTypeManager(String filename)
    {
        this.META_PATH = "META-INF/azane_js_bindings/"+filename+".json";
    }

    public void initialize()
    {
        dataClassType.clear();

        long time = System.currentTimeMillis();
        int jarCnt = 0;
        AtomicInteger clsCnt = new AtomicInteger();
        AtomicInteger errCnt = new AtomicInteger();

        try {
            Enumeration<URL> resources = OriginiumArts.class.getClassLoader().getResources(META_PATH);
            while (resources.hasMoreElements())
            {
                URL url = resources.nextElement();
                jarCnt++;
                try (InputStream is = url.openStream())
                {
                    JsonObject entryObject = GSON.fromJson(new InputStreamReader(is), JsonObject.class);
                    List<JsonArray> entries = entryObject.entrySet().stream().map((i)->i.getValue().getAsJsonArray()).toList();
                    entries.forEach(array -> {
                        array.forEach(entry -> {
                            clsCnt.getAndIncrement();
                            JsonObject jobj = entry.getAsJsonObject();
                            String className = jobj.get("class").getAsString();
                            String simple = jobj.get("simple").getAsString();
                            String full = jobj.get("full").getAsString();
                            if(!dataClassType.containsKey(full))
                            {
                                try {
                                    Class<?> clazz = Class.forName(className);
                                    dataClassType.put(full, clazz);
                                    if(!simple.equals("null"))
                                    {
                                        if(!dataClassType.containsKey(simple))
                                            dataClassType.put(simple,clazz);
                                        else
                                            DebugLogger.log(LogLv.ERROR,"[Conflict SimpleName] Class %s from JarUrl %s already exists, skipping simple".formatted(simple, url));
                                    }

                                }
                                catch (ClassNotFoundException e) {
                                    errCnt.getAndIncrement();
                                    DebugLogger.error("Class {} from JarUrl {} not found", className,url,e);
                                }
                            }
                            else
                                DebugLogger.log(LogLv.ERROR,"[Conflict FullName] Class %s from JarUrl %s already exists, skipping all".formatted(className, url));
                        });
                    });
                }
            }
        } catch (Exception e) {
           DebugLogger.error("Building json data type from {} failed with exception!",META_PATH);
        }
        DebugLogger.info("JsonDataTypeManager initialized with {} jars, {} classes, {} errors in {}ms from {}",
                jarCnt, clsCnt.get(), errCnt.get(), System.currentTimeMillis() - time,META_PATH);
        debugLogAllData();
    }

    public Optional<Class<?>> getClass(String prefix)
    {
        return dataClassType.containsKey(prefix) ? Optional.of(dataClassType.get(prefix)) : Optional.empty();
    }
    public Optional<Class<?>> getClass(ResourceLocation rl)
    {
        return getClass(extractTypePrefix(rl));
    }

    private void debugLogAllData()
    {
        //DebugLogger.log(LogLv.INFO, "Loaded %d json type entries from %s".formatted(dataClassType.size(), META_PATH));
        dataClassType.forEach((id, clazz) -> DebugLogger.log(LogLv.INFO, "Data ID: %s, Class: %s".formatted(id, clazz.getName())));
    }
}
