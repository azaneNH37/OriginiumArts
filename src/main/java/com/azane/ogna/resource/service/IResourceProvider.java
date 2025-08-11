package com.azane.ogna.resource.service;

import com.azane.ogna.genable.entity.IBladeEffect;
import com.azane.ogna.genable.entity.IBullet;
import com.azane.ogna.genable.item.chip.IChip;
import com.azane.ogna.genable.item.skill.ISkill;
import com.azane.ogna.genable.item.weapon.IStaffDataBase;
import com.azane.ogna.genable.item.weapon.ISwordDataBase;
import com.azane.ogna.lib.GsonExtra;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * @author tacz
 * @author azaneNH37 (2025-08-04)
 */
public interface IResourceProvider
{
    Gson GSON = new GsonBuilder()
        .registerTypeAdapter(ResourceLocation.class,new ResourceLocation.Serializer())
        .addDeserializationExclusionStrategy(GsonExtra.EXPOSE_FILTER_deserialize)
        .addSerializationExclusionStrategy(GsonExtra.EXPOSE_FILTER_serialize)
        .create();


    //此处添加全局访问数据的接口
    Set<Map.Entry<ResourceLocation, IBladeEffect>> getAllBladeEffects();

    @Nullable IBladeEffect getBladeEffect(ResourceLocation id);

    Set<Map.Entry<ResourceLocation, IBullet>> getAllBullets();
    @Nullable IBullet getBullet(ResourceLocation id);

    Set<Map.Entry<ResourceLocation, IStaffDataBase>> getAllStaffs();
    @Nullable IStaffDataBase getStaff(ResourceLocation id);

    Set<Map.Entry<ResourceLocation, ISwordDataBase>> getAllSwords();
    @Nullable ISwordDataBase getSword(ResourceLocation id);

    Set<Map.Entry<ResourceLocation, ISkill>> getAllSkills();
    @Nullable ISkill getSkill(ResourceLocation id);

    Set<Map.Entry<ResourceLocation, IChip>> getAllChips();
    @Nullable IChip getChip(ResourceLocation id);
}
