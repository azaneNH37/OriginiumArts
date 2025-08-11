package com.azane.ogna.client.renderer.atkentity;

import com.azane.ogna.OgnaConfig;
import com.azane.ogna.client.model.atkentity.BladeEffectModel;
import com.azane.ogna.entity.genable.BladeEffect;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * @author azaneNH37 (2025-07-13)
 */
public class BladeEffectRenderer extends GeoEntityRenderer<BladeEffect>
{
    public BladeEffectRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new BladeEffectModel());
        //addRenderLayer(new AutoGlowingGeoLayer<>(this));
        //addRenderLayer(new ColorLayer<>(this));
    }

    @Override
    public void render(BladeEffect entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight)
    {

        Vector3f scale = entity.getEntityData().get(BladeEffect.SCALE);
        if(OgnaConfig.isDebughitbox())
        {
            AABB box = entity.getEntityData().get(BladeEffect.ATTACK_AREA);
            //OriginiumArts.LOGGER.warn("{}", box);
            LevelRenderer.renderLineBox(
                poseStack,
                bufferSource.getBuffer(RenderType.LINES),
                box.move(-entity.getX(), -entity.getY(), -entity.getZ()),
                0.0F, 1.0F, 0.0F, 1.0F
            );
        }

        poseStack.pushPose();
        poseStack.scale(scale.x(), scale.y(), scale.z());
        //TODO: perhaps we need to standardlize the geo model
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));
        //poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()+135f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getXRot()));
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    @Override
    public void actuallyRender(PoseStack poseStack, BladeEffect entity, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        double baseAlpha = 1.0D;
        if(!isReRender)
        {
            int lifetime = entity.getDataBase().getLife();
            double deathTime = lifetime;
            baseAlpha = (Math.min(deathTime, Math.max(0, (lifetime - (entity.tickCount) - partialTick + entity.getEntityData().get(BladeEffect.DELAY)))) / deathTime);
            baseAlpha = -Math.pow(baseAlpha - 1, 4.0)+1.0;
            //DebugLogger.log("RENDER:tick:{},delay:{},alpha:{}", entity.tickCount, entity.getEntityData().get(BladeEffect.DELAY), baseAlpha);
        }
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, red, green, blue, (float) baseAlpha);
    }
}