package com.azane.ogna.world.effect;

import com.azane.ogna.combat.util.ArkDmgTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;

/**
 * 只是一个临时的解决方案，丑陋
 * @author azaneNH37 (2025-08-11)
 */
public class SandPotionEffect extends MobEffect
{
    public SandPotionEffect()
    {
        super(MobEffectCategory.HARMFUL, 0xC2B280);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier)
    {
        float baseVal = 1.5f+1F*pAmplifier;
        if(pLivingEntity instanceof RangedAttackMob)
            baseVal *= 2;
        pLivingEntity.hurt(new DamageSource(ArkDmgTypes.getHolder(ArkDmgTypes.DOT, pLivingEntity.level().isClientSide())), baseVal);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier)
    {
        return pDuration % Math.max(20 - pAmplifier*3,5) == 0;
    }

    @Override
    public Component getDisplayName()
    {
        return Component.translatable("ogna.genable.chip.inner.sand_poison.name")
            .append(" ("+Component.translatable("ogna.genable.chip.inner.sand_poison.content").getString()+")");
    }
}
