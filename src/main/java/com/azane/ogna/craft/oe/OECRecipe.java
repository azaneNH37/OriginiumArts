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
public class OECRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final RecipeIngredient ingredient;
    private final ItemStack result;
    private final double energyCost;
    private final int processingTime;

    public OECRecipe(ResourceLocation id, RecipeIngredient ingredient, ItemStack result, double energyCost, int processingTime) {
        this.id = id;
        this.ingredient = ingredient;
        this.result = result;
        this.energyCost = energyCost;
        this.processingTime = processingTime;
    }

    @Override
    public boolean matches(Container container, Level level) {
        ItemStack input = container.getItem(0);
        ItemStack output = container.getItem(1);

        // 检查输入物品是否匹配
        boolean inputMatches = !input.isEmpty() && ingredient.test(input) && input.getCount() >= ingredient.getCount();

        // 检查输出槽是否可以放入结果物品
        boolean outputValid = output.isEmpty() ||
            (ItemStack.isSameItem(output, result) && output.getCount() + result.getCount() <= output.getMaxStackSize());

        return inputMatches && outputValid;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipe.OEC_TYPE.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipe.OEC_SERIALIZER.get();
    }

    public boolean canProcess(ItemStack input, ItemStack output, double availableEnergy) {
        boolean inputMatches = ingredient.test(input) && input.getCount() >= ingredient.getCount();
        boolean energyEnough = availableEnergy >= energyCost;
        boolean outputValid = output.isEmpty() ||
            (ItemStack.isSameItem(output, result) && output.getCount() + result.getCount() <= output.getMaxStackSize());

        return inputMatches && energyEnough && outputValid;
    }

    public static class Serializer implements RecipeSerializer<OECRecipe> {
        @Override
        public OECRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            JsonObject ingredientJson = GsonHelper.getAsJsonObject(json, "ingredient");
            Ingredient ingredient = Ingredient.fromJson(ingredientJson);
            int count = GsonHelper.getAsInt(ingredientJson, "count", 1);

            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            double energyCost = GsonHelper.getAsDouble(json, "energy_cost");
            int processingTime = GsonHelper.getAsInt(json, "processing_time", 200);

            return new OECRecipe(recipeId, new RecipeIngredient(ingredient, count), result, energyCost, processingTime);
        }

        @Override
        public @Nullable OECRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            int count = buffer.readVarInt();
            ItemStack result = buffer.readItem();
            double energyCost = buffer.readDouble();
            int processingTime = buffer.readVarInt();

            return new OECRecipe(recipeId, new RecipeIngredient(ingredient, count), result, energyCost, processingTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, OECRecipe recipe) {
            recipe.ingredient.getIngredient().toNetwork(buffer);
            buffer.writeVarInt(recipe.ingredient.getCount());
            buffer.writeItem(recipe.result);
            buffer.writeDouble(recipe.energyCost);
            buffer.writeVarInt(recipe.processingTime);
        }
    }
}