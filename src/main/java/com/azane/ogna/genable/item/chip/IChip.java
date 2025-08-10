package com.azane.ogna.genable.item.chip;

import com.azane.ogna.combat.chip.ChipArg;
import com.azane.ogna.combat.chip.ChipSet;
import com.azane.ogna.combat.chip.ChipTiming;
import com.azane.ogna.combat.data.ArkDamageSource;
import com.azane.ogna.combat.data.CombatUnit;
import com.azane.ogna.combat.data.SelectorUnit;
import com.azane.ogna.genable.data.display.ChipDisplayContext;
import com.azane.ogna.genable.item.base.IGenItemDatabase;
import com.azane.ogna.resource.helper.IresourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/**
 * 一个旨在实现模块化在实体，Ogna武器引入时点类效果修正的接口。
 */
public interface IChip extends IresourceLocation, IGenItemDatabase
{
    default ChipDisplayContext getDisplayContext() {return ChipDisplayContext.EMPTY;}

    boolean isItem();

    boolean canPlugIn(ChipSet chipSet, ChipArg arg);

    List<ChipTiming> registerTiming();

    int getVolumeConsume(ChipSet chipSet, ChipArg arg);

    default void onInsert(ChipSet chipSet, ChipArg arg) {}

    default void onRemove(ChipSet chipSet, ChipArg arg){}

    default void onImpactEntity(ServerLevel level, LivingEntity target, CombatUnit combatUnit, SelectorUnit selectorUnit, ArkDamageSource damageSource){};
}
