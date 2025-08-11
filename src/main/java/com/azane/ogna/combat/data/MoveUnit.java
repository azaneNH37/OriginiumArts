package com.azane.ogna.combat.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * @author azaneNH37 (2025-08-07)
 */
@AllArgsConstructor
@Getter
public class MoveUnit
{
    private final Entity targetEntity;
    private final Vec3 targetPos;
    private final double xRot;
    private final double yRot;

    public static class Builder
    {
        private Entity targetEntity;
        private Vec3 targetPos;
        private double xRot;
        private double yRot;

        public Builder targetEntity(Entity targetEntity)
        {
            this.targetEntity = targetEntity;
            return this;
        }

        public Builder targetPos(Vec3 targetPos)
        {
            this.targetPos = targetPos;
            return this;
        }

        public Builder xRot(double xRot)
        {
            this.xRot = xRot;
            return this;
        }

        public Builder yRot(double yRot)
        {
            this.yRot = yRot;
            return this;
        }

        public MoveUnit build()
        {
            return new MoveUnit(targetEntity, targetPos, xRot, yRot);
        }
    }
}
