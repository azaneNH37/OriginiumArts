package com.azane.ogna.craft.rlr;

import net.minecraft.world.item.crafting.RecipeType;

/**
 * @author azaneNH37 (2025-08-09)
 */
public class RlResultRecipeType implements RecipeType<RlResultRecipe> {
    public static final RlResultRecipeType INSTANCE = new RlResultRecipeType();
    public static final String ID = "rlr";

    private RlResultRecipeType() {}

    @Override
    public String toString() {
        return ID;
    }
}