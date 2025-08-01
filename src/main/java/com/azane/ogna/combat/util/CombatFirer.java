package com.azane.ogna.combat.util;

import com.azane.ogna.capability.weapon.IOgnaWeaponCap;
import com.azane.ogna.combat.chip.ChipTiming;
import com.azane.ogna.combat.data.CombatUnit;
import com.azane.ogna.combat.data.DmgDataSet;
import com.azane.ogna.combat.data.SelectorUnit;
import com.azane.ogna.genable.data.AtkEntityData;
import com.azane.ogna.genable.item.chip.IChip;
import com.azane.ogna.genable.item.skill.ISkill;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.azane.ogna.util.AtkEntityHelper;
import com.google.common.collect.ImmutableList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public final class CombatFirer
{
    public static void fire(AtkEntityHelper.AtkEntityConsumer consumer,
                            ServerLevel level, ServerPlayer player,
                            IOgnaWeapon weapon, IOgnaWeaponCap weaponCap,
                            ItemStack stack,String AEunitID,String DDunitID,double baseVal)
    {
        ISkill skill = weaponCap.getSkillCap().getSkill();

        AtkEntityData w_ae = weapon.getDefaultDatabase(stack).getAtkEntities();
        AtkEntityData s_ae = skill == null ? null : skill.getAtkEntities();
        AtkEntityData.AtkUnit AEunit = s_ae == null ? w_ae.getAtkUnit(AEunitID) :
            s_ae.hasAtkUnit(AEunitID) ? s_ae.getAtkUnit(AEunitID) : w_ae.getAtkUnit(AEunitID);

        DmgDataSet w_dd = weapon.getDefaultDatabase(stack).getOgnaWeaponData().getDmgDataSet();
        DmgDataSet s_dd = skill == null ? null : skill.getSkillData().getDmgDataSet();
        DmgDataSet.DamageData DDunit = s_dd == null ? w_dd.getDamageData(DDunitID) :
            DmgDataSet.DamageData.combine(s_dd.getDamageData(DDunitID),w_dd.getDamageData(DDunitID));

        ImmutableList.Builder<OnImpactEntity> builder = new ImmutableList.Builder<>();
        weaponCap.getChipSet().gather(ChipTiming.ON_HIT_ENTITY).forEach(chip -> builder.add(chip::onImpactEntity));
        if(skill != null)
            builder.add(skill::onImpactEntity);

        CombatUnit combatUnit = CombatUnit.of(
            DDunit.getDmgTypeHolder(false),
            baseVal,
            weaponCap.extractMatrices(Set.of(Attributes.ATTACK_DAMAGE)),
            DDunit.getDmgCategory(),
            builder.build()
        );
        SelectorUnit selectorUnit = SelectorUnit.of(
            DDunit.getSelectorType(),
            DDunit.getRange(), DDunit.getHitCount(),
            en->true
        );

        consumer.create(level, player, AEunit, combatUnit, selectorUnit);
    }

    public static void fireDefault(ServerLevel level, ServerPlayer player,
                            IOgnaWeapon weapon, IOgnaWeaponCap weaponCap,
                            ItemStack stack, String AEunitID, String DDunitID)
    {
        fire(AtkEntityHelper.DEFAULT, level, player, weapon, weaponCap, stack, AEunitID, DDunitID, player.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
    }
}
