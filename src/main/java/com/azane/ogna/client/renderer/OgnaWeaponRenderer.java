package com.azane.ogna.client.renderer;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.lib.RenderUtils;
import com.azane.ogna.client.renderer.layer.GlowingLayer;
import com.azane.ogna.client.model.weapon.OgnaWeaponModel;
import com.azane.ogna.item.weapon.OgnaWeapon;
import com.azane.ogna.lib.RlHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import static com.azane.ogna.client.lib.OffHandItemTransform.*;

/**
 * @author azaneNH37 (2025-08-04)
 */
public class OgnaWeaponRenderer<T extends OgnaWeapon> extends GeoItemRenderer<T>
{
    private static final ResourceLocation LEVEL = RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/weapon/level.png");

    private final Minecraft minecraft;
    private ItemRenderer itemRenderer;
    private ModelManager modelManager;

    public OgnaWeaponRenderer()
    {
        super(new OgnaWeaponModel<>());
        addRenderLayer(new GlowingLayer<>(this));
        minecraft = Minecraft.getInstance();
        itemRenderer = minecraft.getItemRenderer();
        modelManager = minecraft.getModelManager();
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        if(getGeoModel() instanceof OgnaWeaponModel<T> ognaWeaponModel)
            ognaWeaponModel.updateCurrentRender(stack,transformType);
        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public void doPostRenderCleanup()
    {
        super.doPostRenderCleanup();
        if(getGeoModel() instanceof OgnaWeaponModel<T> ognaWeaponModel)
            ognaWeaponModel.updateCurrentRender(null,null);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        PoseStack copyPose = poseStack;
        if(needFreeTransform(renderPerspective))
        {
            copyPose = RenderUtils.copyPoseStack(poseStack);
            applyFreeItemTransform(copyPose, renderPerspective, getCurrentDatums(animatable,currentItemStack,renderPerspective));
            preRender(copyPose, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        }
        super.actuallyRender(copyPose, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    protected void renderInGui(ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        BakedModel bakedModel = modelManager.getModel(animatable.getGuiModel(currentItemStack));
        poseStack.popPose();
        poseStack.pushPose();

        /*
        poseStack.pushPose();
        poseStack.translate(-0.5F, -0.5F, 0.0F);
        RenderUtils.renderRectTexture(poseStack, bufferSource, LEVEL,
            0.0F, 0.0F, 1.0F, 1.0F, -0.01F,
            new int[]{255, 100, 100, 255}, packedLight, packedOverlay);
        poseStack.popPose();
         */

        itemRenderer.render(currentItemStack, transformType, false,poseStack, bufferSource, packedLight, packedOverlay, bakedModel);
    }
}
