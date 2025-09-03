package com.azane.ogna.genable.item.skill.skills;

import com.azane.cjsop.annotation.JsonClassTypeBinder;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.combat.data.CastContext;
import com.azane.ogna.genable.item.skill.DefaultSkillDataBase;
import net.minecraft.world.entity.LivingEntity;

/**
 * @author azaneNH37 (2025-07-29)
 */
@JsonClassTypeBinder(fullName = "skill.key_of_chronology", namespace = OriginiumArts.MOD_ID)
public class KeyOfChronology extends DefaultSkillDataBase
{
    @Override
    public void onImpactEntity(LivingEntity target, CastContext castContext)
    {
        var attacker = castContext.getCaster();
        if (attacker != null)
        {
            double deltaX = target.getX() - attacker.getX();
            double deltaZ = target.getZ() - attacker.getZ();
            double knockbackStrength = 0.2D;
            target.knockback(knockbackStrength, deltaX, deltaZ);
        }
    }
}
