package com.azane.ogna.resource.service;


import com.azane.ogna.OriginiumArts;
import com.azane.ogna.genable.entity.IBladeEffect;
import com.azane.ogna.genable.entity.IBullet;
import com.azane.ogna.genable.item.chip.IChip;
import com.azane.ogna.genable.item.skill.ISkill;
import com.azane.ogna.genable.item.weapon.IStaffDataBase;
import com.azane.ogna.genable.item.weapon.ISwordDataBase;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.resource.manager.DynamicDataManager;
import com.azane.ogna.resource.manager.INetworkCacheReloadListener;
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
    protected DynamicDataManager<IBullet> bullets;
    protected DyItemDataManager<IStaffDataBase> staffs;
    protected DyItemDataManager<ISwordDataBase> swords;
    protected DyItemDataManager<ISkill> skills;
    protected DyItemDataManager<IChip> chips;
    //游戏内自存储的data

    //此处添加继承自IResourceProvider的接口实现


    @Override
    public Set<Map.Entry<ResourceLocation, IBladeEffect>> getAllBladeEffects() {return bladeEffects.getAllDataEntries();}

    @Override
    public @Nullable IBladeEffect getBladeEffect(ResourceLocation id)
    {
        return bladeEffects.getData(id);
    }

    @Override
    public Set<Map.Entry<ResourceLocation, IBullet>> getAllBullets()
    {
        return bullets.getAllDataEntries();
    }
    @Override
    public @Nullable IBullet getBullet(ResourceLocation id)
    {
        return bullets.getData(id);
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

    @Override
    public Set<Map.Entry<ResourceLocation, ISwordDataBase>> getAllSwords() {return swords.getAllDataEntries();}
    @Override
    public @Nullable ISwordDataBase getSword(ResourceLocation id) {return swords.getData(id);}

    @Override
    public Set<Map.Entry<ResourceLocation, ISkill>> getAllSkills() {return skills.getAllDataEntries();}

    @Override
    public @Nullable ISkill getSkill(ResourceLocation id) {return skills.getData(id);}

    @Override
    public Set<Map.Entry<ResourceLocation, IChip>> getAllChips() {return chips.getAllDataEntries();}
    @Override
    public @Nullable IChip getChip(ResourceLocation id) {return chips.getData(id);}

    /**
     * 理论上 每一次服务端的重新加载都会重新调用该方法
     * 但客户端只会在初始化时调用该方法
     */
    protected void reloadAndBind()
    {
       //实例化全局数据
        bladeEffects = new DynamicDataManager<>(IBladeEffect.class,GSON,"ogna/blade","BladeEffects",JsonTypeManagers.modTypeManager,DataServiceInit.bladeEffectInit);
        staffs = new DyItemDataManager<>(IStaffDataBase.class,GSON,"ogna/staff","Staffs",JsonTypeManagers.modTypeManager,DataServiceInit.staffInit);
        swords = new DyItemDataManager<>(ISwordDataBase.class,GSON,"ogna/sword","Swords",JsonTypeManagers.modTypeManager,DataServiceInit.swordInit);
        bullets = new DynamicDataManager<>(IBullet.class,GSON,"ogna/bullet","Bullets",JsonTypeManagers.modTypeManager,DataServiceInit.bulletInit);
        skills = new DyItemDataManager<>(ISkill.class,GSON,"ogna/skill","Skills",JsonTypeManagers.modTypeManager,DataServiceInit.skillInit);
        chips = new DyItemDataManager<>(IChip.class,GSON,"ogna/chip","Chips",JsonTypeManagers.modTypeManager,DataServiceInit.chipInit);


        ImmutableMap.Builder<ResourceLocation, INetworkCacheReloadListener> builder = ImmutableMap.builder();
        //注册C/S传递和reload加载
        register(bladeEffects, "blade_effects", builder);
        register(staffs, "staffs", builder);
        register(swords, "swords", builder);
        register(bullets, "bullets", builder);
        register(skills, "skills", builder);
        register(chips, "chips", builder);
        listeners = builder.build();
    }

    private <T extends INetworkCacheReloadListener> void register(T listener,String rl, ImmutableMap.Builder<ResourceLocation, INetworkCacheReloadListener> builder)
    {
        builder.put(Objects.requireNonNull(RlHelper.build(OriginiumArts.MOD_ID, rl)),listener);
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
