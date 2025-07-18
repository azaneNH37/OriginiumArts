package com.azane.ogna.combat.attr;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.BiFunction;

@AllArgsConstructor
@Getter
public enum DmgBucket
{
    DIRECT_ADD(0D, Double::sum,(a,b)->a-b),
    DIRECT_MUL(0D, Double::sum,(a,b)->a-b),
    TOTAL_ADD(0D,Double::sum,(a,b)->a-b),
    TOTAL_MUL(1D,(a,b) -> a * b,(a,b) -> a / b);

    private final double initialVal;
    private final BiFunction<Double,Double,Double> apply;
    private final BiFunction<Double,Double,Double> remove;
}
