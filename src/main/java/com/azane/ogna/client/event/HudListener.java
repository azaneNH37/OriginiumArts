package com.azane.ogna.client.event;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.client.gui.hud.OgnaHuds;
import com.azane.ogna.client.lib.InputExtraCheck;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author azaneNH37 (2025-07-20)
 */
@Mod.EventBusSubscriber(modid = OriginiumArts.MOD_ID,value = Dist.CLIENT)
public class HudListener
{
    @SubscribeEvent
    public static void onHudPostRender(RenderGuiEvent.Post event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(InputExtraCheck.isInGame())
            OgnaHuds.GAME_WINDOW.render(minecraft.getWindow().getGuiScaledWidth(),
                minecraft.getWindow().getGuiScaledHeight(), event.getGuiGraphics(), event.getPartialTick());
    }
}
