package com.azane.ogna.craft.rlr;

import com.azane.ogna.craft.RecipeIngredient;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * @author azaneNH37 (2025-08-09)
 */
public class RlrCraftHelper
{

    // 检查玩家是否有足够的原料
    public static boolean canCraft(Player player, RlResultRecipe recipe) {
        Inventory inventory = player.getInventory();

        for (RecipeIngredient recipeIngredient : recipe.getRlrIngredients()) {
            int needed = recipeIngredient.getCount();
            int found = 0;

            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (recipeIngredient.test(stack)) {
                    found += stack.getCount();
                    if (found >= needed) break;
                }
            }

            if (found < needed) {
                return false;
            }
        }

        return true;
    }

    // 执行合成
    public static boolean executeCraft(Player player, RlResultRecipe recipe) {
        if (!canCraft(player, recipe)) {
            return false;
        }

        // 扣除原料
        for (RecipeIngredient recipeIngredient : recipe.getRlrIngredients()) {
            consumeIngredient(player.getInventory(), recipeIngredient);
        }

        // 给予产物
        ItemStack result = recipe.getResult().buildItemStack();
        player.drop(result, false);

        return true;
    }

    // 消耗指定原料
    private static void consumeIngredient(Inventory inventory, RecipeIngredient recipeIngredient) {
        int needed = recipeIngredient.getCount();

        for (int i = 0; i < inventory.getContainerSize() && needed > 0; i++) {
            ItemStack stack = inventory.getItem(i);
            if (recipeIngredient.test(stack)) {
                int toConsume = Math.min(needed, stack.getCount());
                stack.shrink(toConsume);
                needed -= toConsume;
            }
        }
    }

    // 获取玩家拥有的指定原料数量
    public static int getIngredientCount(Player player, RecipeIngredient recipeIngredient) {
        Inventory inventory = player.getInventory();
        int count = 0;

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (recipeIngredient.test(stack)) {
                count += stack.getCount();
            }
        }

        return count;
    }
}