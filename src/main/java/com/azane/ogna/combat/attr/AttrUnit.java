package com.azane.ogna.combat.attr;

import com.azane.ogna.combat.data.AttrModifier;
import lombok.NoArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 属性单位类，用于存储单个属性的值，包含{@link AttrMatrix}和所有被记录的modifier
 */
@NoArgsConstructor
public class AttrUnit implements INBTSerializable<CompoundTag>
{
    private final AttrMatrix matrix = new AttrMatrix(false);
    private final Map<UUID,Integer> modifiers = new ConcurrentHashMap<>();

    public AttrMatrix extractMatrix()
    {
        return matrix.copy();
    }

    public void acceptModifier(AttrModifier modifier)
    {
        modifiers.computeIfPresent(modifier.getId(),(id,v)->{
            if (v < modifier.getStackSize())
            {
                v++;
                matrix.apply(modifier.getBucket(),modifier.getAmount());
                return v;
            }
            return v;
        });
        modifiers.computeIfAbsent(modifier.getId(), id -> {
            matrix.apply(modifier.getBucket(), modifier.getAmount());
            return 1;
        });
    }

    public void removeModifier(AttrModifier modifier)
    {
        modifiers.computeIfPresent(modifier.getId(), (id, v) -> {
            if (v > 1)
            {
                v--;
                matrix.remove(modifier.getBucket(), modifier.getAmount());
                return v;
            }
            matrix.remove(modifier.getBucket(), modifier.getAmount());
            return null;
        });
    }

    @Override
    public CompoundTag serializeNBT()
    {
        var nbt = new CompoundTag();
        nbt.put("matrix", matrix.serializeNBT());
        var modifiersNbt = new CompoundTag();
        modifiers.forEach((id,stack)-> modifiersNbt.putInt(id.toString(), stack));
        nbt.put("modifiers", modifiersNbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        matrix.deserializeNBT(nbt.getCompound("matrix"));
        var modifiersNbt = nbt.getCompound("modifiers");
        modifiers.clear();
        modifiersNbt.getAllKeys().forEach(key -> {
            UUID id = UUID.fromString(key);
            int stackSize = modifiersNbt.getInt(key);
            modifiers.put(id, stackSize);
        });
    }
}
