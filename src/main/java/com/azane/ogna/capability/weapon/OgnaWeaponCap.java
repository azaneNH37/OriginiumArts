package com.azane.ogna.capability.weapon;

import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.genable.data.OgnaWeaponData;
import com.azane.ogna.item.genable.AttackType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class OgnaWeaponCap implements IOgnaWeaponCap
{
    private OgnaWeaponData baseData;

    public OgnaWeaponCap(OgnaWeaponData baseData)
    {
        this.baseData = baseData;
    }

    @Override
    public boolean canAttack(ItemStack stack, Player player, AttackType attackType)
    {
        return baseData.isCanAttack() && baseData.getConsumption() <= getCurrentEnergy(stack);
    }

    @Override
    public boolean canReload(ItemStack stack, Player player)
    {
        return baseData.isCanReload() && baseData.getMaxEnergy() > getCurrentEnergy(stack);
    }

    @Override
    public int getCurrentEnergy(ItemStack stack)
    {
        return 100;
    }

    @Override
    public long getCooldownTime(ItemStack stack)
    {
        return baseData.getCooldownTime();
    }

    @Override
    public long getMaxChargeTime(ItemStack stack)
    {
        return baseData.getMaxChargeTime();
    }

    @Override
    public long getReloadTime(ItemStack stack)
    {
        return baseData.getReloadTime();
    }

    @Override
    public int getMaxEnergy(ItemStack stack)
    {
        return baseData.getMaxEnergy();
    }

    @Override
    public CompoundTag serializeNBT()
    {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {

    }
}
