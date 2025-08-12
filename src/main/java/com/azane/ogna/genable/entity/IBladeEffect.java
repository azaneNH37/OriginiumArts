package com.azane.ogna.genable.entity;

import com.azane.ogna.genable.data.FxData;
import com.azane.ogna.genable.data.GeckoAssetData;
import com.azane.ogna.genable.data.SoundKeyData;
import com.azane.ogna.resource.helper.IresourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Predicate;

import static com.azane.ogna.genable.manager.BladeEffectAABBManager.*;

/**
 * @author azaneNH37 (2025-08-04)
 */
public interface IBladeEffect extends IresourceLocation
{
    int getLife();

    int getColor();

    Set<Integer> getHitFrame();

    @Nullable
    GeckoAssetData getGeckoAsset();
    @Nullable
    FxData getFxData();
    @Nullable
    SoundKeyData getSoundData();


    //BladeEffectAABBManager.BladeConfig getTransform();
    BladeTransform generateTransform(Entity owner);

    <T extends Entity> Predicate<T> generateFilter(BladeTransform transform);
}
