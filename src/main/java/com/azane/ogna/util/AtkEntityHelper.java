package com.azane.ogna.util;

import com.azane.ogna.combat.data.CombatUnit;
import com.azane.ogna.combat.data.MoveUnit;
import com.azane.ogna.combat.data.SelectorUnit;
import com.azane.ogna.entity.genable.BladeEffect;
import com.azane.ogna.entity.genable.Bullet;
import com.azane.ogna.genable.data.AtkEntityData;
import com.azane.ogna.genable.entity.IBullet;
import com.azane.ogna.resource.service.ServerDataService;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Objects;

/**
 * @author azaneNH37 (2025-08-07)
 */
public final class AtkEntityHelper
{

    public static Entity createBlade(ServerLevel level, ServerPlayer player, AtkEntityData.AtkUnit atkUnit, CombatUnit combatUnit, SelectorUnit selectorUnit)
    {
        if(atkUnit == null)
            return null;
        BladeEffect effect = BladeEffect.createBlade(level,player, atkUnit.getId(), atkUnit.getDelay(),combatUnit,selectorUnit);
        level.addFreshEntity(effect);
        return effect;
    }


    public static Entity createBullet(ServerLevel level, ServerPlayer player, AtkEntityData.AtkUnit atkUnit, CombatUnit combatUnit, SelectorUnit selectorUnit)
    {
        if (atkUnit == null)
            return null;
        IBullet bulletData = ServerDataService.get().getBullet(atkUnit.getId());
        if(bulletData != null)
        {
            var bullet = new Bullet(player,level,atkUnit.getId(),combatUnit,selectorUnit);
            bullet.shootFromRotation(player,player.getXRot(),player.getYRot(),0, bulletData.getSpeed(),0);
            level.addFreshEntity(bullet);
            return bullet;
        }
        return null;
    }

    public static Entity createTargetBullet(ServerLevel level, ServerPlayer player, AtkEntityData.AtkUnit atkUnit, CombatUnit combatUnit, SelectorUnit selectorUnit, MoveUnit moveUnit)
    {
        if (atkUnit == null)
            return null;
        if (!Objects.equals(atkUnit.getAtkEntityType(), "bullet"))
            throw new IllegalArgumentException("Expected bullet type for target bullet creation, got: " + atkUnit.getAtkEntityType());
        IBullet bulletData = ServerDataService.get().getBullet(atkUnit.getId());
        if(bulletData != null)
        {
            var bullet = new Bullet(player,level,atkUnit.getId(),combatUnit,selectorUnit,moveUnit);
            bullet.shootFromRotation(player,(float) moveUnit.getXRot(),(float) moveUnit.getYRot(),0, bulletData.getSpeed(),0);
            level.addFreshEntity(bullet);
            return bullet;
        }
        return null;
    }

    public static Entity createDefault(ServerLevel level, ServerPlayer player, AtkEntityData.AtkUnit atkUnit, CombatUnit combatUnit, SelectorUnit selectorUnit)
    {
        if(atkUnit == null)
            return null;
        String type = atkUnit.getAtkEntityType();
        switch (type)
        {
            case "blade" -> {
                return createBlade(level, player, atkUnit, combatUnit, selectorUnit);
            }
            case "bullet" -> {
                return createBullet(level, player, atkUnit, combatUnit, selectorUnit);
            }
            default -> throw new IllegalArgumentException("Unknown AtkEntity type: " + type);
        }
    }
}
