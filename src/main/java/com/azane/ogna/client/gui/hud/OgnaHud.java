package com.azane.ogna.client.gui.hud;

import com.azane.ogna.client.lib.GuiPosHelper;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

import static com.azane.ogna.client.lib.GuiPosHelper.*;

@OnlyIn(Dist.CLIENT)
@AllArgsConstructor
public abstract class OgnaHud
{
    public final Vec2 centre;
    public final Vec2 size;
    public final Vec2 standard;
    public final List<OgnaHud> children;

    public void render(int width,int height,GuiGraphics graphics,float partialTicks)
    {
        var scales = new Vec2(width /standard.x,height /standard.y);
        float scale = Math.abs(scales.x-1f) < Math.abs(scales.y-1f) ? scales.x : scales.y;
        var leftTop = toLeftTop(differScale(new Vec2(width,height),centre),differScale(size,new Vec2(scale,scale)));
        graphics.pose().pushPose();
        graphics.pose().translate(leftTop.x,leftTop.y,0);
        graphics.pose().scale(scale,scale,1);
        actuallyRender(graphics,partialTicks);
        children.forEach(hud->hud.render((int) size.x, (int) size.y, graphics, partialTicks));
        graphics.pose().popPose();
    }

    public abstract void actuallyRender(GuiGraphics graphics,float partialTicks);

    public void blitTextureSimple(GuiGraphics graphics, ResourceLocation rl,int width, int height)
    {
        graphics.blit(rl, 0, 0, 0, 0, width, height, width, height);
    }
    public void blitTextureSimple(GuiGraphics graphics, ResourceLocation rl,int offx,int offy,int width, int height)
    {
        graphics.blit(rl, offx, offy, 0, 0, width, height, width, height);
    }
}