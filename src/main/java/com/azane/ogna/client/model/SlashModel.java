package com.azane.ogna.client.model;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.entity.SlashEntity;
import com.azane.ogna.util.GeoExtendUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.molang.MolangParser;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SlashModel extends DefaultedEntityGeoModel<SlashEntity>
{
    public SlashModel()
    {
        super(ResourceLocation.tryBuild(OriginiumArts.MOD_ID,"slash"));
    }

    @Override
    public RenderType getRenderType(SlashEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }

    @Override
    public void applyMolangQueries(SlashEntity animatable, double animTime)
    {
        super.applyMolangQueries(animatable,animTime);
        MolangParser parser = MolangParser.INSTANCE;
        //OriginiumArts.LOGGER.warn("has_key:{}",MolangParser.VARIABLES.containsKey(GeoExtendUtil.ML_ENTITY_Y_ROT));
        //OriginiumArts.LOGGER.warn("val:{}",parser.getVariable(GeoExtendUtil.ML_ENTITY_Y_ROT).get());
        parser.setValue(GeoExtendUtil.ML_ENTITY_Y_ROT, animatable::getYRot);
    }
}
