package com.azane.ogna.client.event;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = OriginiumArts.MOD_ID,value = Dist.CLIENT)
public class PlayerClickListener
{
    @SubscribeEvent
    public static void onAttackInput(InputEvent.InteractionKeyMappingTriggered event)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if(event.isAttack() && player != null && IOgnaWeapon.isWeapon(player.getMainHandItem()))
        {
            event.setCanceled(true);
        }
    }
}
