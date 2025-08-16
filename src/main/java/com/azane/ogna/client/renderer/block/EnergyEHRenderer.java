package com.azane.ogna.client.renderer.block;

import com.azane.ogna.OgnaConfig;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.block.entity.EnergyEHBlockEntity;
import com.azane.ogna.lib.RlHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

/**
 * @author azaneNH37 (2025-08-11)
 */
public class EnergyEHRenderer extends GeoBlockRenderer<EnergyEHBlockEntity>
{
    public EnergyEHRenderer(BlockEntityRendererProvider.Context context)
    {
        super(new DefaultedBlockGeoModel<>(RlHelper.build(OriginiumArts.MOD_ID,"energy_eh")));
        if(OgnaConfig.isEnableGlowingTexture())
            addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void actuallyRender(PoseStack poseStack, EnergyEHBlockEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        float mul = 1f;
        long time = System.currentTimeMillis()%100000;
        if (animatable.getEnergy() >= EnergyEHBlockEntity.MAX_ENERGY)
            mul = (float) (0.6f + 0.1f*Math.sin(time*0.002f));
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green*mul, blue*mul, alpha);
    }
}
