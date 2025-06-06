package com.azane.ogna.resource.service;


import com.azane.ogna.OriginiumArts;
import com.azane.ogna.genable.entity.IBladeEffect;
import com.azane.ogna.genable.item.IStaffDataBase;
import com.azane.ogna.resource.manager.CommonDataManager;
import com.azane.ogna.resource.manager.DynamicDataManager;
import com.azane.ogna.resource.manager.INetworkCacheReloadListener;
import com.azane.ogna.resource.manager.JsonDataTypeManager;
import com.azane.ogna.resource.manager.specific.DyItemDataManager;
import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public abstract class CommonDataService implements IResourceProvider
{
    protected Map<ResourceLocation,INetworkCacheReloadListener> listeners = new HashMap<>();



    //此处添加需要全局管理的data类
    //从json导入的data
    protected DynamicDataManager<IBladeEffect> bladeEffects;
    protected DyItemDataManager<IStaffDataBase> staffs;
    //游戏内自存储的data

    //此处添加继承自IResourceProvider的接口实现


    @Override
    public Set<Map.Entry<ResourceLocation, IBladeEffect>> getAllBladeEffects()
    {
        return bladeEffects.getAllDataEntries();
    }

    @Override
    public @Nullable IBladeEffect getBladeEffect(ResourceLocation id)
    {
        return bladeEffects.getData(id);
    }

    @Override
    public Set<Map.Entry<ResourceLocation, IStaffDataBase>> getAllStaffs()
    {
        return staffs.getAllDataEntries();
    }

    @Override
    public @Nullable IStaffDataBase getStaff(ResourceLocation id)
    {
        return staffs.getData(id);
    }

    /**
     * 理论上 每一次服务端的重新加载都会重新调用该方法
     * 但客户端只会在初始化时调用该方法
     */
    protected void reloadAndBind()
    {
       //实例化全局数据
        bladeEffects = new DynamicDataManager<>(IBladeEffect.class,GSON,"acjson/blade","BladeEffects",JsonTypeManagers.modTypeManager);
        staffs = new DyItemDataManager<>(IStaffDataBase.class,GSON,"acjson/staff","Staffs",JsonTypeManagers.modTypeManager);


        ImmutableMap.Builder<ResourceLocation, INetworkCacheReloadListener> builder = ImmutableMap.builder();
        //注册C/S传递和reload加载
        register(bladeEffects, "blade_effects", builder);
        register(staffs, "staffs", builder);
        listeners = builder.build();
    }

    private <T extends INetworkCacheReloadListener> void register(T listener,String rl, ImmutableMap.Builder<ResourceLocation, INetworkCacheReloadListener> builder)
    {
        builder.put(Objects.requireNonNull(ResourceLocation.tryBuild(OriginiumArts.MOD_ID, rl)),listener);
    }

    /**
     * 根据当前环境选择合适的缓存<br/>
     * 当前环境为单人游戏或多人游戏的服务端时，返回{@link ServerDataService}实例<br/>
     * 当前环境为多人游戏的客户端时，返回{@link ClientDataService}实例
     * @return {@link IResourceProvider} 实例
     */
    public static IResourceProvider get()
    {
        ServerDataService service = ServerDataService.getS_INSTANCE();
        return service == null ? ClientDataService.getC_INSTANCE() : service;
    }
}
