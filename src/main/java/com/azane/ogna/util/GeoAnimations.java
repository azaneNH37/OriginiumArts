package com.azane.ogna.util;

import software.bernie.geckolib.core.animation.RawAnimation;

/**
 * 逐渐尝试统一动画命名
 */
public class GeoAnimations
{
    public static final RawAnimation MISC_IDLE = RawAnimation.begin().thenLoop("misc.idle");
    public static final RawAnimation MISC_WORK = RawAnimation.begin().thenLoop("misc.work");
    public static final RawAnimation OP_OPEN = RawAnimation.begin().thenPlay("op.open");
    public static final RawAnimation OP_CLOSE = RawAnimation.begin().thenPlay("op.close");
}
