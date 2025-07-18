package com.azane.ogna.combat.attr;

import com.azane.ogna.lib.RlHelper;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
public class AttrMap implements INBTSerializable<CompoundTag>
{
    private final Map<Attribute,AttrUnit> attributes = new ConcurrentHashMap<>();

    public AttrMap(Attribute... attrs)
    {
        for (Attribute attr : attrs)
        {
            attributes.put(attr, new AttrUnit());
        }
    }

    public AttrUnit getAttribute(Attribute attribute)
    {
        return attributes.computeIfAbsent(attribute, attr -> new AttrUnit());
    }

    public Matrices extractMatrices()
    {
        return new Matrices(this);
    }

    @Override
    public CompoundTag serializeNBT()
    {
        var nbt = new CompoundTag();
        attributes.forEach((attribute, unit) -> nbt.put(ForgeRegistries.ATTRIBUTES.getKey(attribute).toString(), unit.serializeNBT()));
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        nbt.getAllKeys().forEach(key -> {
            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(RlHelper.parse(key));
            if (attribute != null) {
                AttrUnit unit = new AttrUnit();
                unit.deserializeNBT(nbt.getCompound(key));
                attributes.put(attribute, unit);
            }
        });
    }

    @Getter
    public static class Matrices
    {
        private final ImmutableMap<Attribute,AttrMatrix> matrices;

        private Matrices(AttrMap attrMap)
        {
            ImmutableMap.Builder<Attribute, AttrMatrix> builder = ImmutableMap.builder();
            attrMap.attributes.forEach((attribute, unit) -> builder.put(attribute, unit.extractMatrix()));
            matrices = builder.build();
        }

        @Nullable
        private AttrMatrix get(Attribute attribute)
        {
            return matrices.get(attribute);
        }
    }
}
