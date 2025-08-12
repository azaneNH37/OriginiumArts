package com.azane.ogna.craft.rlr;

import com.azane.ogna.craft.QuantifiedIngredient;
import com.azane.ogna.resource.service.IResourceProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * RLRRecipe - Resource Location Result Recipe
 * 整合了原有的Recipe、Serializer、Type和Helper功能
 * @author azaneNH37 (2025-08-09)
 */
public class RLRRecipe implements Recipe<Container> {

    // Recipe Type单例
    public static final RLRRecipeType TYPE = new RLRRecipeType();
    // Recipe Serializer单例
    public static final RLRRecipeSerializer SERIALIZER = new RLRRecipeSerializer();

    private final ResourceLocation id;
    @Getter
    private final NonNullList<QuantifiedIngredient> quantifiedIngredients;
    public NonNullList<Ingredient> getIngredients()
    {
        NonNullList<Ingredient> ingredientsList = NonNullList.create();
        for (QuantifiedIngredient ingredient : quantifiedIngredients) {
            ingredientsList.add(ingredient.getIngredient());
        }
        return ingredientsList;
    }
    @Getter
    private final RLRRecipeResult result;

    public RLRRecipe(ResourceLocation id, NonNullList<QuantifiedIngredient> ingredients, RLRRecipeResult result) {
        this.id = id;
        this.quantifiedIngredients = ingredients;
        this.result = result;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return result.buildItemStack();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result.buildItemStack();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return TYPE;
    }

    /**
     * 执行合成
     */
    public boolean craft(Player player) {
        if (!canCraft(player)) return false;

        // 消耗原料
        for (QuantifiedIngredient ingredient : quantifiedIngredients) {
            consumeIngredient(player.getInventory(), ingredient);
        }

        // 给予产物
        player.drop(result.buildItemStack(), false);
        return true;
    }

    /**
     * 检查玩家是否有足够的原料
     */
    public boolean canCraft(Player player) {
        Inventory inventory = player.getInventory();

        for (QuantifiedIngredient quantifiedIngredient : quantifiedIngredients) {
            int needed = quantifiedIngredient.getCount();
            int found = 0;

            for (int i = 0; i < inventory.getContainerSize() && found < needed; i++) {
                ItemStack stack = inventory.getItem(i);
                if (quantifiedIngredient.test(stack)) {
                    found += stack.getCount();
                }
            }

            if (found < needed) return false;
        }

        return true;
    }

    /**
     * 消耗指定原料
     */
    private void consumeIngredient(Inventory inventory, QuantifiedIngredient quantifiedIngredient) {
        int needed = quantifiedIngredient.getCount();

        for (int i = 0; i < inventory.getContainerSize() && needed > 0; i++) {
            ItemStack stack = inventory.getItem(i);
            if (quantifiedIngredient.test(stack)) {
                int toConsume = Math.min(needed, stack.getCount());
                stack.shrink(toConsume);
                needed -= toConsume;
            }
        }
    }

    /**
     * Recipe Type内部类
     */
    public static class RLRRecipeType implements RecipeType<RLRRecipe> {
        public static final String ID = "rlr";

        @Override
        public String toString() {
            return ID;
        }
    }

    /**
     * Recipe Serializer内部类
     */
    public static class RLRRecipeSerializer implements RecipeSerializer<RLRRecipe> {

        @Override
        public RLRRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            // 解析原料
            JsonArray ingredientsArray = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<QuantifiedIngredient> ingredients = NonNullList.withSize(ingredientsArray.size(), QuantifiedIngredient.EMPTY);

            for (int i = 0; i < ingredientsArray.size(); i++) {
                JsonObject ingredientObj = ingredientsArray.get(i).getAsJsonObject();
                Ingredient ingredient = Ingredient.fromJson(ingredientObj.get("ingredient"));
                int count = GsonHelper.getAsInt(ingredientObj, "count", 1);
                ingredients.set(i,new QuantifiedIngredient(ingredient, count));
            }

            // 解析产物
            JsonObject resultObj = GsonHelper.getAsJsonObject(json, "result");
            RLRRecipeResult result = IResourceProvider.GSON.fromJson(resultObj, RLRRecipeResult.class);

            return new RLRRecipe(recipeId, ingredients, result);
        }

        @Override
        public @Nullable RLRRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            // 读取原料
            int ingredientCount = buffer.readVarInt();
            NonNullList<QuantifiedIngredient> ingredients = NonNullList.withSize(ingredientCount, QuantifiedIngredient.EMPTY);

            for (int i = 0; i < ingredientCount; i++) {
                Ingredient ingredient = Ingredient.fromNetwork(buffer);
                int count = buffer.readVarInt();
                ingredients.set(i,new QuantifiedIngredient(ingredient, count));
            }

            // 读取产物
            String type = buffer.readUtf();
            ResourceLocation id = buffer.readResourceLocation();
            int count = buffer.readInt();
            RLRRecipeResult result = new RLRRecipeResult(type, id, count);

            return new RLRRecipe(recipeId, ingredients, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, RLRRecipe recipe) {
            // 写入原料
            buffer.writeVarInt(recipe.getQuantifiedIngredients().size());
            for (QuantifiedIngredient ingredient : recipe.getQuantifiedIngredients()) {
                ingredient.getIngredient().toNetwork(buffer);
                buffer.writeVarInt(ingredient.getCount());
            }

            // 写入产物
            RLRRecipeResult result = recipe.getResult();
            buffer.writeUtf(result.getType());
            buffer.writeResourceLocation(result.getId());
            buffer.writeInt(result.getCount());
        }
    }
}