package com.azane.ogna.client.model.atkentity;

import com.azane.ogna.entity.genable.Bullet;
import com.azane.ogna.genable.data.GeckoAssetData;
import com.azane.ogna.lib.RlHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

import java.util.Optional;

public class BulletModel extends DefaultedEntityGeoModel<Bullet>
{

    public BulletModel()
    {
        super(RlHelper.EMPTY);
    }

    @Override
    public RenderType getRenderType(Bullet animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getModelResource(Bullet animatable)
    {
        return buildFormattedModelPath(Optional.ofNullable(animatable.getDataBase().getGeckoAsset()).map(GeckoAssetData::getModel).orElse(animatable.getDataBase().getId()));
    }

    @Override
    public ResourceLocation getTextureResource(Bullet animatable)
    {
        return buildFormattedTexturePath(Optional.ofNullable(animatable.getDataBase().getGeckoAsset()).map(GeckoAssetData::getTexture).orElse(animatable.getDataBase().getId()));
    }

    @Override
    public ResourceLocation getAnimationResource(Bullet animatable)
    {
        return buildFormattedAnimationPath(Optional.ofNullable(animatable.getDataBase().getGeckoAsset()).map(GeckoAssetData::getAnimation).orElse(animatable.getDataBase().getId()));
    }
}