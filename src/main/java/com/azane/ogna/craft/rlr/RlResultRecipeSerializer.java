package com.azane.ogna.craft.rlr;

import com.azane.ogna.craft.RecipeIngredient;
import com.azane.ogna.craft.RecipeResult;
import com.azane.ogna.resource.service.IResourceProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class RlResultRecipeSerializer implements RecipeSerializer<RlResultRecipe> {
    public static final RlResultRecipeSerializer INSTANCE = new RlResultRecipeSerializer();

    @Override
    public RlResultRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        // 解析原料
        JsonArray ingredientsArray = GsonHelper.getAsJsonArray(json, "ingredients");
        List<RecipeIngredient> ingredients = new ArrayList<>();

        for (int i = 0; i < ingredientsArray.size(); i++) {
            JsonObject ingredientObj = ingredientsArray.get(i).getAsJsonObject();
            Ingredient ingredient = Ingredient.fromJson(ingredientObj.get("ingredient"));
            int count = GsonHelper.getAsInt(ingredientObj, "count", 1);
            ingredients.add(new RecipeIngredient(ingredient, count));
        }

        // 解析产物
        JsonObject resultObj = GsonHelper.getAsJsonObject(json, "result");
        RecipeResult result = IResourceProvider.GSON.fromJson(resultObj, RecipeResult.class);

        return new RlResultRecipe(recipeId, ingredients, result);
    }

    @Override
    public @Nullable RlResultRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        // 读取原料
        int ingredientCount = buffer.readVarInt();
        List<RecipeIngredient> ingredients = new ArrayList<>();

        for (int i = 0; i < ingredientCount; i++) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            int count = buffer.readVarInt();
            ingredients.add(new RecipeIngredient(ingredient, count));
        }

        // 读取产物
        String type = buffer.readUtf();
        ResourceLocation id = buffer.readResourceLocation();
        int count = buffer.readInt();
        RecipeResult result = new RecipeResult(type, id,count);

        return new RlResultRecipe(recipeId, ingredients, result);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, RlResultRecipe recipe) {
        // 写入原料
        buffer.writeVarInt(recipe.getRlrIngredients().size());
        for (RecipeIngredient ingredient : recipe.getRlrIngredients()) {
            ingredient.getIngredient().toNetwork(buffer);
            buffer.writeVarInt(ingredient.getCount());
        }

        // 写入产物
        buffer.writeUtf(recipe.getResult().getType());
        buffer.writeResourceLocation(recipe.getResult().getId());
        buffer.writeInt(recipe.getResult().getCount());
    }
}