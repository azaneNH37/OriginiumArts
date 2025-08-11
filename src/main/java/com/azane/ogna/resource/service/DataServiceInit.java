package com.azane.ogna.resource.service;

import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.genable.entity.IBladeEffect;
import com.azane.ogna.genable.entity.IBullet;
import com.azane.ogna.genable.item.chip.IChip;
import com.azane.ogna.genable.item.skill.ISkill;
import com.azane.ogna.genable.item.weapon.IStaffDataBase;
import com.azane.ogna.genable.item.weapon.ISwordDataBase;
import com.azane.ogna.resource.manager.JsonDataManager;
import net.minecraft.resources.ResourceLocation;

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
    };

    public static Consumer<JsonDataManager<IBladeEffect>> bladeEffectInit = debugRl::accept;
    public static Consumer<JsonDataManager<IBullet>> bulletInit = debugRl::accept;
    public static Consumer<JsonDataManager<IStaffDataBase>> staffInit = debugRl::accept;
    public static Consumer<JsonDataManager<ISwordDataBase>> swordInit = debugRl::accept;
    public static Consumer<JsonDataManager<ISkill>> skillInit = debugRl::accept;
    public static Consumer<JsonDataManager<IChip>> chipInit = debugRl::accept;
}
