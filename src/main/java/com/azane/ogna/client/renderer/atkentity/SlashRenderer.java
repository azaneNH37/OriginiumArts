package com.azane.ogna.client.renderer.atkentity;

import com.azane.ogna.client.model.atkentity.SlashModel;
import com.azane.ogna.client.renderer.layer.ColorLayer;
import com.azane.ogna.entity.SlashEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SlashRenderer extends GeoEntityRenderer<SlashEntity>
{
    public SlashRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new SlashModel());
        addRenderLayer(new ColorLayer<>(this));
    }

    @Override
    public void render(SlashEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight)
    {
        /*
        AABB box = entity.getAttackArea();
        if(box != null)
        {
            //OriginiumArts.LOGGER.warn("{}", box);
            LevelRenderer.renderLineBox(
                poseStack,
                bufferSource.getBuffer(RenderType.LINES),
                box.move(-entity.getX(), -entity.getY(), -entity.getZ()),
                0.0F, 1.0F, 0.0F, 1.0F
            );
        }
         */
        poseStack.pushPose();
        //TODO: perhaps we need to standardlize the geo model
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()+135f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getXRot()));
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    @Override
    public void actuallyRender(PoseStack poseStack, SlashEntity entity, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        double baseAlpha = 1.0D;
        if(!isReRender)
        {
            int lifetime = entity.getLifetime();
            double deathTime = lifetime;
            baseAlpha = (Math.min(deathTime, Math.max(0, (lifetime - (entity.tickCount) - partialTick))) / deathTime);
            baseAlpha = -Math.pow(baseAlpha - 1, 4.0)+1.0;
        }
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, (float) baseAlpha);
    }
}