package com.azane.ogna.combat.data;

import com.azane.ogna.combat.util.SelectorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author azaneNH37 (2025-07-20)
 */
@AllArgsConstructor(staticName = "of")
@Getter
public class SelectorUnit
{
    private final SelectorType type;
    private final double range;
    private final int hitCount;
    private final Predicate<LivingEntity> filter;

    public List<LivingEntity> gatherMultiTargets(ServerLevel level, AABB basis, Predicate<LivingEntity> tester)
    {
        if(type == SelectorType.SINGLE)
            return List.of();
        List<LivingEntity> raw = level.getEntitiesOfClass(LivingEntity.class,basis.inflate(range),tester.and(filter));
        if(type == SelectorType.AREA)
            return raw;
        Collections.shuffle(raw);
        return raw.subList(0,Math.min(hitCount,raw.size()));
    }
}
