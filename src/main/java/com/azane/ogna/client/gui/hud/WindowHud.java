package com.azane.ogna.client.gui.hud;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec2;

import java.util.List;

public class WindowHud extends OgnaHud
{
    public WindowHud(List<OgnaHud> children)
    {
        super(new Vec2(0.5f,0.5f), Vec2.ONE, children);
    }

    @Override
    public void actuallyRender(GuiGraphics graphics, int width, int height, float partialTicks)
    {

    }
}
