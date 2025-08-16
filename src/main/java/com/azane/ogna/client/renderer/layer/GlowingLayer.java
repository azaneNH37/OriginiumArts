package com.azane.ogna.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * {@link software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer} <- 菜完了
 * @author azaneNH37 (2025/8/16)
 */
public class GlowingLayer<T extends GeoAnimatable> extends GeoRenderLayer<T>
{
    public static final String SUFFIX = "_glowmask";

    public GlowingLayer(GeoRenderer<T> renderer) {super(renderer);}

    public RenderType getGlowingRenderType(T animatable)
    {
        var oriRl = getRenderer().getTextureLocation(animatable);
        var glowRl = oriRl.withPath(path -> path.substring(0, path.lastIndexOf('.')) + SUFFIX + path.substring(path.lastIndexOf('.')));
        return RenderType.entityTranslucent(glowRl);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType emissiveRenderType = getGlowingRenderType(animatable);

        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, emissiveRenderType,
            bufferSource.getBuffer(emissiveRenderType), partialTick, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
            1, 1, 1, 1);
    }
}
