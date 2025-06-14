package com.azane.ogna.resource.service;

import com.azane.ogna.genable.entity.IBladeEffect;
import com.azane.ogna.genable.item.weapon.IStaffDataBase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public interface IResourceProvider
{
    Gson GSON = new GsonBuilder()
        .registerTypeAdapter(ResourceLocation.class,new ResourceLocation.Serializer())
        .create();


    //此处添加全局访问数据的接口
    Set<Map.Entry<ResourceLocation, IBladeEffect>> getAllBladeEffects();

    @Nullable IBladeEffect getBladeEffect(ResourceLocation id);

    Set<Map.Entry<ResourceLocation, IStaffDataBase>> getAllStaffs();

    @Nullable IStaffDataBase getStaff(ResourceLocation id);
}
