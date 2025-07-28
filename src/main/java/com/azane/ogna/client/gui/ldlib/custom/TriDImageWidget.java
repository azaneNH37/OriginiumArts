package com.azane.ogna.client.gui.ldlib.custom;

import com.azane.ogna.debug.log.DebugLogger;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@LDLRegister(name = "image.tri",group = "widget.custom")
public class TriDImageWidget extends ImageWidget
{
    @Setter
    private Supplier<ItemStack> itemSupplier;

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        if(itemSupplier == null || itemSupplier.get() == null || itemSupplier.get().isEmpty())
        {
            //DebugLogger.logReduced("fail","Rendering 3D item gui failed, item supplier is null or item is empty.");
            super.drawInBackground(graphics, mouseX, mouseY, partialTicks);
            return;
        }
        //DebugLogger.logReduced("item_gui_ren","Rendering 3D item gui for item: " + itemSupplier.get().getDescriptionId());
        Minecraft.getInstance().textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        //posestack.translate(400, 400, 200);
        posestack.translate(this.getPositionX(), this.getPositionY(), 0.0D);
        posestack.scale(1.0F, -1.0F, 1.0F);
        posestack.scale(25.0F, 25.0F, 1.0F);
        float rotationPeriod = 8f;
        float rot = (System.currentTimeMillis() % (int) (rotationPeriod * 1000)) * (360f / (rotationPeriod * 1000));
        //posestack.mulPose(Axis.XP.rotationDegrees(rotPitch));
        posestack.mulPose(Axis.YP.rotationDegrees(rot));
        RenderSystem.applyModelViewMatrix();
        PoseStack tmpPose = new PoseStack();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        Lighting.setupForFlatItems();

        Minecraft.getInstance().getItemRenderer().renderStatic(itemSupplier.get(), ItemDisplayContext.FIXED, 0xf000f0, OverlayTexture.NO_OVERLAY, tmpPose, bufferSource, null, 0);

        bufferSource.endBatch();
        RenderSystem.enableDepthTest();
        Lighting.setupFor3DItems();
        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
    }
}
