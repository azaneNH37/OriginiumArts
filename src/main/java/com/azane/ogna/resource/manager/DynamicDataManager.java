package com.azane.ogna.resource.manager;


import com.azane.ogna.resource.helper.ParserHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;

/**
 * 引入了对复合类型的支持，须搭配JsonClassTypeBinder和{@link JsonDataTypeManager}使用
 * @param <T> 一个接口或者是基类
 */
public class DynamicDataManager<T> extends CommonDataManager<T>
{
    public final JsonDataTypeManager manager;

    public DynamicDataManager(Class<T> dataClass, Gson pGson, String directory, String marker,JsonDataTypeManager manager)
    {
        super(dataClass, pGson, directory, marker);
        this.manager = manager;
    }

    public DynamicDataManager(Class<T> dataClass, Gson pGson, FileToIdConverter fileToIdConverter, String marker,JsonDataTypeManager manager)
    {
        super(dataClass, pGson, fileToIdConverter, marker);
        this.manager = manager;
    }

    @Override
    protected T parseJson(ResourceLocation rl, JsonElement element)
    {
        return ParserHelper.parseJsonDynamic(getGson(),element,getDataClass(),rl,manager);
    }

    @Override
    protected T parseJson(ResourceLocation rl, String element)
    {
        return ParserHelper.parseJsonDynamic(getGson(),element,getDataClass(),rl,manager);
    }
}
