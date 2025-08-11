package com.azane.ogna.client.renderer.atkentity;

import com.azane.ogna.client.model.atkentity.BulletModel;
import com.azane.ogna.entity.genable.Bullet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * @author azaneNH37 (2025-07-17)
 */
public class BulletRenderer extends GeoEntityRenderer<Bullet>
{
    public BulletRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new BulletModel());
        //addRenderLayer(new AutoGlowingGeoLayer<>(this));
        //addRenderLayer(new ColorLayer<>(this));
    }


    @Override
    public void render(Bullet pEntity, float entityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource bufferSource, int packedLight)
    {
        pPoseStack.pushPose();
        //TODO: perhaps we need to standardlize the geo model
        pPoseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot())));
        super.render(pEntity, entityYaw, pPartialTicks, pPoseStack, bufferSource, packedLight);
        pPoseStack.popPose();
    }

    @Override
    public void actuallyRender(PoseStack poseStack, Bullet entity, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        double baseAlpha = 1.0D;
        if(!isReRender)
        {
            int lifetime = entity.getDataBase().getLife();
            double deathTime = lifetime;
            baseAlpha = (Math.min(deathTime, Math.max(0, (lifetime - (entity.tickCount) - partialTick))) / deathTime);
            baseAlpha = -Math.pow(baseAlpha - 1, 4.0)+1.0;
            //DebugLogger.log("RENDER:tick:{},delay:{},alpha:{}", entity.tickCount, entity.getEntityData().get(BladeEffect.DELAY), baseAlpha);
        }
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, LightTexture.FULL_BRIGHT, packedOverlay, red, green, blue, (float) baseAlpha);
    }
}