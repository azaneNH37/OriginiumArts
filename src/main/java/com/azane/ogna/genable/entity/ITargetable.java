package com.azane.ogna.genable.entity;

import net.minecraft.world.phys.Vec3;

import static com.azane.ogna.lib.MthHelper.slerp;

public interface ITargetable
{
    Vec3 getActualTarget();

    default Vec3 updateDeltaMovement(Vec3 originDelta,Vec3 curPosition,float minTracking,float turnRate)
    {
        Vec3 currentTarget = getActualTarget();
        if(currentTarget == null)
            return originDelta;
        double distance = curPosition.distanceTo(currentTarget);
        if (distance < minTracking)
            return originDelta;

        Vec3 targetDir = currentTarget.subtract(curPosition).normalize();
        double speed = originDelta.length();
        Vec3 currentDir = originDelta.normalize();
        Vec3 newDirection = slerp(currentDir, targetDir, turnRate);

        return newDirection.scale(speed);
    }
}
