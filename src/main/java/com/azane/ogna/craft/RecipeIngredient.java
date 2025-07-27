package com.azane.ogna.craft;

import lombok.Getter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

@Getter
public class RecipeIngredient
{
    private final Ingredient ingredient;
    private final int count;

    public RecipeIngredient(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }

    public RecipeIngredient(Item item, int count) {
        this(Ingredient.of(item), count);
    }

    public RecipeIngredient(ItemStack stack) {
        this(Ingredient.of(stack), stack.getCount());
    }

    public boolean test(ItemStack stack) {
        return ingredient.test(stack);
    }

}