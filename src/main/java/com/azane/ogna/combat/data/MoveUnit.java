package com.azane.ogna.combat.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * @author azaneNH37 (2025-08-07)
 */
@AllArgsConstructor
@Getter
public class MoveUnit
{
    private final Vec3 initialPos;

    private final Entity targetEntity;
    private final Vec3 targetPos;

    private final double xRot;
    private final double yRot;

    private final float speedAmplifier;
    private final float sizeAmplifier;

    private final float minTrackingDistance;
    private final float turnRate;


    public static MoveUnit fromBuffer(FriendlyByteBuf buf, Level level)
    {
        Vec3 initialPos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        double xRot = buf.readDouble();
        double yRot = buf.readDouble();
        Entity targetEntity = null;
        if (buf.readBoolean())
            targetEntity = level.getEntity(buf.readInt());
        Vec3 targetPos = null;
        if (buf.readBoolean())
            targetPos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        float speedAmplifier = buf.readFloat();
        float size = buf.readFloat();
        float minTrackingDistance = buf.readFloat();
        float turnRate = buf.readFloat();
        return new Builder()
            .initialPos(initialPos)
            .targetEntity(targetEntity)
            .targetPos(targetPos)
            .xRot(xRot)
            .yRot(yRot)
            .speedAmplifier(speedAmplifier)
            .sizeAmplifier(size)
            .minTrackingDistance(minTrackingDistance)
            .turnRate(turnRate)
            .build();
    }

    public void toBuffer(FriendlyByteBuf buf)
    {
        buf.writeDouble(initialPos.x);
        buf.writeDouble(initialPos.y);
        buf.writeDouble(initialPos.z);
        buf.writeDouble(xRot);
        buf.writeDouble(yRot);
        if (targetEntity != null)
            buf.writeBoolean(true).writeInt(targetEntity.getId());
        else
            buf.writeBoolean(false);
        if (targetPos != null)
            buf.writeBoolean(true).writeDouble(targetPos.x).writeDouble(targetPos.y).writeDouble(targetPos.z);
        else
            buf.writeBoolean(false);

        buf.writeFloat(speedAmplifier);
        buf.writeFloat(sizeAmplifier);
        buf.writeFloat(minTrackingDistance);
        buf.writeFloat(turnRate);
    }

    public static class Builder
    {
        private Vec3 initialPos = null;
        private Entity targetEntity = null;
        private Vec3 targetPos = null;
        private double xRot = 0;
        private double yRot = 0;
        private float speedAmplifier = 1.0f;
        private float sizeAmplifier = 1F;
        private float minTrackingDistance = 0.2F;
        private float turnRate = 0.35F;

        public Builder initialPos(Vec3 initialPos)
        {
            this.initialPos = initialPos;
            return this;
        }
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
        public Builder speedAmplifier(float speedAmplifier)
        {
            this.speedAmplifier = speedAmplifier;
            return this;
        }
        public Builder sizeAmplifier(float size)
        {
            this.sizeAmplifier = size;
            return this;
        }
        public Builder minTrackingDistance(float minTrackingDistance)
        {
            this.minTrackingDistance = minTrackingDistance;
            return this;
        }
        public Builder turnRate(float turnRate)
        {
            this.turnRate = turnRate;
            return this;
        }

        public MoveUnit build()
        {
            return new MoveUnit(initialPos,targetEntity, targetPos, xRot, yRot,
                speedAmplifier, sizeAmplifier, minTrackingDistance, turnRate);
        }
    }
}
