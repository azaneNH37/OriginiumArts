package com.azane.ogna.lib;

import net.minecraft.world.phys.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * @author azaneNH37 (2025/9/10)
 */
public final class ProjectileHelper
{
    /**
     * 获取沿特定路径移动的投掷物所碰撞到的所有实体。
     * * @param pLevel         当前世界等级
     * @param pProjectile    投掷物实体本身
     * @param pStartVec      投掷物路径的起点坐标
     * @param pEndVec        投掷物路径的终点坐标
     * @param pProjectileSize 投掷物自定义的碰撞箱大小
     * @param pFilter        用于筛选实体的谓词
     * @return 碰撞到的实体列表，如果没有则返回一个空列表。
     */
    public static List<Entity> getEntitiesAlongPath(Level pLevel, Entity pProjectile, Vec3 pStartVec, Vec3 pEndVec,
                                                    AABB pProjectileSize, Predicate<Entity> pFilter) {

        List<Entity> hitEntities = new ArrayList<>();

        // 计算投掷物路径的总体边界框（起点和终点AABB的合并）
        AABB startAABB = pProjectileSize.move(pStartVec);
        AABB endAABB = pProjectileSize.move(pEndVec);
        AABB combinedAABB = startAABB.minmax(endAABB);

        // 使用更严格的过滤器，排除投掷物自己和不符合条件的实体
        Predicate<Entity> combinedFilter = pFilter.and(entity -> !entity.equals(pProjectile));

        // 预筛选可能与路径相交的实体
        for (Entity entity : pLevel.getEntities(pProjectile, combinedAABB, combinedFilter)) {
            // 对预筛选后的实体进行精确的 swept AABB 检测
            if (checkSweptAABBIntersection(pStartVec, pEndVec, pProjectileSize, entity.getBoundingBox())) {
                hitEntities.add(entity);
            }
        }

        return hitEntities;
    }

    /**
     * 检查一个移动的AABB（投掷物）与一个静态AABB（实体）是否相交。
     * 使用“扩大目标AABB并进行线段-AABB相交检测”的优化方法。
     *
     * @param pStart          投掷物路径起点
     * @param pEnd            投掷物路径终点
     * @param pProjectileBox  投掷物的自定义碰撞箱大小
     * @param pTargetBox      目标实体的碰撞箱
     * @return 如果路径与目标相交，返回 true。
     */
    private static boolean checkSweptAABBIntersection(Vec3 pStart, Vec3 pEnd, AABB pProjectileBox, AABB pTargetBox) {
        // 计算移动向量
        Vec3 moveVec = pEnd.subtract(pStart);

        // 投掷物AABB的半长宽高
        double halfX = (pProjectileBox.maxX - pProjectileBox.minX) / 2.0;
        double halfY = (pProjectileBox.maxY - pProjectileBox.minY) / 2.0;
        double halfZ = (pProjectileBox.maxZ - pProjectileBox.minZ) / 2.0;

        // 膨胀目标AABB
        AABB inflatedTarget = pTargetBox.inflate(halfX, halfY, halfZ);

        // 检测从起点到终点的线段与膨胀后的AABB是否有交点
        return inflatedTarget.clip(pStart, pStart.add(moveVec)).isPresent();
    }
}
