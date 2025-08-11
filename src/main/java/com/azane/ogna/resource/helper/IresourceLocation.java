package com.azane.ogna.resource.helper;

import net.minecraft.resources.ResourceLocation;

/**
 * @author azaneNH37 (2025-06-06)
 */
public interface IresourceLocation
{
    String TAG_RL = "id";
    /**
     * 获取特效的ID
     * @return ID
     */
    ResourceLocation getId();
    /**
     * 设置特效的ID
     * @param id ID
     */
    void setId(ResourceLocation id);
}
