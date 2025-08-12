package com.azane.ogna.craft;

import lombok.Getter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.List;

/**
 * @author azaneNH37 (2025-07-28)
 */
@Getter
public class QuantifiedIngredient
{
    public static final QuantifiedIngredient EMPTY = new QuantifiedIngredient(Ingredient.EMPTY, 0);

    private final Ingredient ingredient;
    private final int count;

    public QuantifiedIngredient(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }

    public QuantifiedIngredient(Item item, int count) {
        this(Ingredient.of(item), count);
    }

    public QuantifiedIngredient(ItemStack stack) {
        this(Ingredient.of(stack), stack.getCount());
    }

    public boolean test(ItemStack stack) {
        return ingredient.test(stack);
    }

    public List<ItemStack> buildStacks()
    {
        return Arrays.stream(ingredient.getItems())
                .map(itemStack -> {
                    ItemStack stack = itemStack.copy();
                    stack.setCount(count);
                    return stack;
                })
                .toList();
    }

    public int getIngredientCount(Player player) {
        Inventory inventory = player.getInventory();
        int count = 0;

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (this.test(stack)) {
                count += stack.getCount();
            }
        }

        return count;
    }
}