package com.azane.ogna.client.gui.hud;

import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;

import java.util.List;

/**
 * @author azaneNH37 (2025-07-29)
 */
public class ThreeDWeaponHud extends OgnaHud
{
    public ThreeDWeaponHud()
    {
        super(new Vec2(0.5f,0.5f), new Vec2(64f,64f), WindowHud.SIZE, List.of());
    }

    @Override
    public void actuallyRender(GuiGraphics graphics, float partialTicks)
    {
        Minecraft mc = Minecraft.getInstance();
        ItemStack mainHandItem = mc.player.getMainHandItem();

        if(mainHandItem.isEmpty() || !IOgnaWeapon.isWeapon(mainHandItem))
            return;

        /*
        Object renderer = mainHandItem.getItem().getRenderPropertiesInternal();
        if(renderer instanceof IClientItemExtensions clientItemExtensions)
        {
            //DebugLogger.logReduced("reex","Rendering 3D weapon hud for item: " + mainHandItem.getDescriptionId());
            if(clientItemExtensions.getCustomRenderer() instanceof OgnaWeaponRenderer<?> ognaWeaponRenderer)
            {
                //DebugLogger.logReduced("rere","Rendering with OgnaWeaponRenderer for item: " + mainHandItem.getDescriptionId());
                graphics.renderItem(mainHandItem,100,100);
                graphics.pose().pushPose();
                graphics.pose().scale(20f,20f,1f);
                ognaWeaponRenderer.renderByItem(mainHandItem, ItemDisplayContext.GUI,
                        graphics.pose(), graphics.bufferSource(),
                        LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                graphics.pose().popPose();
            }
        }
         */
        Minecraft.getInstance().textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        //posestack.translate(400, 400, 200);
        posestack.translate(80.0D, 80.0D, 0.0D);
        posestack.scale(1.0F, -1.0F, 1.0F);
        posestack.scale(20.0F, 20.0F, 1.0F);
        float rotationPeriod = 8f;
        float rot = (System.currentTimeMillis() % (int) (8f * 1000)) * (360f / (rotationPeriod * 1000));
        //posestack.mulPose(Axis.XP.rotationDegrees(rotPitch));
        posestack.mulPose(Axis.YP.rotationDegrees(rot));
        RenderSystem.applyModelViewMatrix();
        PoseStack tmpPose = new PoseStack();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        Lighting.setupForFlatItems();

        Minecraft.getInstance().getItemRenderer().renderStatic(mainHandItem, ItemDisplayContext.FIXED, 0xf000f0, OverlayTexture.NO_OVERLAY, tmpPose, bufferSource, null, 0);

        bufferSource.endBatch();
        RenderSystem.enableDepthTest();
        Lighting.setupFor3DItems();
        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
    }
}
