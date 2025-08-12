package com.azane.ogna.compat.jei.categories;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.compat.jei.JeiHelper;
import com.azane.ogna.craft.oe.OECRecipe;
import com.azane.ogna.craft.oe.OEGRecipe;
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

import java.util.Arrays;

/**
 * @author azaneNH37 (2025/8/12)
 */
public class OECRecipeCategory implements IRecipeCategory<OECRecipe>
{
    public static final RecipeType<OECRecipe> RECIPE_TYPE = RecipeType.create(OriginiumArts.MOD_ID, "oec", OECRecipe.class);
    private static final ResourceLocation TEXTURE = RlHelper.build(OriginiumArts.MOD_ID, "textures/gui/jei/oe_category.png");

    @SuppressWarnings("deprecation")
    @Getter
    private final IDrawable background;
    @Getter
    private final IDrawable icon;
    @Getter
    private final Component title;
    private final IDrawable slotBack;
    private final IDrawable progressBack;
    private final IDrawableAnimated progress;
    private final IDrawable energyIcon;


    public OECRecipeCategory(IGuiHelper guiHelper)
    {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 180, 36);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlock.ENERGY_EH.item.get()));


        this.progressBack = guiHelper.createDrawable(TEXTURE, 0, 36, 64, 28);
        IDrawableStatic arrowDrawable = guiHelper.createDrawable(TEXTURE, 0, 65, 64, 28);
        this.progress = guiHelper.createAnimatedDrawable(arrowDrawable, 30, IDrawableAnimated.StartDirection.LEFT, false);

        this.slotBack = guiHelper.createDrawable(TEXTURE, 180, 0, 32, 32);
        this.energyIcon = guiHelper.createDrawable(TEXTURE, 2, 95, 49, 49);

        this.title = Component.translatable("jei.ognmarts.category.oec");
    }

    @Override
    public RecipeType<OECRecipe> getRecipeType() {return RECIPE_TYPE;}

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, OECRecipe recipe, IFocusGroup focuses)
    {
        builder.addSlot(RecipeIngredientRole.INPUT, 32, 10)
            .addItemStacks(recipe.getIngredient().buildStacks())
            .setSlotName("input");
        builder.addSlot(RecipeIngredientRole.OUTPUT,92,10)
            .addItemStack(recipe.getResult())
            .setSlotName("output");
    }

    @Override
    public void draw(OECRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY)
    {
        JeiHelper.draw(graphics,40,18,20f/32f, this.slotBack);
        JeiHelper.draw(graphics, 70, 18, 0.5f, this.progressBack);
        JeiHelper.draw(graphics, 70, 18, 0.5f, this.progress);
        JeiHelper.draw(graphics,100,18,20f/32f, this.slotBack);
        JeiHelper.draw(graphics,120,18, 0.3f, this.energyIcon);

        Font font = Minecraft.getInstance().font;
        String energyText = String.format("-%.1f OE", recipe.getEnergyCost());
        graphics.drawString(font,energyText, 130, 15, 0xFFFFF9D9);
    }
}
