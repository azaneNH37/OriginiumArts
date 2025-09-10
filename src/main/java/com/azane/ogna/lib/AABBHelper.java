package com.azane.ogna.lib;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * @author azaneNH37 (2025/9/10)
 */
public final class AABBHelper
{
    public static AABB cube(Vec3 centre, double size)
    {
        double halfSize = size / 2.0;
        return new AABB(
            centre.x - halfSize, centre.y - halfSize, centre.z - halfSize,
            centre.x + halfSize, centre.y + halfSize, centre.z + halfSize
        );
    }
}
