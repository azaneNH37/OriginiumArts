package com.azane.ogna.client.renderer.weapon;

import com.azane.ogna.client.model.weapon.OgnaWeaponModel;
import com.azane.ogna.item.genable.OgnaWeapon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import static com.azane.ogna.client.lib.OffHandItemTransform.*;

public class OgnaWeaponRenderer<T extends OgnaWeapon> extends GeoItemRenderer<T>
{
    public OgnaWeaponRenderer()
    {
        super(new OgnaWeaponModel<>());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        if(getGeoModel() instanceof OgnaWeaponModel<T> ognaWeaponModel)
            ognaWeaponModel.updateCurrentItemStack(stack);
        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public void doPostRenderCleanup()
    {
        super.doPostRenderCleanup();
        if(getGeoModel() instanceof OgnaWeaponModel<T> ognaWeaponModel)
            ognaWeaponModel.updateCurrentItemStack(null);
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
}
