package com.azane.ogna.combat.util;

import com.azane.ogna.item.weapon.IOgnaWeapon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class SkillTracker
{
    public static void handleMainHandChange(Player player, ItemStack lastMainHand, ItemStack curMainHand)
    {
        if(!IOgnaWeapon.isWeapon(lastMainHand))
            return;
        IOgnaWeapon lastWeapon = (IOgnaWeapon) lastMainHand.getItem();
    }
}