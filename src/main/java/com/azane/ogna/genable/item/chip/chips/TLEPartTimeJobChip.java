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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * @author azaneNH37 (2025/8/15)
 */
@NoArgsConstructor
@JsonClassTypeBinder(fullName = "chip.tle.ptj",namespace = OriginiumArts.MOD_ID)
public class TLEPartTimeJobChip extends ItemChip
{
    @Override
    public List<ChipTiming> registerTiming() {return List.of(ChipTiming.CHIP_TICK);}

    @Override
    public void onTick(boolean isMainHand, Level level, Player player, ItemStack stack, IOgnaWeaponCap cap, ISkillCap skillCap, int tickCount)
    {
        if(level.isClientSide() || tickCount % 5 != 0 || skillCap.isActive())
            return;
        player.heal(isMainHand ? 1f : 0.2f);
    }

    @Override
    public void appendHoverText(ItemStack stack, List<Component> tooltip, TooltipFlag flag)
    {
        super.appendHoverText(stack, tooltip, flag);
        tooltip.add(Component.translatable("ogna.genable.chip.tle.part_time_job.content"));
    }
}
