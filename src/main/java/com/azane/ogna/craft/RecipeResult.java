package com.azane.ogna.craft;

import com.azane.ogna.craft.rlr.RlrRecipes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@Getter
@AllArgsConstructor
public class RecipeResult {
    private final String type;
    private final ResourceLocation id;
    private final int count;

    // 通过数据存储结构获取产物
    public ItemStack buildItemStack()
    {
        var func = RlrRecipes.map.get(type);
        if(func == null) {
            return ItemStack.EMPTY;
        }
        return func.getBuildStack().apply(id,count);
    }
}