package com.azane.ogna.genable.entity;

import com.azane.ogna.genable.data.FxData;
import com.azane.ogna.genable.data.GeckoAssetData;
import com.azane.ogna.resource.helper.IresourceLocation;

import javax.annotation.Nullable;

public interface IBullet extends IresourceLocation
{
    int getLife();
    float getRange();
    float getSpeed();
    boolean isGravity();
    boolean isPenetrate();
    @Nullable
    GeckoAssetData getGeckoAsset();
    @Nullable
    FxData getFxData();
}
