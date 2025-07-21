package com.azane.ogna.client.lib;

import net.minecraft.world.phys.Vec2;

public class GuiPosHelper
{
    public static Vec2 toLeftTop(Vec2 centre,Vec2 size)
    {
        return new Vec2(centre.x - size.x / 2f, centre.y - size.y / 2f);
    }

    public static Vec2 differScale(Vec2 pos, Vec2 scale)
    {
        return new Vec2(pos.x * scale.x, pos.y * scale.y);
    }
}
