package com.azane.ogna.genable.item.chip.chips;

import com.azane.cjsop.annotation.JsonClassTypeBinder;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.capability.skill.ISkillCap;
import com.azane.ogna.capability.weapon.IOgnaWeaponCap;
import com.azane.ogna.combat.chip.ChipTiming;
import com.azane.ogna.genable.item.chip.ItemChip;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * @author azaneNH37 (2025/8/15)
 */
@NoArgsConstructor
@JsonClassTypeBinder(fullName = "chip.tle.ora", namespace = OriginiumArts.MOD_ID)
public class TLEOpticalChip extends ItemChip
{
    @Override
    public List<ChipTiming> registerTiming() {return List.of(ChipTiming.ON_SKILL_START);}

    @Override
    public void onSkillStart(Level level, Player player, ItemStack stack, IOgnaWeaponCap cap, ISkillCap skillCap)
    {
        if(player instanceof ServerPlayer serverPlayer)
        {
            serverPlayer.forceAddEffect(new MobEffectInstance(MobEffects.INVISIBILITY,200,0),null);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, List<Component> tooltip, TooltipFlag flag)
    {
        super.appendHoverText(stack, tooltip, flag);
        tooltip.add(Component.translatable("ogna.genable.chip.tle.optical.content"));
    }
}
