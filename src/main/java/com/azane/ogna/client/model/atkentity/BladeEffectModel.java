package com.azane.ogna.client.model.atkentity;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.entity.genable.BladeEffect;
import com.azane.ogna.genable.data.GeckoAssetData;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

import java.util.Optional;

public class BladeEffectModel extends DefaultedEntityGeoModel<BladeEffect>
{
    private ResourceLocation cachedTexture;
    private ResourceLocation cachedModelResource;
    private ResourceLocation cachedAnimation;

    public BladeEffectModel()
    {
        super(ResourceLocation.tryBuild(OriginiumArts.MOD_ID,"slash"));
    }

    @Override
    public RenderType getRenderType(BladeEffect animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getModelResource(BladeEffect animatable)
    {
        if(cachedModelResource == null)
            cachedModelResource = buildFormattedModelPath(Optional.ofNullable(animatable.getDataBase().getGeckoAsset()).map(GeckoAssetData::getModel).orElse(animatable.getDataBase().getId()));
        return cachedModelResource;
    }

    @Override
    public ResourceLocation getTextureResource(BladeEffect animatable)
    {
        if(cachedTexture == null)
            cachedTexture = buildFormattedTexturePath(Optional.ofNullable(animatable.getDataBase().getGeckoAsset()).map(GeckoAssetData::getTexture).orElse(animatable.getDataBase().getId()));
        return cachedTexture;
    }

    @Override
    public ResourceLocation getAnimationResource(BladeEffect animatable)
    {
        if(cachedAnimation == null)
            cachedAnimation = buildFormattedAnimationPath(Optional.ofNullable(animatable.getDataBase().getGeckoAsset()).map(GeckoAssetData::getAnimation).orElse(animatable.getDataBase().getId()));
        return cachedAnimation;
    }
}