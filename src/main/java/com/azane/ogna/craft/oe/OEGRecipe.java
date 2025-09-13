package com.azane.ogna.craft.oe;

import com.azane.ogna.craft.QuantifiedIngredient;
import com.azane.ogna.craft.catalyst.CatalystRequirement;
import com.azane.ogna.craft.catalyst.CatalystScanner;
import com.azane.ogna.registry.ModRecipe;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.core.BlockPos;
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
 * 增强的发电配方，支持催化剂需求
 * @author azaneNH37 (2025-09-12)
 */
@Getter
public class OEGRecipe implements Recipe<Container>, Comparable<OEGRecipe> {
    private final ResourceLocation id;
    private final QuantifiedIngredient ingredient;
    private final double energyOutput;
    private final int processingTime;
    private final CatalystRequirement catalystRequirement;

    public OEGRecipe(ResourceLocation id, QuantifiedIngredient ingredient, double energyOutput,
                     int processingTime, CatalystRequirement catalystRequirement) {
        this.id = id;
        this.ingredient = ingredient;
        this.energyOutput = energyOutput;
        this.processingTime = processingTime;
        this.catalystRequirement = catalystRequirement != null ? catalystRequirement : new CatalystRequirement();
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

    /**
     * 检查是否可以处理（包括催化剂检查）
     */
    public boolean canProcess(ItemStack input, Level level, BlockPos pos) {
        boolean inputMatches = ingredient.test(input) && input.getCount() >= ingredient.getCount();
        boolean catalystValid = catalystRequirement.isEmpty() ||
            CatalystScanner.checkRequirement(level, pos, catalystRequirement);

        return inputMatches && catalystValid;
    }

    /**
     * 兼容旧版本的canProcess方法
     */
    public boolean canProcess(ItemStack input) {
        return ingredient.test(input) && input.getCount() >= ingredient.getCount();
    }

    /**
     * 配方优先级比较：所需催化剂数量越多，优先级越高
     */
    @Override
    public int compareTo(OEGRecipe other) {
        return Integer.compare(other.catalystRequirement.getTotalRequiredCount(),
            this.catalystRequirement.getTotalRequiredCount());
    }

    public static class Serializer implements RecipeSerializer<OEGRecipe> {
        @Override
        public OEGRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            JsonObject ingredientJson = GsonHelper.getAsJsonObject(json, "ingredient");
            Ingredient ingredient = Ingredient.fromJson(ingredientJson);
            int count = GsonHelper.getAsInt(ingredientJson, "count", 1);

            double energyOutput = GsonHelper.getAsDouble(json, "energy_output");
            int processingTime = GsonHelper.getAsInt(json, "processing_time", 200);

            CatalystRequirement catalystRequirement = CatalystRequirement.fromJson(json.get("catalysts"));

            return new OEGRecipe(recipeId, new QuantifiedIngredient(ingredient, count),
                energyOutput, processingTime, catalystRequirement);
        }

        @Override
        public @Nullable OEGRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            int count = buffer.readVarInt();
            double energyOutput = buffer.readDouble();
            int processingTime = buffer.readVarInt();
            CatalystRequirement catalystRequirement = CatalystRequirement.fromNetwork(buffer);

            return new OEGRecipe(recipeId, new QuantifiedIngredient(ingredient, count),
                energyOutput, processingTime, catalystRequirement);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, OEGRecipe recipe) {
            recipe.ingredient.getIngredient().toNetwork(buffer);
            buffer.writeVarInt(recipe.ingredient.getCount());
            buffer.writeDouble(recipe.energyOutput);
            buffer.writeVarInt(recipe.processingTime);
            recipe.catalystRequirement.toNetwork(buffer);
        }
    }
}