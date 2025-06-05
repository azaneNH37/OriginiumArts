package com.azane.ogna.resource.helper;

import net.minecraft.resources.ResourceLocation;

public interface IresourceLocation
{
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
