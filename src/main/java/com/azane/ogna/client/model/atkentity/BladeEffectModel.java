package com.azane.ogna.client.model.atkentity;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.entity.genable.BladeEffect;
import com.azane.ogna.genable.data.GeckoAssetData;
import com.azane.ogna.lib.RlHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

import java.util.Optional;

/**
 * @author azaneNH37 (2025-08-04)
 */
public class BladeEffectModel extends DefaultedEntityGeoModel<BladeEffect>
{

    public BladeEffectModel()
    {
        super(RlHelper.build(OriginiumArts.MOD_ID,"slash"));
    }

    @Override
    public RenderType getRenderType(BladeEffect animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getModelResource(BladeEffect animatable)
    {
        return buildFormattedModelPath(Optional.ofNullable(animatable.getDataBase().getGeckoAsset()).map(GeckoAssetData::getModel).orElse(animatable.getDataBase().getId()));
    }

    @Override
    public ResourceLocation getTextureResource(BladeEffect animatable)
    {
        return buildFormattedTexturePath(Optional.ofNullable(animatable.getDataBase().getGeckoAsset()).map(GeckoAssetData::getTexture).orElse(animatable.getDataBase().getId()));

    }

    @Override
    public ResourceLocation getAnimationResource(BladeEffect animatable)
    {
        return buildFormattedAnimationPath(Optional.ofNullable(animatable.getDataBase().getGeckoAsset()).map(GeckoAssetData::getAnimation).orElse(animatable.getDataBase().getId()));
    }
}