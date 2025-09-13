package com.azane.ogna.craft.oe;

import com.azane.ogna.craft.QuantifiedIngredient;
import com.azane.ogna.craft.catalyst.CatalystRequirement;
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
 * 增强的制造配方，支持催化剂需求
 * @author azaneNH37 (2025-09-12)
 */
@Getter
public class OECRecipe implements Recipe<Container>, Comparable<OECRecipe> {
    private final ResourceLocation id;
    private final QuantifiedIngredient ingredient;
    private final ItemStack result;
    private final double energyCost;
    private final int processingTime;
    private final CatalystRequirement catalystRequirement;

    public OECRecipe(ResourceLocation id, QuantifiedIngredient ingredient, ItemStack result,
                     double energyCost, int processingTime, CatalystRequirement catalystRequirement) {
        this.id = id;
        this.ingredient = ingredient;
        this.result = result;
        this.energyCost = energyCost;
        this.processingTime = processingTime;
        this.catalystRequirement = catalystRequirement != null ? catalystRequirement : new CatalystRequirement();
    }

    @Override
    public boolean matches(Container container, Level level) {return false;}
    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {return ItemStack.EMPTY;}
    @Override
    public boolean canCraftInDimensions(int width, int height) {return true;}
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {return ItemStack.EMPTY;}
    @Override
    public RecipeType<?> getType() {return ModRecipe.OEC_TYPE.get();}
    @Override
    public RecipeSerializer<?> getSerializer() {return ModRecipe.OEC_SERIALIZER.get();}

    public record ProcessContext(ItemStack input, ItemStack output, double availableEnergy, CatalystRequirement catalystProvide) { }

    public boolean canProcess(ProcessContext context)
    {
        boolean inputMatches = ingredient.test(context.input) && context.input.getCount() >= ingredient.getCount();
        boolean energyEnough = context.availableEnergy >= energyCost;
        boolean outputValid = context.output.isEmpty() ||
            (ItemStack.isSameItem(context.output, result) && context.output.getCount() + result.getCount() <= context.output.getMaxStackSize());
        boolean catalystValid = catalystRequirement.isEmpty() || (context.catalystProvide != null && catalystRequirement.isSatisfiedBy(context.catalystProvide));

        return inputMatches && energyEnough && outputValid && catalystValid;
    }

    /**
     * 配方优先级比较：所需催化剂数量越多，优先级越高
     */
    @Override
    public int compareTo(OECRecipe other) {
        return Integer.compare(other.catalystRequirement.getTotalRequiredCount(),
            this.catalystRequirement.getTotalRequiredCount());
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

            CatalystRequirement catalystRequirement = CatalystRequirement.fromJson(json.get("catalysts"));

            return new OECRecipe(recipeId, new QuantifiedIngredient(ingredient, count),
                result, energyCost, processingTime, catalystRequirement);
        }

        @Override
        public @Nullable OECRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            int count = buffer.readVarInt();
            ItemStack result = buffer.readItem();
            double energyCost = buffer.readDouble();
            int processingTime = buffer.readVarInt();
            CatalystRequirement catalystRequirement = CatalystRequirement.fromNetwork(buffer);

            return new OECRecipe(recipeId, new QuantifiedIngredient(ingredient, count),
                result, energyCost, processingTime, catalystRequirement);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, OECRecipe recipe) {
            recipe.ingredient.getIngredient().toNetwork(buffer);
            buffer.writeVarInt(recipe.ingredient.getCount());
            buffer.writeItem(recipe.result);
            buffer.writeDouble(recipe.energyCost);
            buffer.writeVarInt(recipe.processingTime);
            recipe.catalystRequirement.toNetwork(buffer);
        }
    }
}