package com.azane.ogna.registry;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.craft.oe.OECRecipe;
import com.azane.ogna.craft.oe.OEGRecipe;
import com.azane.ogna.craft.rlr.RlResultRecipe;
import com.azane.ogna.craft.rlr.RlResultRecipeSerializer;
import com.azane.ogna.craft.rlr.RlResultRecipeType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author azaneNH37 (2025-08-09)
 */
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

    // OEG Recipe - Originium Energy Generation
    public static final RegistryObject<RecipeType<OEGRecipe>> OEG_TYPE = RECIPE_TYPES.register("oeg",
        () -> new RecipeType<>() {@Override public String toString() {return OriginiumArts.MOD_ID + ":oeg";}});

    public static final RegistryObject<RecipeSerializer<OEGRecipe>> OEG_SERIALIZER =
        RECIPE_SERIALIZERS.register("oeg", OEGRecipe.Serializer::new);

    // OEC Recipe - Originium Energy Consumption
    public static final RegistryObject<RecipeType<OECRecipe>> OEC_TYPE = RECIPE_TYPES.register("oec",
        () -> new RecipeType<>() {@Override public String toString() {return OriginiumArts.MOD_ID + ":oec";}});

    public static final RegistryObject<RecipeSerializer<OECRecipe>> OEC_SERIALIZER =
        RECIPE_SERIALIZERS.register("oec", OECRecipe.Serializer::new);


    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }
}
