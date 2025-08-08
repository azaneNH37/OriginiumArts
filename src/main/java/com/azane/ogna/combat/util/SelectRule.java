package com.azane.ogna.combat.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public enum SelectRule
{
    NULL(le->true),
    NON_PLAYER(le -> !(le instanceof Player));

    private final Predicate<LivingEntity> filter;
}
