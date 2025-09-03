package com.azane.ogna.genable.item.skill.skills;

import com.azane.cjsop.annotation.JsonClassTypeBinder;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.combat.data.*;
import com.azane.ogna.genable.item.skill.DefaultSkillDataBase;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.network.OgnmChannel;
import com.azane.ogna.network.to_client.FxEntityEffectTriggerPacket;
import com.azane.ogna.registry.ModAttribute;
import com.azane.ogna.registry.ModEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

/**
 * @author azaneNH37 (2025-08-09)
 */
@JsonClassTypeBinder(fullName = "skill.destreza", namespace = OriginiumArts.MOD_ID)
public class Destreza extends DefaultSkillDataBase
{
    @Override
    public void onImpactEntity(LivingEntity target, CastContext castContext)
    {
        target.forceAddEffect(new MobEffectInstance(ModEffect.SAND_POTION.get(),
            (int) castContext.getMatrix(ModAttribute.EFFECT_TICK.get()).submit(65) ,
            (int) castContext.getMatrix(ModAttribute.EFFECT_LEVEL.get()).submit(2)),null);
        OgnmChannel.DEFAULT.sendToWithinRange(
            new FxEntityEffectTriggerPacket(RlHelper.parse("ognmarts:sand_poison"), target.getId(),false),
            castContext.getServerLevel(),
            target.getOnPos(),
            128
        );
    }
}
