package com.azane.ogna.combat.chip;

import com.azane.ogna.genable.item.chip.IChip;
import com.azane.ogna.item.OgnaChip;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.azane.ogna.lib.ColorHelper;
import com.azane.ogna.lib.IComponentDisplay;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.registry.ModAttribute;
import com.azane.ogna.resource.service.CommonDataService;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * @author azaneNH37 (2025-08-02)
 */
public class ChipSet implements INBTSerializable<CompoundTag>, IComponentDisplay
{
    public static final ChipSet FALLBACK = new ChipSet(ChipEnv.FALLBACK);

    @Getter
    private ChipEnv chipEnv;
    @Getter
    private int volumeTake;

    private final Map<ChipTiming, List<ResourceLocation>> triggeredChips = new ConcurrentHashMap<>();
    private final Map<ResourceLocation,Integer> chips = new ConcurrentHashMap<>();
    private final List<ResourceLocation> toRemove = new ArrayList<>();

    public ChipSet(ChipEnv chipEnv)
    {
        this.chipEnv = chipEnv;
        for(ChipTiming timing : ChipTiming.values())
            triggeredChips.put(timing, new ArrayList<>());
    }

    public boolean isEmpty()
    {
        return chips.isEmpty();
    }

    public static int getVolumeTake(ChipArg arg)
    {
        if(IOgnaWeapon.isWeapon(arg.getWeaponStack()))
        {
            IOgnaWeapon weapon = (IOgnaWeapon)arg.getWeaponStack().getItem();
            return weapon.getWeaponCap(arg.getWeaponStack()).getChipSet().getVolumeTake();
        }
        return 0;
    }

    public static int getVolumeLimit(ChipArg arg)
    {
        if(IOgnaWeapon.isWeapon(arg.getWeaponStack()))
        {
            IOgnaWeapon weapon = (IOgnaWeapon)arg.getWeaponStack().getItem();
            return (int) weapon.getWeaponCap(arg.getWeaponStack())
                .submitBaseAttrVal(ModAttribute.CHIP_SET_VOLUME.get(),
                    arg.getEntity() instanceof Player ? (Player) arg.getEntity() : null,
                    arg.getWeaponStack());
        }
        return 0;
    }

    public void cleanUp()
    {
        if(!toRemove.isEmpty())
        {
            toRemove.forEach(rl -> {
                chips.computeIfPresent(rl, (key, count) -> count > 1 ? count - 1 : null);
                IChip iChip = CommonDataService.get().getChip(rl);
                if(iChip != null)
                    iChip.registerTiming().forEach(timing -> triggeredChips.get(timing).remove(rl));
            });
            toRemove.clear();
        }
    }

    public boolean insertChip(IChip chip,ChipArg arg)
    {
        cleanUp();
        if(chip.canPlugIn(this,arg))
        {
            volumeTake += chip.getVolumeConsume(this, arg);
            chip.registerTiming().forEach(timing -> triggeredChips.get(timing).add(chip.getId()));
            chips.compute(chip.getId(), (rl, count) -> count == null ? 1 : count + 1);
            chip.onInsert(this, arg);
            return true;
        }
        return false;
    }

    public boolean removeChip(IChip chip, ChipArg arg)
    {
        if(chips.containsKey(chip.getId()))
        {
            volumeTake -= chip.getVolumeConsume(this, arg);
            chip.onRemove(this, arg);
            toRemove.add(chip.getId());
            return true;
        }
        return false;
    }

    public int getChipCount(ResourceLocation rl)
    {
        return chips.getOrDefault(rl, 0);
    }

    public List<IChip> getStoredChips(Predicate<IChip> predicate)
    {
        cleanUp();
        return chips.keySet().stream()
            .map(OgnaChip::getChip).filter(predicate).toList();
    }

    public List<IChip> gather(ChipTiming timing)
    {
        cleanUp();
        return triggeredChips.get(timing).stream().map(chip-> CommonDataService.get().getChip(chip))
            .filter(Objects::nonNull).toList();
    }

    @Override
    public CompoundTag serializeNBT()
    {
        var nbt = new CompoundTag();
        nbt.putString("env", chipEnv.name());
        var chipsNbt = new CompoundTag();
        chips.forEach((id, count) -> chipsNbt.putInt(id.toString(), count));
        nbt.put("chips", chipsNbt);
        var triggeredNbt = new CompoundTag();
        triggeredChips.forEach((timing, chipsList) -> {
            var lt = new ListTag();
            chipsList.forEach(rl->lt.add(StringTag.valueOf(rl.toString())));
            triggeredNbt.put(timing.name(), lt);
        });
        nbt.put("triggered", triggeredNbt);
        var lis = new ListTag();
        toRemove.forEach(rl -> lis.add(StringTag.valueOf(rl.toString())));
        nbt.put("toRemove", lis);
        nbt.putInt("volumeTake", volumeTake);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        chipEnv = ChipEnv.valueOf(nbt.getString("env"));
        var chipsNbt = nbt.getCompound("chips");
        chips.clear();
        chipsNbt.getAllKeys().forEach(key ->
            chips.put(RlHelper.parse(key), chipsNbt.getInt(key)));
        var triggeredNbt = nbt.getCompound("triggered");
        triggeredChips.clear();
        for(ChipTiming timing : ChipTiming.values())
        {
            var chipsList = new ArrayList<ResourceLocation>();
            var lt = triggeredNbt.getList(timing.name(), Tag.TAG_STRING);
            for(int i = 0; i < lt.size(); i++) {
                chipsList.add(RlHelper.parse(lt.getString(i)));
            }
            triggeredChips.put(timing, chipsList);
        }
        toRemove.clear();
        var lis = nbt.getList("toRemove", Tag.TAG_STRING);
        for(int i = 0; i < lis.size(); i++)
        {
            toRemove.add(RlHelper.parse(lis.getString(i)));
        }
        volumeTake = nbt.getInt("volumeTake");
    }

    @Override
    public void appendHoverText(ItemStack stack, List<Component> tooltip, TooltipFlag flag)
    {
        chips.forEach((rl,amt)->
            {
                var chip = OgnaChip.getChip(rl);
                if(chip != null)
                {
                    tooltip.add(
                        Component.empty()
                            .append("[")
                            .append(Component.translatable(chip.getDisplayContext().getName()).withStyle(Style.EMPTY.withColor(chip.getDisplayContext().getColor())))
                            .append("] ×")
                            .append(Component.literal(String.valueOf(amt))).withStyle(Style.EMPTY.withColor(ColorHelper.getGradientColor(amt)).withBold(true)));
                }
            }
        );
    }
}