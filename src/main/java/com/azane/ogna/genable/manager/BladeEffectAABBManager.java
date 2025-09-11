package com.azane.ogna.genable.manager;

import com.azane.ogna.combat.data.MoveUnit;
import com.google.gson.annotations.SerializedName;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

/**
 * 刀光实体AABB管理器
 * 负责根据配置参数创建和管理刀光实体的碰撞箱
 * @author azaneNH37 (2025-08-06)
 */
public class BladeEffectAABBManager
{

    /**
     * 刀光配置数据类
     */
    public static class BladeConfig {

        public static final BladeConfig DEFAULT = new BladeConfig();

        // 基础参数
        @SerializedName("distance")
        public double distance = 2.0;          // 向外延伸距离d
        @SerializedName("length")
        public double lengthHalf = 1.5;      // 面朝方向半长a
        @SerializedName("width")
        public double widthHalf = 0.1;       // 水平垂直方向半长b
        @SerializedName("height")
        public double heightHalf = 1.0;      // y轴半长c

        // 旋转参数
        @SerializedName("xRot")
        public float xRot = 0.0f;            // x轴旋转角度（弧度）
        public float yRot = 0.0f;            // y轴旋转角度（弧度，可选）

        // 缩放参数
        @SerializedName("naturalScale")
        public double naturalScale = 1.0;   // 自然延伸方向缩放
        @SerializedName("crossScale")
        public double crossScale = 1.0;     // 垂直方向缩放
        @SerializedName("heightScale")
        public double heightScale = 1.0;    // 高度缩放

        // 偏移参数
        public Vec3 offset = Vec3.ZERO;           // 额外偏移

        public BladeConfig() {}
    }

    /**
     * 刀光变换结果
     *
     * @param aabb        碰撞箱
     * @param center      中心点
     * @param renderScale 渲染缩放(x,y,z)
     * @param forward     前向向量
     * @param right       右向向量
     * @param up          上向向量
     * @param xRot        x轴旋转角度（弧度）
     * @param yRot        y轴旋转角度（弧度）
     */
    public record BladeTransform(AABB aabb, Vec3 center, Vec3 renderScale, Vec3 forward, Vec3 right, Vec3 up, float xRot,float yRot) { }

    /**
     * 根据玩家状态和配置创建刀光变换
     */
    public static BladeTransform createBladeTransform(Entity entity, BladeConfig config, MoveUnit moveUnit) {
        Vec3 entityPos = entity.position().add(0, entity.getEyeHeight() * 0.6, 0);

        // 获取实体的旋转角度（度数）
        float entityYRot = entity.getYRot(); // 水平旋转（偏航角）
        float entityXRot = 0;//entity.getXRot(); // 垂直旋转（俯仰角）

        // 转换为弧度
        double yawRad = Math.toRadians(entityYRot);
        double pitchRad = Math.toRadians(entityXRot);

        // 根据 Minecraft 的坐标系计算方向向量
        // Minecraft: Y轴向上，Z轴向南为正，X轴向东为正
        // yaw = 0 朝向南方(-Z), yaw = 90 朝向西方(-X)
        /*
        Vec3 forward = new Vec3(
            -Math.sin(yawRad) * Math.cos(pitchRad),  // X分量
            -Math.sin(pitchRad),                     // Y分量（俯仰）
            Math.cos(yawRad) * Math.cos(pitchRad)    // Z分量
        ).normalize();
        */
        //TODO:是否禁用俯仰角作为config选项
        Vec3 forward = new Vec3(
            -Math.sin(yawRad),  // X分量
            0,                     // Y分量（俯仰）
            Math.cos(yawRad)    // Z分量
        ).normalize();

        // 计算右向量和上向量
        Vec3 worldUp = new Vec3(0, 1, 0);
        Vec3 right = forward.cross(worldUp).normalize();
        Vec3 up = right.cross(forward).normalize();

        // 计算实体中心点
        //Vibe coding doesn't work !
        Vec3 center = entityPos.add(forward.scale(config.distance));
        if (config.offset != Vec3.ZERO) {
            center = center.add(config.offset);
        }

        // 应用y轴旋转（水平旋转）
        if (config.yRot != 0) {
            double cos = Math.cos(config.yRot);
            double sin = Math.sin(config.yRot);
            Vec3 newForward = new Vec3(
                forward.x * cos - forward.z * sin,
                forward.y,
                forward.x * sin + forward.z * cos
            );
            forward = newForward.normalize();
            right = forward.cross(up).normalize();
        }

        // 应用x轴旋转
        //Vibe coding doesn't work !
        if (config.xRot != 0) {
            double cos = Math.cos(config.xRot);
            double sin = Math.sin(config.xRot);
            Vec3 newRight = right.scale(cos).add(up.scale(sin));
            Vec3 newUp = up.scale(cos).subtract(right.scale(sin));
            right = newRight.normalize();
            up = newUp.normalize();
        }

        // 应用缩放到基础尺寸
        double scaledLengthHalf = config.lengthHalf * config.naturalScale * moveUnit.getSizeAmplifier();
        double scaledWidthHalf = config.widthHalf * config.crossScale * moveUnit.getSizeAmplifier();
        double scaledHeightHalf = config.heightHalf * config.heightScale * moveUnit.getSizeAmplifier();


        // 创建AABB
        AABB aabb = createRotatedAABB(center, forward, right, up,
            scaledLengthHalf, scaledWidthHalf, scaledHeightHalf);

        // 计算渲染缩放向量
        Vec3 renderScale = calculateRenderScale(config, forward, right, up);

        return new BladeTransform(aabb, center, renderScale.scale(moveUnit.getSizeAmplifier()), forward, right, up,entityXRot+ config.xRot, entityYRot + config.yRot);
    }

