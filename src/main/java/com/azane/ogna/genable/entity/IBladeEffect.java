package com.azane.ogna.genable.entity;

import com.azane.ogna.genable.data.GeckoAssetData;
import com.azane.ogna.resource.helper.IresourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Predicate;

import static com.azane.ogna.genable.manager.BladeEffectAABBManager.*;

public interface IBladeEffect extends IresourceLocation
{
    /**
     * 获取特效的生命周期
     * @return 生命周期
     */
    int getLife();

    /**
     * 获取特效的颜色
     * @return 颜色
     */
    int getColor();
    /**
     * 获取特效的命中帧
     * @return 命中帧集合
     */
    Set<Integer> getHitFrame();

    @Nullable
    GeckoAssetData getGeckoAsset();

    /**
     * 获取特效的变换配置
     * @return 变换配置
     */
    //BladeEffectAABBManager.BladeConfig getTransform();
    BladeTransform generateTransform(Entity owner);

    <T extends Entity> Predicate<T> generateFilter(BladeTransform transform);
}
