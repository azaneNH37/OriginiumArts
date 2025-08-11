package com.azane.ogna.client.resource.manager;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * @author azaneNH37 (2025-06-06)
 */
@Getter
public class ModelAdditionManager
{
    @SerializedName("item_models")
    private List<ResourceLocation> itemModels;
}