package com.azane.ogna.client.gui.hud;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.gameplay.AttackState;
import com.azane.ogna.client.gameplay.AttackStateManager;
import com.azane.ogna.client.lib.GuiPosHelper;
import com.azane.ogna.lib.RlHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

import java.util.List;

/**
 * @author azaneNH37 (2025-07-22)
 */
public class AttackStateHud extends OgnaHud
{
    public static final ResourceLocation TARGET = RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/astate_target.png");
    public static final ResourceLocation CD = RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/astate_cd.png");
    public static final ResourceLocation CHARGE = RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/astate_charge.png");
    public static final ResourceLocation RELOAD = RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/astate_reload.png");

    public static final Vec2 SIZE = new Vec2(64f,64f);

    public AttackStateHud()
    {
        super(new Vec2(0.5f,0.5f),
           SIZE, WindowHud.SIZE,List.of());
    }

    @Override
    public void actuallyRender(GuiGraphics graphics, float partialTicks)
    {
        AttackStateManager.AttackStateData data = AttackStateManager.getInstance().getAttackStateData(Minecraft.getInstance().player.getUUID());
        if(data == null || data.getWeaponState() == AttackState.UNKNOWN || data.getWeaponState() == AttackState.IDLE)
            return;
        graphics.pose().pushPose();
        graphics.blit(TARGET,0, 0,0,0, 64, 64,64,64);
        double process = data.getWeaponState() == AttackState.CHARGING ?
            (System.currentTimeMillis() - data.getChargeStartTime()) / (double) data.getExpectLastingTime() :
            1D - (data.getStateEndTime() - System.currentTimeMillis()) / (double) data.getExpectLastingTime();
        if(process > 1D)
            process = 1D + Math.sin(14*(process-1D)) * 0.12D;
        int cSize = (int) (64*process);
        graphics.pose().pushPose();
        graphics.pose().translate((64-cSize)/2f, (64-cSize)/2f, 0);
        graphics.pose().scale((float) process, (float) process,0);
        ResourceLocation texture = switch (data.getWeaponState()) {
            case CHARGING -> CHARGE;
            case RELOADING -> RELOAD;
            default -> CD;
        };
        blitTextureSimple(graphics, texture, 64, 64);
        graphics.pose().popPose();
        graphics.pose().popPose();
    }
}
