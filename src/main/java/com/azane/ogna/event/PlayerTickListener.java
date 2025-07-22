package com.azane.ogna.event;

import com.azane.ogna.combat.util.SkillTracker;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.WeakHashMap;

@Mod.EventBusSubscriber
public class PlayerTickListener
{
    private static WeakHashMap<Player, ItemStack> lastMainHandMap = new WeakHashMap<>();

    @SubscribeEvent
    public static void onPlayerTickEnd(TickEvent.PlayerTickEvent event)
    {
        /*
        if (event.phase == TickEvent.Phase.START) return;

        Player player = event.player;
        ItemStack currentMainHand = player.getMainHandItem();
        ItemStack lastMainHand = lastMainHandMap.get(player);

        if ((lastMainHand == ItemStack.EMPTY && currentMainHand != ItemStack.EMPTY) ||
            (lastMainHand != ItemStack.EMPTY && currentMainHand == ItemStack.EMPTY) ||
            !ItemStack.matches(currentMainHand, lastMainHand))
        {
            SkillTracker.handleMainHandChange(player, lastMainHand, currentMainHand);
            lastMainHandMap.put(player, currentMainHand.copy());
        }
                 */
    }

    @SubscribeEvent
    public static void onPlayerTickStart(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END) return;

        Player player = event.player;
        ItemStack currentMainHand = player.getMainHandItem();

        if(player.tickCount % 2 == 0)
        {
            if(IOgnaWeapon.isWeapon(currentMainHand))
            {
                IOgnaWeapon weapon = (IOgnaWeapon) currentMainHand.getItem();
                weapon.tick(player.level(), player, currentMainHand);
            }
        }
    }
}
