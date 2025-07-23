package com.azane.ogna.util;

import com.azane.ogna.combat.data.CombatUnit;
import com.azane.ogna.combat.data.SelectorUnit;
import com.azane.ogna.entity.genable.BladeEffect;
import com.azane.ogna.entity.genable.Bullet;
import com.azane.ogna.genable.data.AtkEntityData;
import com.azane.ogna.genable.entity.IBullet;
import com.azane.ogna.resource.service.ServerDataService;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class AtkEntityHelper
{
    public static AtkEntityConsumer BLADE = (level, player, atkUnit, combatUnit, selectorUnit) -> {
        if(atkUnit == null)
            return;
        level.addFreshEntity(BladeEffect.createBlade(level,player, atkUnit.getId(), atkUnit.getDelay(),combatUnit,selectorUnit));
    };

    public static AtkEntityConsumer BULLET = (level, player, atkUnit, combatUnit, selectorUnit) -> {
        if (atkUnit == null)
            return;
        IBullet bulletData = ServerDataService.get().getBullet(atkUnit.getId());
        if(bulletData != null)
        {
            var bullet = new Bullet(player,level,atkUnit.getId(),combatUnit,selectorUnit);
            bullet.shootFromRotation(player,player.getXRot(),player.getYRot(),0, bulletData.getSpeed(),0);
            level.addFreshEntity(bullet);
        }
    };

    public static AtkEntityConsumer DEFAULT = (level, player, atkUnit, combatUnit, selectorUnit) -> {
        if(atkUnit == null)
            return;
        String type = atkUnit.getAtkEntityType();
        switch (type)
        {
            case "blade" -> BLADE.create(level, player, atkUnit, combatUnit, selectorUnit);
            case "bullet" -> BULLET.create(level, player, atkUnit, combatUnit, selectorUnit);
            default -> throw new IllegalArgumentException("Unknown AtkEntity type: " + type);
        }
    };

    @FunctionalInterface
    public interface AtkEntityConsumer
    {
        void create(ServerLevel level, ServerPlayer player, AtkEntityData.AtkUnit atkUnit, CombatUnit combatUnit, SelectorUnit selectorUnit);
    }
}
