package com.azane.ogna.item.genable;

import com.azane.ogna.capability.weapon.IOgnaWeaponCap;
import com.azane.ogna.genable.item.base.IuuidStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 注意区分 IOgnaWeaponCap 和 IOgnaWeapon<br>
 * IOgnaWeaponCap 是武器的能力接口，包含能量、冷却等数据<br>
 * IOgnaWeapon 是武器的功能接口，包含攻击、重装等逻辑
 */
public interface IOgnaWeapon extends IuuidStack
{
    static boolean isWeapon(ItemStack stack) {
        return stack.getItem() instanceof IOgnaWeapon;
    }

    IOgnaWeaponCap getWeaponCap(ItemStack stack);

    /**
     * 服务端攻击逻辑 - 纯数据处理，不包含特效
     */
    void onServerAttack(ItemStack stack, ServerPlayer player, AttackType attackType, long chargeTime);

    /**
     * 服务端重装逻辑 - 恢复能量
     */
    void onServerReload(ItemStack stack, ServerPlayer player);

    /**
     * 客户端特效逻辑 - 音效、粒子、UI等
     */
    @OnlyIn(Dist.CLIENT)
    default void onClientEffects(ItemStack stack, Player player, AttackType attackType, long chargeTime)
    {

    }
}
