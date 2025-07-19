package com.azane.ogna.client.gui.hud;

import lombok.AllArgsConstructor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@AllArgsConstructor
public abstract class OgnaHud
{
    public final Vec2 central;
    public final Vec2 size;
    public final List<OgnaHud> children;

    public void render(int width,int height,GuiGraphics graphics,float partialTicks)
    {
        int targetWidth = size.x <= 1f ? (int)( width * size.x) : (int) size.x;
        int targetHeight = size.y <= 1f ? (int)( height * size.y) : (int) size.y;
        int x = (int) (central.x * width - targetWidth / 2f);
        int y = (int) (central.y * height - targetHeight / 2f);
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        actuallyRender(graphics,targetWidth,targetHeight,partialTicks);
        children.forEach(hud->hud.render(targetWidth,targetHeight, graphics, partialTicks));
        graphics.pose().popPose();
    }

    public abstract Vec2 getBaseSize();

    public abstract void actuallyRender(GuiGraphics graphics,int width,int height,float partialTicks);

    public void blitTextureSimple(GuiGraphics graphics, ResourceLocation rl,int width, int height)
    {
        graphics.blit(rl, 0, 0, 0, 0, width, height, width, height);
    }
}