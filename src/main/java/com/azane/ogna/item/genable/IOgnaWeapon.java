package com.azane.ogna.item.genable;

import com.azane.ogna.genable.item.base.IuuidStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IOgnaWeapon extends IuuidStack
{
    static boolean isWeapon(ItemStack stack) {
        return stack.getItem() instanceof IOgnaWeapon;
    }
    /**
     * 检查武器是否能够攻击 (客户端预检查 + 服务端验证)
     */
    default boolean canAttack(ItemStack stack, Player player, AttackType attackType)
    {
        return true;
    }

    /**
     * 检查武器是否能够重装
     */
    default boolean canReload(ItemStack stack, Player player)
    {
        return true;
    }

    /**
     * 获取武器冷却时间（毫秒）
     */
    default long getCooldownTime(ItemStack stack)
    {
        return 3000;
    }

    /**
     * 获取武器最大蓄力时间（毫秒）
     */
    default long getMaxChargeTime(ItemStack stack)
    {
        return 3000;
    }

    /**
     * 获取武器重装时间（毫秒）
     */
    default long getReloadTime(ItemStack stack)
    {
        return 5000;
    }

    /**
     * 获取武器最大能量值
     */
    default int getMaxEnergy(ItemStack stack)
    {
        return 100;
    }

    /**
     * 获取当前能量值
     */
    default int getCurrentEnergy(ItemStack stack)
    {
        return 50;
    }

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

    /**
     * 客户端重装特效
     */
    @OnlyIn(Dist.CLIENT)
    default void onClientReloadEffects(ItemStack stack, Player player)
    {

    }
}
