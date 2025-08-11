package com.azane.ogna.resource.manager.specific;

import com.azane.ogna.resource.helper.ExtractHelper;
import com.azane.ogna.resource.helper.ITagLike;
import com.azane.ogna.resource.helper.IresourceLocation;
import com.azane.ogna.resource.manager.CommonDataManager;
import com.azane.ogna.resource.manager.JsonDataManager;
import com.google.gson.Gson;
import lombok.Getter;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * @author azaneNH37 (2025-07-13)
 */
public class TagLikeDataManager<T extends ITagLike<U>,U> extends CommonDataManager<T>
{
    @Getter
    private final Class<U> tagType;

    public TagLikeDataManager(Class<T> dataClass, Class<U> tagType, Gson pGson, FileToIdConverter directory, String marker, Consumer<JsonDataManager<T>> onDataMapInit)
    {
        super(dataClass, pGson, directory, marker, onDataMapInit);
        this.tagType = tagType;
    }

    public TagLikeDataManager(Class<T> dataClass,Class<U> tagType,Gson pGson, String directory, String marker, Consumer<JsonDataManager<T>> onDataMapInit)
    {
        this(dataClass, tagType, pGson, FileToIdConverter.json(directory), marker, onDataMapInit);
    }

    @Override
    protected void generateUnitData(ResourceLocation id, T data)
    {
        if (data != null) {
            if(dataMap.containsKey(id))
            {
                T oldData = dataMap.get(id);
                oldData.castToType(tagType).absorb(data.castToType(tagType));
                return;
            }
            if(data instanceof IresourceLocation rlData)
                rlData.setId(ExtractHelper.extractPureId(id));
            dataMap.put(id, data);
        }
    }
}
