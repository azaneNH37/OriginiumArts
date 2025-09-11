package com.azane.ogna.combat.util;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.capability.weapon.IOgnaWeaponCap;
import com.azane.ogna.combat.attr.DmgBucket;
import com.azane.ogna.combat.data.AttrModifier;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.azane.ogna.lib.*;
import com.azane.ogna.registry.ModAttribute;
import com.azane.ogna.util.NBTConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author azaneNH37 (2025/9/1)
 */
public class WeaponPotential
{
    public static final int LIMIT1 = 5;
    public static final int LIMIT2 = 16;
    public static final int LIMIT3 = 50;

    public static final Function<Integer,Double> CURVE1 = lv-> Math.pow(lv,1.5)*300;
    public static final Function<Integer,Double> CURVE2 = lv-> Math.pow(lv,2)*120;
    public static final Function<Integer,Double> CURVE3 = lv-> Math.pow(lv,lv/6d)*15;
    public static final Function<Integer,Double> CURVE4 = lv-> (double) Long.MAX_VALUE;

    public static final List<Double> EXP_TABLE = new ArrayList<>();

    public static final List<List<AttrModifier>> POTENTIALS = new ArrayList<>();

    static
    {
        for(int i=0;i<=LIMIT1;i++)
            EXP_TABLE.add(CURVE1.apply(i));
        for(int i=LIMIT1+1;i<=LIMIT2;i++)
            EXP_TABLE.add(CURVE2.apply(i));
        for(int i=LIMIT2+1;i<=LIMIT3;i++)
            EXP_TABLE.add(CURVE3.apply(i));
        EXP_TABLE.add(CURVE4.apply(LIMIT3+1));
        // init potentials
        for(int i=0;i<EXP_TABLE.size();i++)
            POTENTIALS.add(new ArrayList<>());
        POTENTIALS.get(1).add(AttrModifier.of(ModAttribute.CHIP_SET_VOLUME.getId(), UUID.fromString("c5a45d61-fe17-46d8-a3a7-1f42db9215dd"), DmgBucket.TOTAL_ADD,15,1));
        POTENTIALS.get(1).add(AttrModifier.of(ModAttribute.WEAPON_ENERGY_STORE.getId(),UUID.fromString("74e01974-7812-486c-978b-f88505b005ae"), DmgBucket.DIRECT_MUL,0.2,1));
        POTENTIALS.get(2).add(AttrModifier.of(RlHelper.parse("generic.attack_damage"), UUID.fromString("43b7350d-72ca-4b11-b35c-b0a2b17dae51"), DmgBucket.TOTAL_MUL,1.25,1));
        POTENTIALS.get(3).add(AttrModifier.of(ModAttribute.CHIP_SET_VOLUME.getId(), UUID.fromString("16d8d207-66db-4987-868e-417ec3861263"), DmgBucket.TOTAL_ADD,15,1));
        POTENTIALS.get(3).add(AttrModifier.of(ModAttribute.WEAPON_ATTACK_CD.getId(), UUID.fromString("b86516f8-76df-4d69-aa9b-31632b10dbb6"), DmgBucket.TOTAL_MUL,0.8,1));
        POTENTIALS.get(4).add(AttrModifier.of(ModAttribute.CHIP_SET_VOLUME.getId(), UUID.fromString("3908ceb7-32a7-4289-9050-484208570827"), DmgBucket.TOTAL_ADD,15,1));
        POTENTIALS.get(4).add(AttrModifier.of(ModAttribute.WEAPON_ENERGY_CONSUME.getId(), UUID.fromString("85e82a0c-d9ec-44d8-b4e0-4f2a62a314d9"), DmgBucket.DIRECT_ADD,-2,1));
        POTENTIALS.get(5).add(AttrModifier.of(ModAttribute.CHIP_SET_VOLUME.getId(), UUID.fromString("73a051bb-53d3-4fa6-a476-e91757b0add3"), DmgBucket.TOTAL_ADD,20,1));
        POTENTIALS.get(5).add(AttrModifier.of(ModAttribute.CHIP_SET_VOLUME.getId(), UUID.fromString("8510078d-54b9-4118-8306-46145849f322"), DmgBucket.TOTAL_MUL,1.15,1));
        for (int i=6;i<=LIMIT2;i++)
        {
            POTENTIALS.get(i).add(AttrModifier.of(ModAttribute.CHIP_SET_VOLUME.getId(), UUID.fromString("42044e63-8009-42e7-9a06-8864b059a486"), DmgBucket.TOTAL_ADD,10,20));
            POTENTIALS.get(i).add(AttrModifier.of(RlHelper.parse("generic.attack_damage"), UUID.fromString("a11dde6a-8639-48ad-8cce-58ab0ba92fee"), DmgBucket.DIRECT_ADD,2,20));
        }
        for(int i=LIMIT2+1;i<=LIMIT3;i++)
        {
            POTENTIALS.get(i).add(AttrModifier.of(ModAttribute.CHIP_SET_VOLUME.getId(), UUID.fromString("f4e2e8b3-2f0c-4d1c-8f7a-1e5f3c6e8b9a"), DmgBucket.TOTAL_ADD,10,50));
            POTENTIALS.get(i).add(AttrModifier.of(RlHelper.parse("generic.attack_damage"), UUID.fromString("286ec458-d8e2-4ba6-8dd1-bf42133287f5"), DmgBucket.DIRECT_ADD,0.5,50));
        }
    }

