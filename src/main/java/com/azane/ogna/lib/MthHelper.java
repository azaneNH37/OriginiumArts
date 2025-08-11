package com.azane.ogna.lib;

import net.minecraft.world.phys.Vec3;

/**
 * @author azaneNH37 (2025-08-07)
 */
public final class MthHelper
{
    /**
     * 球面线性插值（Spherical Linear Interpolation）
     * @param start
     * @param end
     * @param factor
     * @return
     */
    public static Vec3 slerp(Vec3 start, Vec3 end, float factor)
    {
        double dot = Math.max(-1, Math.min(1, start.dot(end)));
        double theta = Math.acos(dot) * factor;

        Vec3 relativeVec = end.subtract(start.scale(dot)).normalize();
        return start.scale(Math.cos(theta)).add(relativeVec.scale(Math.sin(theta)));
    }
}
