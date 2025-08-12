package com.azane.ogna.compat.jei;

import com.azane.ogna.genable.item.base.IGenItem;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;

/**
 * @author azaneNH37 (2025/8/12)
 */
public class RLRSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {

    @Override
    public String apply(ItemStack itemStack, UidContext context)
    {
        if (itemStack.getItem() instanceof IGenItem genItem)
        {
            String id = genItem.getDatabaseId(itemStack);
            return id == null ? IIngredientSubtypeInterpreter.NONE : id;
        }
        return IIngredientSubtypeInterpreter.NONE;
    }
}