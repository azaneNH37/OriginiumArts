package com.azane.ogna.util;

import com.azane.ogna.resource.service.ServerDataService;
import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class RlrRecipes
{
    private RlrRecipes(){}

    public static final ImmutableMap<String, BiFunction<ResourceLocation,Integer, ItemStack>> map;

    static {
        ImmutableMap.Builder<String,BiFunction<ResourceLocation,Integer,ItemStack>> builder = new ImmutableMap.Builder<>();
        builder.put("staff", (id,amt)->
            Optional.ofNullable(ServerDataService.get().getStaff(id)).
                map(i->i.buildItemStack(amt)).
                orElseThrow(()->new IllegalArgumentException("Staff Database not found: " + id)));
        builder.put("skill", (id,amt)->
            Optional.ofNullable(ServerDataService.get().getSkill(id)).
                map(i->i.buildItemStack(amt)).
                orElseThrow(()->new IllegalArgumentException("Skill Database not found: " + id)));
        map = builder.build();
    }
}
