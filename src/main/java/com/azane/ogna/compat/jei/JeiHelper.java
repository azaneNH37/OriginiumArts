package com.azane.ogna.compat.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;

/**
 * @author azaneNH37 (2025/8/12)
 */
public final class JeiHelper
{
    public static void draw(GuiGraphics graphics, float centreX,float centreY,float scale,IDrawable drawable)
    {
        float nx = drawable.getWidth()*scale,ny = drawable.getHeight()*scale;
        float mul = 1f/scale;
        graphics.pose().pushPose();
        graphics.pose().scale(scale,scale,1f);
        drawable.draw(graphics, (int) ((centreX-nx/2)*mul), (int) ((centreY-ny/2)*mul));
        graphics.pose().popPose();
    }
}
