package com.azane.ogna.resource.service;

import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.genable.entity.IBladeEffect;
import com.azane.ogna.genable.entity.IBullet;
import com.azane.ogna.genable.item.chip.IChip;
import com.azane.ogna.genable.item.skill.ISkill;
import com.azane.ogna.genable.item.weapon.IStaffDataBase;
import com.azane.ogna.genable.item.weapon.ISwordDataBase;
import com.azane.ogna.lib.IComponentDisplay;
import com.azane.ogna.resource.manager.JsonDataManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author azaneNH37 (2025-08-04)
 */
public class DataServiceInit
{
    public static Consumer<JsonDataManager<?>> debugRl = jm ->{
        DebugLogger.info(jm.getMarker(),
                "DataServiceInit with {} entries: \n[{}]",
                jm.getAllDataEntries().size(),
                jm.getAllDataEntries().stream()
                    .map(Map.Entry::getKey).map(ResourceLocation::toString)
                    .sorted()
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("No data found"));
        /*
        if(IComponentDisplay.class.isAssignableFrom(jm.getDataClass()))
        {
            jm.getAllDataEntries().stream()
                .map(Map.Entry::getValue)
                .map(d -> (IComponentDisplay)d)
                .forEach(d -> {
                    List<Component> tooltip = new ArrayList<>();
                    d.appendHoverText(ItemStack.EMPTY, tooltip, TooltipFlag.NORMAL);
                    tooltip.stream().map(Component::getString).reduce((a, b) -> a + "\n" + b).ifPresent(s -> DebugLogger.info(jm.getMarker(), "\n{}", s));
                });
        }
         */
    };

    public static Consumer<JsonDataManager<IBladeEffect>> bladeEffectInit = debugRl::accept;
    public static Consumer<JsonDataManager<IBullet>> bulletInit = debugRl::accept;
    public static Consumer<JsonDataManager<IStaffDataBase>> staffInit = debugRl::accept;
    public static Consumer<JsonDataManager<ISwordDataBase>> swordInit = debugRl::accept;
    public static Consumer<JsonDataManager<ISkill>> skillInit = debugRl::accept;
    public static Consumer<JsonDataManager<IChip>> chipInit = debugRl::accept;
}
