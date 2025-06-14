package com.azane.ogna.capability.weapon;

import com.azane.ogna.item.genable.AttackType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

public interface IOgnaWeaponCap extends INBTSerializable<CompoundTag>
{
    IOgnaWeaponCap FALLBACK = new IOgnaWeaponCap()
    {
        @Override
        public CompoundTag serializeNBT(){return new CompoundTag(); }

        @Override
        public void deserializeNBT(CompoundTag nbt){}
    };
    /**
     * 检查武器是否能够攻击 (客户端预检查 + 服务端验证)
     */
    default boolean canAttack(ItemStack stack, Player player, AttackType attackType)
    {
        return false;
    }
    /**
     * 检查武器是否能够重装
     */
    default boolean canReload(ItemStack stack, Player player)
    {
        return false;
    }
    /**
     * 获取武器冷却时间（毫秒）
     */
    default long getCooldownTime(ItemStack stack)
    {
        return 100000;
    }

    /**
     * 获取武器最大蓄力时间（毫秒）
     */
    default long getMaxChargeTime(ItemStack stack)
    {
        return 1000;
    }

    /**
     * 获取武器重装时间（毫秒）
     */
    default long getReloadTime(ItemStack stack)
    {
        return 1000;
    }

    /**
     * 获取武器最大能量值
     */
    default int getMaxEnergy(ItemStack stack)
    {
        return 1000;
    }

    /**
     * 获取当前能量值
     */
    default int getCurrentEnergy(ItemStack stack)
    {
        return 0;
    }
}