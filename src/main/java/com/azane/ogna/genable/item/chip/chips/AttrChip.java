package com.azane.ogna.genable.item.chip.chips;

import com.azane.cjsop.annotation.JsonClassTypeBinder;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.combat.chip.ChipArg;
import com.azane.ogna.combat.chip.ChipEnv;
import com.azane.ogna.combat.chip.ChipSet;
import com.azane.ogna.combat.chip.ChipTiming;
import com.azane.ogna.combat.data.AttrModifier;
import com.azane.ogna.genable.item.chip.ItemChip;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@JsonClassTypeBinder(fullName = "chip.attr",namespace = OriginiumArts.MOD_ID)
public class AttrChip extends ItemChip
{
    @SerializedName("env")
    private ChipEnv chipEnv = ChipEnv.FALLBACK;

    @SerializedName("list")
    private List<AttrModifier> modifiers = new ArrayList<>();

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
            if(IOgnaWeapon.isWeapon(arg.getWeaponStack()))
            {
                IOgnaWeapon weapon = (IOgnaWeapon)arg.getWeaponStack().getItem();
                modifiers.forEach(weapon.getWeaponCap(arg.getWeaponStack())::acceptModifier);
            }
        }
    }

    @Override
    public void onRemove(ChipSet chipSet, ChipArg arg)
    {
        if(chipEnv == ChipEnv.WEAPON)
        {
            if(IOgnaWeapon.isWeapon(arg.getWeaponStack()))
            {
                IOgnaWeapon weapon = (IOgnaWeapon)arg.getWeaponStack().getItem();
                modifiers.forEach(weapon.getWeaponCap(arg.getWeaponStack())::removeModifier);
            }
        }
    }
}