    public static int getLevelByExp(double exp)
    {
        for(int i=0;i<EXP_TABLE.size();i++)
            if(exp<=EXP_TABLE.get(i))
                return i-1;
        return EXP_TABLE.size()-1;
    }


    public static void applyPotential(IOgnaWeaponCap weaponCap)
    {
        int newLevel = getLevelByExp(NbtHelper.getOrCreate(weaponCap.getExtraData(), NBTConstants.WP_EXP, 0.0));
        int oldLevel = NbtHelper.getOrCreate(weaponCap.getExtraData(), NBTConstants.WP_LEVEL, 0);
        if(newLevel>oldLevel)
        {
            NbtHelper.put(weaponCap.getExtraData(), NBTConstants.WP_LEVEL, newLevel);
            for(int i=oldLevel+1;i<=newLevel;i++)
            {
                for(AttrModifier modifier : POTENTIALS.get(i))
                    weaponCap.acceptModifier(modifier);
                weaponCap.onPotentialLevel(i);
            }
        }
    }

    public static void appendHoverText(ItemStack stack, List<Component> tooltip, TooltipFlag flag)
    {
        if(IOgnaWeapon.isWeapon(stack))
        {
            IOgnaWeaponCap cap = ((IOgnaWeapon) stack.getItem()).getWeaponCap(stack);
            int level = NbtHelper.getOrCreate(cap.getExtraData(), NBTConstants.WP_LEVEL, 0);
            double exp = NbtHelper.getOrCreate(cap.getExtraData(), NBTConstants.WP_EXP, 0.0);
            tooltip.add(Component.translatable("ogna.tip.weapon.potential", NumStrHelper.roman(level)).withStyle(Style.EMPTY.withBold(true).withColor(ColorHelper.getBrighter(ColorHelper.getGradientColor(level*4),1.5f))));
            if(level<EXP_TABLE.size()-1)
            {
                double deltaExp = EXP_TABLE.get(level + 1) - EXP_TABLE.get(level);
                double curExp = exp - EXP_TABLE.get(level);
                //OriginiumArts.LOGGER.warn("{} / {} ({} - {})",curExp,deltaExp,exp,EXP_TABLE.get(level));
                int prog = (int) (curExp * 25 / deltaExp);
                int rem = 25 - prog;
                tooltip.add(Component.empty()
                    .append(Component.literal("-".repeat(Mth.clamp(prog,0,25))).withStyle(ChatFormatting.BOLD,ChatFormatting.GREEN))
                    .append(Component.literal("-".repeat(Mth.clamp(rem,0,25))).withStyle(ChatFormatting.WHITE))
                    .append(Component.literal(" %s/%s".formatted(NumStrHelper.format((long) exp),NumStrHelper.format(EXP_TABLE.get(level+1).longValue()))).withStyle(ChatFormatting.GREEN))
                );
            }
            if(Screen.hasShiftDown())
            {
                tooltip.add(Component.translatable("ogna.tip.separator").withStyle(ChatFormatting.GRAY));
                for(int i=1;i<=5;i++)
                {
                    var header = Component.translatable("ogna.tip.weapon.potential", NumStrHelper.roman(i)).append(" ").withStyle(level >= i ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY);
                    POTENTIALS.get(i).forEach(attrModifier -> header.append(attrModifier.getComponent(stack,tooltip,flag)).append(" "));
                    if(I18n.exists("ogna.tip.weapon.potential.level.%d".formatted(i)))
                        header.append(Component.translatable("ogna.tip.weapon.potential.level.%d".formatted(i)).withStyle(ChatFormatting.WHITE));
                    tooltip.add(header);
                }
                if(level>5)
                {
                    var header = Component.translatable("ogna.tip.weapon.potential", NumStrHelper.roman(6)).append("+ ");
                    POTENTIALS.get(6).forEach(attrModifier -> header.append(attrModifier.getComponent(stack,tooltip,flag)).append(" "));
                    tooltip.add(header.append(" [×%d]".formatted(level-5)).withStyle(Style.EMPTY.withColor(ColorHelper.getGradientColor(level*4))));
                }
                tooltip.add(Component.translatable("ogna.tip.separator").withStyle(ChatFormatting.GRAY));
            }
            else
                tooltip.add(Component.translatable("ogna.tip.expand.shift").withStyle(ChatFormatting.YELLOW));
        }
    }
}
