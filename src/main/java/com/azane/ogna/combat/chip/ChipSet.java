package com.azane.ogna.combat.chip;

import com.azane.ogna.genable.item.chip.IChip;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.resource.service.CommonDataService;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ChipSet implements INBTSerializable<CompoundTag>
{
    public static final ChipSet FALLBACK = new ChipSet(ChipEnv.FALLBACK);

    @Getter
    private ChipEnv chipEnv;

    private final Map<ChipTiming, List<ResourceLocation>> triggeredChips = new ConcurrentHashMap<>();
    private final Map<ResourceLocation,Integer> chips = new ConcurrentHashMap<>();
    private final List<ResourceLocation> toRemove = new ArrayList<>();

    public ChipSet(ChipEnv chipEnv)
    {
        this.chipEnv = chipEnv;
        for(ChipTiming timing : ChipTiming.values())
            triggeredChips.put(timing, new ArrayList<>());
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
    }
}