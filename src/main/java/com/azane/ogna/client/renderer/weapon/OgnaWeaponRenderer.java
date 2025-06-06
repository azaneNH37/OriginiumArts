package com.azane.ogna.client.renderer.weapon;

import com.azane.ogna.client.model.weapon.OgnaWeaponModel;
import com.azane.ogna.item.genable.OgnaWeapon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import static com.azane.ogna.client.lib.OffHandItemTransform.*;

public class OgnaWeaponRenderer<T extends OgnaWeapon> extends GeoItemRenderer<T>
{
    private final Minecraft minecraft;
    private ItemRenderer itemRenderer;
    private ModelManager modelManager;

    public OgnaWeaponRenderer()
    {
        super(new OgnaWeaponModel<>());
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
        if(!isReRender && needFreeTransform(renderPerspective))
        {
            applyFreeItemTransform(poseStack, renderPerspective, getCurrentDatums(animatable,currentItemStack,renderPerspective));
            preRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        }
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    protected void renderInGui(ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        BakedModel bakedModel = modelManager.getModel(animatable.getGuiModel(currentItemStack));
        poseStack.popPose();
        poseStack.pushPose();
        itemRenderer.render(currentItemStack, transformType, false,poseStack, bufferSource, packedLight, packedOverlay, bakedModel);
        //super.renderInGui(transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }
}
