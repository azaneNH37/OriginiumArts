package com.azane.ogna.registry;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.craft.RlResultRecipe;
import com.azane.ogna.craft.RlResultRecipeSerializer;
import com.azane.ogna.craft.RlResultRecipeType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipe
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, OriginiumArts.MOD_ID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
        DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, OriginiumArts.MOD_ID);

    public static final RegistryObject<RecipeSerializer<RlResultRecipe>> RL_RESULT_SERIALIZER =
        RECIPE_SERIALIZERS.register("rlr", () -> RlResultRecipeSerializer.INSTANCE);

    public static final RegistryObject<RecipeType<RlResultRecipe>> RL_RESULT_TYPE =
        RECIPE_TYPES.register("rlr", () -> RlResultRecipeType.INSTANCE);

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }
}
