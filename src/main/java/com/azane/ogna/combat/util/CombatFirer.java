package com.azane.ogna.combat.util;

import com.azane.ogna.combat.data.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

/**
 * @author azaneNH37 (2025-08-09)
 */
public final class CombatFirer
{
    public static Entity fireDefault(ServerLevel level, ServerPlayer player,
                            ItemStack weaponStack, String AEunitID, String DDunitID)
    {
        return fireDefault(level, player, weaponStack, AEunitID, DDunitID,
            new MoveUnit.Builder().initialPos(player.getEyePosition()).xRot(player.getXRot()).yRot(player.getYRot()).build());
    }

    public static Entity fireDefault(ServerLevel level, ServerPlayer player,
                                     ItemStack weaponStack, String AEunitID, String DDunitID,
                                     MoveUnit moveUnit)
    {
        CastContext castContext = new CastContext.Builder()
            .serverLevel(level).caster(player).weapon(weaponStack)
            .attackEntityUnit(AEunitID).damageDataSet(DDunitID)
            .baseFilter(SelectRule.NON_PLAYER.getFilter()).baseVal(player.getAttribute(Attributes.ATTACK_DAMAGE).getValue())
            .moveUnit(moveUnit)
            .build();
        return castContext.createAttackEntity();
    }
}
