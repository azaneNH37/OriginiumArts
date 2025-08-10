package com.azane.ogna.client.renderer.block;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.block.entity.EnergyEHBlockEntity;
import com.azane.ogna.block.entity.InjectEPTBlockEntity;
import com.azane.ogna.lib.RlHelper;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class EnergyEHRenderer extends GeoBlockRenderer<EnergyEHBlockEntity>
{
    public EnergyEHRenderer(BlockEntityRendererProvider.Context context)
    {
        super(new DefaultedBlockGeoModel<>(RlHelper.build(OriginiumArts.MOD_ID,"energy_eh")));
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
