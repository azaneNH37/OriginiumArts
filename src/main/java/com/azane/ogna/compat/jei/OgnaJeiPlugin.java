package com.azane.ogna.compat.jei;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.compat.jei.categories.OECRecipeCategory;
import com.azane.ogna.compat.jei.categories.OEGRecipeCategory;
import com.azane.ogna.craft.oe.OECRecipe;
import com.azane.ogna.craft.oe.OEGRecipe;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.registry.ModBlock;
import com.azane.ogna.registry.ModRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author azaneNH37 (2025/8/11)
 */
@JeiPlugin
public class OgnaJeiPlugin implements IModPlugin
{
    public static final ResourceLocation UID = RlHelper.build(OriginiumArts.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {return UID;}

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
            new OECRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            new OEGRecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        // 注册OEC配方（耗能配方）
        List<OECRecipe> oecRecipes = recipeManager.getAllRecipesFor(ModRecipe.OEC_TYPE.get());
        registration.addRecipes(OECRecipeCategory.RECIPE_TYPE, oecRecipes);

        // 注册OEG配方（产能配方）
        List<OEGRecipe> oegRecipes = recipeManager.getAllRecipesFor(ModRecipe.OEG_TYPE.get());
        registration.addRecipes(OEGRecipeCategory.RECIPE_TYPE, oegRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        registration.addRecipeCatalyst(new ItemStack(ModBlock.ENERGY_EH.item.get()),
            OECRecipeCategory.RECIPE_TYPE, OEGRecipeCategory.RECIPE_TYPE);
    }
}
