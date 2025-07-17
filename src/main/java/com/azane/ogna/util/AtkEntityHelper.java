package com.azane.ogna.util;

import com.azane.ogna.entity.genable.BladeEffect;
import com.azane.ogna.entity.genable.Bullet;
import com.azane.ogna.genable.data.WeaponAtkEntityData;
import com.azane.ogna.genable.entity.IBullet;
import com.azane.ogna.resource.service.ServerDataService;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class AtkEntityHelper
{
    public static void createDefaultBlade(ServerLevel level, ServerPlayer player, WeaponAtkEntityData.AtkUnit atkUnit)
    {
        if(atkUnit == null)
            return;
        level.addFreshEntity(BladeEffect.createBlade(level,player, atkUnit.getId(), atkUnit.getDelay()));
    }

    public static void shootDefaultBullet(ServerLevel level,ServerPlayer player, WeaponAtkEntityData.AtkUnit atkUnit)
    {
        if (atkUnit == null)
            return;
        IBullet bulletData = ServerDataService.get().getBullet(atkUnit.getId());
        if(bulletData != null)
        {
            var bullet = new Bullet(player,level,atkUnit.getId());
            bullet.shootFromRotation(player,player.getXRot(),player.getYRot(),0, bulletData.getSpeed(),0);
            level.addFreshEntity(bullet);
        }
    }
}
