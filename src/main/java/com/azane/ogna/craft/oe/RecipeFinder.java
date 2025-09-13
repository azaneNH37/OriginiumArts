package com.azane.ogna.craft.oe;

import com.azane.ogna.registry.ModRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 配方查找器，用于查找和筛选符合条件的配方
 * @author azaneNH37 (2025-09-12)
 */
public class RecipeFinder {

    /**
     * 查找最佳的OEG配方
     * @param level 世界
     * @param pos 方块位置
     * @param inputStack 输入物品
     * @return 最佳配方，如果没有符合条件的配方则返回null
     */
    @Nullable
    public static OEGRecipe findBestOEGRecipe(Level level, BlockPos pos, ItemStack inputStack) {
        if (level == null || inputStack.isEmpty()) {
            return null;
        }

        RecipeManager recipeManager = level.getRecipeManager();
        List<OEGRecipe> validRecipes = recipeManager.getAllRecipesFor(ModRecipe.OEG_TYPE.get())
            .stream()
            .filter(recipe -> recipe.canProcess(inputStack, level, pos))
            .collect(Collectors.toList());

        if (validRecipes.isEmpty()) {
            return null;
        }

        // 排序：催化剂需求越多的配方优先级越高
        Collections.sort(validRecipes);
        return validRecipes.get(0);
    }

    /**
     * 查找最佳的OEC配方
     * @param level 世界
     * @param pos 方块位置
     * @param inputStack 输入物品
     * @param outputStack 输出物品
     * @param availableEnergy 可用能量
     * @return 最佳配方，如果没有符合条件的配方则返回null
     */
    @Nullable
    public static OECRecipe findBestOECRecipe(Level level, BlockPos pos, ItemStack inputStack,
                                              ItemStack outputStack, double availableEnergy) {
        if (level == null || inputStack.isEmpty()) {
            return null;
        }

        RecipeManager recipeManager = level.getRecipeManager();
        List<OECRecipe> validRecipes = recipeManager.getAllRecipesFor(ModRecipe.OEC_TYPE.get())
            .stream()
            .filter(recipe -> recipe.canProcess(inputStack, outputStack, availableEnergy, level, pos))
            .collect(Collectors.toList());

        if (validRecipes.isEmpty()) {
            return null;
        }

        // 排序：催化剂需求越多的配方优先级越高
        Collections.sort(validRecipes);
        return validRecipes.get(0);
    }



    /**
     * 检查是否有可用的OEG配方（不考虑催化剂）
     */
    public static boolean hasBasicOEGRecipe(Level level, ItemStack inputStack) {
        if (level == null || inputStack.isEmpty()) {
            return false;
        }

        RecipeManager recipeManager = level.getRecipeManager();
        return recipeManager.getAllRecipesFor(ModRecipe.OEG_TYPE.get())
            .stream()
            .anyMatch(recipe -> recipe.canProcess(inputStack));
    }

    /**
     * 检查是否有可用的OEC配方（不考虑催化剂）
     */
    public static boolean hasBasicOECRecipe(Level level, ItemStack inputStack, ItemStack outputStack, double availableEnergy) {
        if (level == null || inputStack.isEmpty()) {
            return false;
        }

        RecipeManager recipeManager = level.getRecipeManager();
        return recipeManager.getAllRecipesFor(ModRecipe.OEC_TYPE.get())
            .stream()
            .anyMatch(recipe -> recipe.canProcess(inputStack, outputStack, availableEnergy));
    }
}