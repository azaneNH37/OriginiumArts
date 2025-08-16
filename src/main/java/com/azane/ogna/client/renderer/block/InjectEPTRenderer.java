package com.azane.ogna.client.renderer.block;

import com.azane.ogna.OgnaConfig;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.block.entity.InjectEPTBlockEntity;
import com.azane.ogna.lib.RlHelper;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

/**
 * @author azaneNH37 (2025-08-10)
 */
public class InjectEPTRenderer extends GeoBlockRenderer<InjectEPTBlockEntity>
{
    public InjectEPTRenderer(BlockEntityRendererProvider.Context context)
    {
        super(new DefaultedBlockGeoModel<>(RlHelper.build(OriginiumArts.MOD_ID,"inject_ept")));
        if(OgnaConfig.isEnableGlowingTexture())
            addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
