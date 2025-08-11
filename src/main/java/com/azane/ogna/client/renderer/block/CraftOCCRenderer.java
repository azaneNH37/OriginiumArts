package com.azane.ogna.client.renderer.block;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.block.entity.CraftOCCBlockEntity;
import com.azane.ogna.lib.RlHelper;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

/**
 * @author azaneNH37 (2025-08-10)
 */
public class CraftOCCRenderer extends GeoBlockRenderer<CraftOCCBlockEntity>
{
    public CraftOCCRenderer(BlockEntityRendererProvider.Context context)
    {
        super(new DefaultedBlockGeoModel<>(RlHelper.build(OriginiumArts.MOD_ID,"craft_occ")));
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
