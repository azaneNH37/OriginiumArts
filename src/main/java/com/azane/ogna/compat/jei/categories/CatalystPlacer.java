package com.azane.ogna.compat.jei.categories;

import com.azane.ogna.block.CatalystBlock;
import com.azane.ogna.craft.catalyst.CatalystRequirement;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

/**
 * @author azaneNH37 (2025/9/13)
 */
public class CatalystPlacer
{
    public static void placeCatalyst(IRecipeLayoutBuilder builder, CatalystRequirement requirement, int centerX,int centerY)
    {
        int typeCnt = requirement.getRequirements().size();
        if(typeCnt==0) return;
        int interval = 18;
        int index = 0;
        for (Map.Entry<CatalystBlock.Type, Integer> entry : requirement.getRequirements().entrySet()) {
            CatalystBlock.Type type = entry.getKey();
            int count = entry.getValue();
            int x = centerX - (typeCnt - 1) * interval / 2 + index * interval - 8;
            int y = centerY  - 8; // -8 to center the slot
            builder.addSlot(RecipeIngredientRole.CATALYST, x, y)
                .addItemStack(new ItemStack(type.itemBlock.get().item.get(), count));
            index++;
        }
    }
}
