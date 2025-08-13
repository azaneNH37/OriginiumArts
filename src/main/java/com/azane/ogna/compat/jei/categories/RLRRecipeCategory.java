package com.azane.ogna.compat.jei.categories;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.compat.jei.JeiHelper;
import com.azane.ogna.craft.rlr.RLRRecipe;
import com.azane.ogna.craft.QuantifiedIngredient;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.registry.ModBlock;
import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * @author azaneNH37 (2025/8/12)
 */
public class RLRRecipeCategory implements IRecipeCategory<RLRRecipe> {
    
    public static final RecipeType<RLRRecipe> RECIPE_TYPE = RecipeType.create(OriginiumArts.MOD_ID, "rlr", RLRRecipe.class);
    private static final ResourceLocation TEXTURE = RlHelper.build(OriginiumArts.MOD_ID, "textures/gui/jei/oe_category.png");
    
    @Getter
    private final IDrawable background;
    @Getter
    private final IDrawable icon;
    @Getter
    private final Component title;
    private final IDrawable slotBack;
    private final IDrawable progressBack;
    private final IDrawableAnimated progress;

    public final static int MAX_INGREDIENTS = 20;

    public RLRRecipeCategory(IGuiHelper guiHelper)
    {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 180, 36);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlock.CRAFT_OCC.item.get()));
        
        this.progressBack = guiHelper.createDrawable(TEXTURE, 0, 36, 64, 28);
        IDrawableStatic arrowDrawable = guiHelper.createDrawable(TEXTURE, 0, 65, 64, 28);
        this.progress = guiHelper.createAnimatedDrawable(arrowDrawable, 30, IDrawableAnimated.StartDirection.LEFT, false);
        
        this.slotBack = guiHelper.createDrawable(TEXTURE, 180, 0, 32, 32);
        this.title = Component.translatable("jei.ognmarts.category.rlr");
    }

    @Override
    public RecipeType<RLRRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RLRRecipe recipe, IFocusGroup focuses)
    {
        var ingredients = recipe.getQuantifiedIngredients();
        
        // 配置输入槽位（水平排列）
        for (int i = 0; i < Math.min(MAX_INGREDIENTS, ingredients.size()); i++) {
            QuantifiedIngredient ingredient = ingredients.get(i);
            int x = 16 + i * 20;
            int y = 10;
            
            builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                .addItemStacks(ingredient.buildStacks())
                .setSlotName("input_" + i);
        }
        
        // 配置输出槽位
        int outputX = 16 + Math.min(MAX_INGREDIENTS, ingredients.size()) * 20 + 48 - 16; // 原料槽后 + 箭头宽度
        builder.addSlot(RecipeIngredientRole.OUTPUT, outputX, 10)
            .addItemStack(recipe.getResultItem(null))
            .setSlotName("output");
    }

    @Override
    public void draw(RLRRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY)
    {
        var ingredients = recipe.getQuantifiedIngredients();
        int ingredientCount = Math.min(MAX_INGREDIENTS, ingredients.size());

        for (int i = 0; i < ingredientCount; i++) {
            int x = 24 + i * 20;
            int y = 18;
            JeiHelper.draw(graphics, x, y, 20f/32f, this.slotBack);
        }

        int arrowX = 24 + ingredientCount * 20 + 16 - 8;
        JeiHelper.draw(graphics, arrowX, 18, 0.5f, this.progressBack);
        JeiHelper.draw(graphics, arrowX, 18, 0.5f, this.progress);

        int outputX = 16 + ingredientCount * 20 + 48 + 8 - 16;
        JeiHelper.draw(graphics, outputX, 18, 20f/32f, this.slotBack);
    }
}