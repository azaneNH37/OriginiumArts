package com.azane.ogna.client.model;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.item.StaffItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class StaffModel extends DefaultedItemGeoModel<StaffItem>
{
    public StaffModel()
    {
        super(ResourceLocation.tryBuild(OriginiumArts.MOD_ID,"staff"));
    }
}
