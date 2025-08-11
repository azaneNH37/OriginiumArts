package com.azane.ogna.combat.attr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.function.BiFunction;
import java.util.function.Function;

@AllArgsConstructor
@Getter
public enum DmgBucket
{
    DIRECT_ADD(0D, Double::sum,(a,b)->a-b,
        (val) -> Component.literal(String.format(val> 0 ?"+%.1f" : "%.1f", val)).withStyle(ChatFormatting.DARK_GREEN,ChatFormatting.BOLD)),
    DIRECT_MUL(0D, Double::sum,(a,b)->a-b,
        (val) -> Component.literal(String.format(val>0 ?"+%.0f%%" : "%.0f%%", val*100)).withStyle(ChatFormatting.BLUE,ChatFormatting.BOLD)),
    TOTAL_ADD(0D,Double::sum,(a,b)->a-b,
        (val) -> Component.literal(String.format(val > 0 ?"+%.1f": "%.1f", val)).withStyle(ChatFormatting.GOLD,ChatFormatting.BOLD)),
    TOTAL_MUL(1D,(a,b) -> a * b,(a,b) -> a / b,
        (val) -> Component.literal(String.format("×%.0f%%", val*100)).withStyle(ChatFormatting.GOLD,ChatFormatting.BOLD));

    private final double initialVal;
    private final BiFunction<Double,Double,Double> apply;
    private final BiFunction<Double,Double,Double> remove;
    private final Function<Double, Component> format;
}
