package com.azane.ogna.client.gui.hud;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.gameplay.ReloadState;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.lib.RlHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

import java.util.List;

public class ReloadStatusHud extends OgnaHud
{
    public static final ResourceLocation RELOADING = RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/reload/reloading.png");
    public static final ResourceLocation COMPLETE = RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/reload/complete.png");
    public static final ResourceLocation WEAPON_MISMATCH = RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/reload/weapon_mismatch.png");
    public static final ResourceLocation OUT_OF_ENERGY = RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/reload/out_of_energy.png");
    public static final ResourceLocation RELOAD_IGNORED = RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/reload/reload_ignored.png");

    public static final Vec2 ASSET_SIZE = new Vec2(256f,64f);
    public static final Vec2 SCREEN_SIZE = new Vec2(128f, 32f);

    private ReloadState reloadState = ReloadState.COMPLETE;
    private long duration;
    private long startTime;

    public ReloadStatusHud()
    {
        super(new Vec2(0.5f,0.43f), SCREEN_SIZE, WindowHud.SIZE,List.of());
    }

    public void refreshReloadState(ReloadState reloadState, long duration)
    {
        this.reloadState = reloadState;
        this.duration = duration;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void actuallyRender(GuiGraphics graphics, float partialTicks)
    {
        if(System.currentTimeMillis()-startTime >= duration)
            return;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        graphics.pose().pushPose();
        ResourceLocation texture = switch (reloadState) {
            case RELOADING -> RELOADING;
            case COMPLETE -> COMPLETE;
            case WEAPON_MISMATCH -> WEAPON_MISMATCH;
            case OUT_OF_ENERGY -> OUT_OF_ENERGY;
            case RELOAD_IGNORED -> RELOAD_IGNORED;
        };
        graphics.pose().scale(0.5f, 0.5f, 1f);
        long time = System.currentTimeMillis()%100000;
        float alpha = (float) (0.6f + 0.4f*Math.sin(time*0.004f));
        //DebugLogger.log("{},{},{},{}",time,time*0.95f,Math.sin(time*0.95f),String.valueOf(alpha));
        graphics.setColor(1f,1f,1f, alpha);
        graphics.blit(texture,0, 0,0,0, 256, 64,256,64);
        graphics.pose().popPose();
    }
}
