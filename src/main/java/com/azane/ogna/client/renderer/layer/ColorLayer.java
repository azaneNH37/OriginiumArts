package com.azane.ogna.client.renderer.layer;

import com.azane.ogna.entity.SlashEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import static net.minecraft.util.FastColor.ARGB32.*;

public class ColorLayer<T extends IColorable & GeoAnimatable> extends GeoRenderLayer<T>
{
    public ColorLayer(GeoRenderer<T> entityRendererIn)
    {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay)
    {
        int packed = animatable.getPackedColor();


        //double baseAlpha = partialTick < 0.5f ? Math.pow(1.7D*partialTick,6) : partialTick < 0.8f ? -Math.pow(partialTick-0.5D,2)+1 : Math.pow(5*(partialTick-1),8);
        //OriginiumArts.LOGGER.warn("alpha:{}",baseAlpha);
        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, renderType,
            bufferSource.getBuffer(renderType), partialTick, packedLight, packedOverlay,
            10*red(packed)/256f, green(packed)/256f, blue(packed)/256f, 1f);
    }
}
