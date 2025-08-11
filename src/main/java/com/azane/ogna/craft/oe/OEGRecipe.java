package com.azane.ogna.craft.oe;

import com.azane.ogna.craft.RecipeIngredient;
import com.azane.ogna.registry.ModRecipe;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * @author azaneNH37 (2025-08-09)
 */
@Getter
public class OEGRecipe implements Recipe<Container>
{
    private final ResourceLocation id;
    private final RecipeIngredient ingredient;
    private final double energyOutput;
    private final int processingTime;

    public OEGRecipe(ResourceLocation id, RecipeIngredient ingredient, double energyOutput, int processingTime) {
        this.id = id;
        this.ingredient = ingredient;
        this.energyOutput = energyOutput;
        this.processingTime = processingTime;
    }

    @Override
    public boolean matches(Container container, Level level) {
        ItemStack input = container.getItem(0);
        return !input.isEmpty() && ingredient.test(input) && input.getCount() >= ingredient.getCount();
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY; // 能量生成配方不产出物品
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipe.OEG_TYPE.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipe.OEG_SERIALIZER.get();
    }

    public boolean canProcess(ItemStack input) {
        return ingredient.test(input) && input.getCount() >= ingredient.getCount();
    }

    public static class Serializer implements RecipeSerializer<OEGRecipe> {
        @Override
        public OEGRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            JsonObject ingredientJson = GsonHelper.getAsJsonObject(json, "ingredient");
            Ingredient ingredient = Ingredient.fromJson(ingredientJson);
            int count = GsonHelper.getAsInt(ingredientJson, "count", 1);

            double energyOutput = GsonHelper.getAsDouble(json, "energy_output");
            int processingTime = GsonHelper.getAsInt(json, "processing_time", 200);

            return new OEGRecipe(recipeId, new RecipeIngredient(ingredient, count), energyOutput, processingTime);
        }

        @Override
        public @Nullable OEGRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            int count = buffer.readVarInt();
            double energyOutput = buffer.readDouble();
            int processingTime = buffer.readVarInt();

            return new OEGRecipe(recipeId, new RecipeIngredient(ingredient, count), energyOutput, processingTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, OEGRecipe recipe) {
            recipe.ingredient.getIngredient().toNetwork(buffer);
            buffer.writeVarInt(recipe.ingredient.getCount());
            buffer.writeDouble(recipe.energyOutput);
            buffer.writeVarInt(recipe.processingTime);
        }
    }
}