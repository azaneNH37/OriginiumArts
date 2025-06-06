package com.azane.ogna.client.resource.manager;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

@Getter
public class ModelAdditionManager
{
    @SerializedName("item_models")
    private List<ResourceLocation> itemModels;
}