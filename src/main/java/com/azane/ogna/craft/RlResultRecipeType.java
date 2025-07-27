package com.azane.ogna.craft;

import net.minecraft.world.item.crafting.RecipeType;

public class RlResultRecipeType implements RecipeType<RlResultRecipe> {
    public static final RlResultRecipeType INSTANCE = new RlResultRecipeType();
    public static final String ID = "rl_result";

    private RlResultRecipeType() {}

    @Override
    public String toString() {
        return ID;
    }
}