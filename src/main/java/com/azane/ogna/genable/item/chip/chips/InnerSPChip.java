package com.azane.ogna.genable.item.chip.chips;

import com.azane.cjsop.annotation.JsonClassTypeBinder;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.capability.skill.ISkillCap;
import com.azane.ogna.capability.weapon.IOgnaWeaponCap;
import com.azane.ogna.combat.chip.ChipArg;
import com.azane.ogna.combat.chip.ChipSet;
import com.azane.ogna.combat.chip.ChipTiming;
import com.azane.ogna.genable.item.chip.NonItemChip;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * @author azaneNH37 (2025/8/13)
 */
@NoArgsConstructor
@Getter
@JsonClassTypeBinder(fullName = "chip.inner.sp", namespace = OriginiumArts.MOD_ID)
public class InnerSPChip extends NonItemChip
{
    @SerializedName("deviation")
    private int deviation = 50;
    @SerializedName("skill_end")
    private int skillEndSP;

    @Override
    public boolean canPlugIn(ChipSet chipSet, ChipArg arg) {return true;}

    @Override
    public List<ChipTiming> registerTiming()
    {
        return List.of(ChipTiming.ON_SKILL_END);
    }

    @Override
    public void onSkillEnd(Level level, Player player, ItemStack stack, IOgnaWeaponCap cap, ISkillCap skillCap)
    {
        if(level instanceof ServerLevel serverLevel)
            skillCap.modifySP(UniformInt.of(Math.max(1,skillEndSP-deviation),skillEndSP + deviation).sample(serverLevel.getRandom()),true,player,stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, List<Component> tooltip, TooltipFlag flag)
    {
        MutableComponent header = getHeader();
        header.append(Component.translatable("ogna.tip.chip.timing.skill.end")
            .append(" ")
            .append(Component.translatable("ogna.tip.sp.gain.range",Math.max(1,skillEndSP-deviation)/10, (skillEndSP + deviation)/10)));
        tooltip.add(header);
    }
}
