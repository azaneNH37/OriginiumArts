package com.azane.ogna.client.gui.hud;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec2;

import java.util.List;

public class WindowHud extends OgnaHud
{
    public static final Vec2 SIZE = new Vec2(1280f, 720f);

    public WindowHud(List<OgnaHud> children)
    {
        super(Vec2.ZERO,SIZE,SIZE, children);
    }

    @Override
    public void render(int width, int height, GuiGraphics graphics, float partialTicks)
    {
        children.forEach(hud->hud.render(width, height, graphics, partialTicks));
    }

    @Override
    public void actuallyRender(GuiGraphics graphics, float partialTicks)
    {

    }
}
