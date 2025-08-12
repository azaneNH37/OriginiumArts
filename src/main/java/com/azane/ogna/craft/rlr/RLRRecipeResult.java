package com.azane.ogna.craft.rlr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * @author azaneNH37 (2025-08-09)
 */
@Getter
@AllArgsConstructor
public class RLRRecipeResult
{
    private final String type;
    private final ResourceLocation id;
    private final int count;

    // 通过数据存储结构获取产物
    public ItemStack buildItemStack()
    {
        var func = RLRRecipes.map.get(type);
        if(func == null) {
            return ItemStack.EMPTY;
        }
        return func.getBuildStack().apply(id,count);
    }
}