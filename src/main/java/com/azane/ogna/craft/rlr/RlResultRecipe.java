package com.azane.ogna.craft.rlr;

import com.azane.ogna.craft.RecipeIngredient;
import com.azane.ogna.craft.RecipeResult;
import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class RlResultRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    // Getters
    @Getter
    private final List<RecipeIngredient> rlrIngredients;
    @Getter
    private final RecipeResult result;

    public RlResultRecipe(ResourceLocation id, List<RecipeIngredient> ingredients, RecipeResult result) {
        this.id = id;
        this.rlrIngredients = ingredients;
        this.result = result;
    }

    @Override
    public boolean matches(Container container, Level level) {return false;}

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {return result.buildItemStack();}

    @Override
    public boolean canCraftInDimensions(int width, int height) {return true;}

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {return result.buildItemStack();}

    @Override
    public ResourceLocation getId() {return id;}

    @Override
    public RecipeSerializer<?> getSerializer() {return RlResultRecipeSerializer.INSTANCE;}

    @Override
    public RecipeType<?> getType() {return RlResultRecipeType.INSTANCE;}

    // 执行合成
    public boolean craft(Player player) {
        return RlrCraftHelper.executeCraft(player, this);
    }

}