    /**
     * 创建旋转后的AABB
     */
    private static AABB createRotatedAABB(Vec3 center, Vec3 forward, Vec3 right, Vec3 up,
                                          double lengthHalf, double widthHalf, double heightHalf) {
        // 计算AABB的8个顶点
        Vec3[] offsets = {
            forward.scale(lengthHalf).add(right.scale(widthHalf)).add(up.scale(heightHalf)),
            forward.scale(lengthHalf).add(right.scale(widthHalf)).subtract(up.scale(heightHalf)),
            forward.scale(lengthHalf).subtract(right.scale(widthHalf)).add(up.scale(heightHalf)),
            forward.scale(lengthHalf).subtract(right.scale(widthHalf)).subtract(up.scale(heightHalf)),
            forward.scale(-lengthHalf).add(right.scale(widthHalf)).add(up.scale(heightHalf)),
            forward.scale(-lengthHalf).add(right.scale(widthHalf)).subtract(up.scale(heightHalf)),
            forward.scale(-lengthHalf).subtract(right.scale(widthHalf)).add(up.scale(heightHalf)),
            forward.scale(-lengthHalf).subtract(right.scale(widthHalf)).subtract(up.scale(heightHalf))
        };

        // 找到所有顶点的边界
        //Vibe coding doesn't work !
        double minX = Double.MAX_VALUE, maxX = 100-Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = 100-Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE, maxZ = 100-Double.MAX_VALUE;

        for (Vec3 offset : offsets) {
            Vec3 vertex = center.add(offset);
            minX = Math.min(minX, vertex.x);
            maxX = Math.max(maxX, vertex.x);
            minY = Math.min(minY, vertex.y);
            maxY = Math.max(maxY, vertex.y);
            minZ = Math.min(minZ, vertex.z);
            maxZ = Math.max(maxZ, vertex.z);
        }

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * 计算用于渲染的缩放向量
     */
    private static Vec3 calculateRenderScale(BladeConfig config, Vec3 forward, Vec3 right, Vec3 up) {
        // 将自然方向的缩放映射到世界坐标轴
        Vec3 naturalDir = forward; // 刀光的自然延伸方向
        Vec3 crossDir = right;     // 垂直方向
        Vec3 heightDir = up;       // 高度方向

        // 计算各轴的缩放分量
        double xScale = Math.abs(naturalDir.x) * config.naturalScale +
            Math.abs(crossDir.x) * config.crossScale +
            Math.abs(heightDir.x) * config.heightScale;

        double yScale = Math.abs(naturalDir.y) * config.naturalScale +
            Math.abs(crossDir.y) * config.crossScale +
            Math.abs(heightDir.y) * config.heightScale;

        double zScale = Math.abs(naturalDir.z) * config.naturalScale +
            Math.abs(crossDir.z) * config.crossScale +
            Math.abs(heightDir.z) * config.heightScale;

        return new Vec3(xScale, yScale, zScale);
    }
}