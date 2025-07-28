package com.azane.ogna.util;

import com.azane.ogna.resource.service.ServerDataService;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public final class RlrRecipes
{
    private RlrRecipes(){}

    @AllArgsConstructor(staticName = "of")
    @Getter
    public static class RecipeMapping
    {
        BiFunction<ResourceLocation,Integer, ItemStack> buildStack;
        Predicate<ResourceLocation> existence;
    }

    public static final ImmutableMap<String, RecipeMapping> map;

    static {
        ImmutableMap.Builder<String,RecipeMapping> builder = new ImmutableMap.Builder<>();
        builder.put("staff", RecipeMapping.of(
            (id,amt)->
            Optional.ofNullable(ServerDataService.get().getStaff(id)).
                map(i->i.buildItemStack(amt)).
                orElseThrow(()->new IllegalArgumentException("Staff Database not found: " + id)),
            id -> ServerDataService.get().getStaff(id) != null
            )
        );
        builder.put("skill", RecipeMapping.of(
            (id,amt)->
                Optional.ofNullable(ServerDataService.get().getSkill(id)).
                    map(i->i.buildItemStack(amt)).
                    orElseThrow(()->new IllegalArgumentException("Skill Database not found: " + id)),
            id -> ServerDataService.get().getSkill(id) != null
            )
        );
        map = builder.build();
    }
}
