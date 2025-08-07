package com.azane.ogna.genable.item.skill.skills;

import com.azane.cjsop.annotation.JsonClassTypeBinder;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.capability.skill.ISkillCap;
import com.azane.ogna.combat.data.ArkDamageSource;
import com.azane.ogna.combat.data.CombatUnit;
import com.azane.ogna.combat.data.MoveUnit;
import com.azane.ogna.combat.data.SelectorUnit;
import com.azane.ogna.combat.util.CombatFirer;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.genable.entity.ITargetable;
import com.azane.ogna.genable.item.skill.DefaultSkillDataBase;
import com.azane.ogna.item.weapon.AttackType;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;

@JsonClassTypeBinder(fullName = "skill.volcano", namespace = OriginiumArts.MOD_ID)
public class Volcano extends DefaultSkillDataBase
{
    @Override
    public void onSkillTick(Level level, Player player, IOgnaWeapon weapon, ItemStack stack, boolean isOpen)
    {
        if(isOpen)
        {
            if(level instanceof ServerLevel serverLevel)
            {
                ServerPlayer serverPlayer = (ServerPlayer) player;
                ISkillCap skillCap = weapon.getWeaponCap(stack).getSkillCap();
                double RD = skillCap.getRD();
                if(((int)RD) % 6 == 0)
                {
                    List<LivingEntity> entities = serverLevel.getEntitiesOfClass(LivingEntity.class,player.getBoundingBox().inflate(24),entity -> !(entity instanceof  Player));
                    Collections.shuffle(entities);
                    for(int i=0;i<Math.min(2,entities.size());i++)
                    {
                        CombatFirer.fireTargetBullet(serverLevel,serverPlayer,weapon,weapon.getWeaponCap(stack),stack,"skill","skill",
                            new MoveUnit.Builder().targetEntity(entities.get(i)).xRot(-45).yRot(serverLevel.random.nextInt(0,360)).build());
                    }
                }
            }
        }
    }
}
