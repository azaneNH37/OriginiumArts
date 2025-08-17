package com.azane.ogna.genable.item.chip.chips;

import com.azane.cjsop.annotation.JsonClassTypeBinder;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.combat.chip.ChipArg;
import com.azane.ogna.combat.chip.ChipEnv;
import com.azane.ogna.combat.chip.ChipSet;
import com.azane.ogna.combat.chip.ChipTiming;
import com.azane.ogna.combat.data.AttrModifier;
import com.azane.ogna.genable.item.chip.ItemChip;
import com.azane.ogna.genable.item.chip.NonItemChip;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author azaneNH37 (2025-08-11)
 */
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@JsonClassTypeBinder(fullName = "chip.inner.attr",namespace = OriginiumArts.MOD_ID)
public class InnerAttrChip extends NonItemChip
{
    @SerializedName("env")
    private ChipEnv chipEnv = ChipEnv.FALLBACK;

    @SerializedName("list")
    private List<AttrModifier> modifiers = new ArrayList<>();

    @Override
    public boolean canPlugIn(ChipSet chipSet, ChipArg arg) {return true;}

    @Override
    public List<ChipTiming> registerTiming()
    {
        return List.of();
    }

    @Override
    public void onInsert(ChipSet chipSet, ChipArg arg)
    {
        if(chipEnv == ChipEnv.WEAPON)
        {
            Optional.ofNullable(arg.getWeaponCap()).ifPresent(cap->modifiers.forEach(cap::acceptModifier));
        }
    }

    @Override
    public void onRemove(ChipSet chipSet, ChipArg arg)
    {
        if(chipEnv == ChipEnv.WEAPON)
        {
            Optional.ofNullable(arg.getWeaponCap()).ifPresent(cap->modifiers.forEach(cap::removeModifier));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, List<Component> tooltip, TooltipFlag flag)
    {
        MutableComponent header = getHeader();
        modifiers.forEach(attrModifier -> header.append(attrModifier.getComponent(stack,tooltip,flag)).append(" "));
        tooltip.add(header);
    }
}
