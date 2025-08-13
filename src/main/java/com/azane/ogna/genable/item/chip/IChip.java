package com.azane.ogna.genable.item.chip;

import com.azane.ogna.capability.skill.ISkillCap;
import com.azane.ogna.capability.weapon.IOgnaWeaponCap;
import com.azane.ogna.combat.chip.ChipArg;
import com.azane.ogna.combat.chip.ChipSet;
import com.azane.ogna.combat.chip.ChipTiming;
import com.azane.ogna.combat.data.ArkDamageSource;
import com.azane.ogna.combat.data.CombatUnit;
import com.azane.ogna.combat.data.SelectorUnit;
import com.azane.ogna.genable.data.display.ChipDisplayContext;
import com.azane.ogna.genable.item.base.IGenItemDatabase;
import com.azane.ogna.lib.IComponentDisplay;
import com.azane.ogna.resource.helper.IresourceLocation;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 *  一个旨在实现模块化在实体，Ogna武器引入时点类效果修正的接口。
 * @author azaneNH37 (2025-08-11)
 */
public interface IChip extends IresourceLocation, IGenItemDatabase, IComponentDisplay
{
    default ChipDisplayContext getDisplayContext() {return ChipDisplayContext.EMPTY;}

    default MutableComponent getHeader()
    {
        return Component.empty()
            .append(Component.translatable(getDisplayContext().getName()).withStyle(Style.EMPTY.withColor(getDisplayContext().getColor())).withStyle(ChatFormatting.BOLD))
            .append(" - ");
    }

    boolean isItem();

    boolean canPlugIn(ChipSet chipSet, ChipArg arg);

    List<ChipTiming> registerTiming();

    int getVolumeConsume(ChipSet chipSet, ChipArg arg);

    default void onInsert(ChipSet chipSet, ChipArg arg) {}

    default void onRemove(ChipSet chipSet, ChipArg arg){}

    default void onImpactEntity(ServerLevel level, LivingEntity target, CombatUnit combatUnit, SelectorUnit selectorUnit, ArkDamageSource damageSource){}

    default void onSkillEnd(Level level, Player player, ItemStack stack, IOgnaWeaponCap cap, ISkillCap skillCap){}
}
