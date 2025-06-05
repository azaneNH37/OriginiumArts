package com.azane.ogna.resource.helper;

import com.azane.ogna.resource.manager.JsonDataTypeManager;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public final class ParserHelper
{
    public static <T> T parseJsonStatic(Gson gson, JsonElement element, Class<T> targetClass)
    {
        return gson.fromJson(element,targetClass);
    }
    public static <T> T parseJsonStatic(Gson gson, String element, Class<T> targetClass)
    {
        return gson.fromJson(element,targetClass);
    }
    public static <T> T parseJsonDynamic(Gson gson, JsonElement element, Class<T> ancestorClass, ResourceLocation rl,JsonDataTypeManager manager)
    {
        return tryAdaptClassUnderAncestor(rl,ancestorClass,manager).map((clss)->gson.fromJson(element,clss)).orElse(null);
    }
    public static <T> T parseJsonDynamic(Gson gson, String element, Class<T> ancestorClass, ResourceLocation rl,JsonDataTypeManager manager)
    {
        return tryAdaptClassUnderAncestor(rl,ancestorClass,manager).map((clss)->gson.fromJson(element,clss)).orElse(null);
    }

    private static <T> Optional<Class<? extends T>> tryAdaptClassUnderAncestor(ResourceLocation rl, Class<T> ancestor,JsonDataTypeManager manager)
    {
        return manager.getClass(rl).map((target)->{
            try {
                return target.asSubclass(ancestor);
            }
            catch (ClassCastException e)
            {
                return null;
            }
        });
    }
}
