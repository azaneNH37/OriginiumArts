package com.azane.ogna.client.lib;

import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public interface IOffHandItem
{
    /**
     *
     * @param animeHash - 用于标识当前动画的哈希值
     * @return 当前动画的Datums数据
     */
    Datums getCurrentAnimeDatums(ItemStack stack, ItemDisplayContext context, int animeHash);

    String getControllerName(ItemStack stack);
}