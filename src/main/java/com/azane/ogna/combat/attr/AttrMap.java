package com.azane.ogna.combat.attr;

import com.azane.ogna.combat.data.AttrModifier;
import com.azane.ogna.lib.RlHelper;
import com.google.common.collect.ImmutableMap;
import lombok.NoArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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

    public void acceptModifier(AttrModifier modifier)
    {
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(modifier.getAttribute());
        if (attribute != null)
            getAttribute(attribute).acceptModifier(modifier);
    }

    public void removeModifier(AttrModifier modifier)
    {
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(modifier.getAttribute());
        if (attribute != null)
            getAttribute(attribute).removeModifier(modifier);
    }

    public Matrices extractMatrices(Set<Attribute> requirement)
    {
        return new Matrices(this, requirement);
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

    public static class Matrices
    {
        public static final Matrices EMPTY = new Matrices();

        private final ImmutableMap<Attribute,AttrMatrix> matrices;

        private Matrices()
        {
            matrices = ImmutableMap.of();
        }

        private Matrices(ImmutableMap.Builder<Attribute, AttrMatrix> builder){matrices = builder.build();}

        private Matrices(AttrMap attrMap, Set<Attribute> requirement)
        {
            ImmutableMap.Builder<Attribute, AttrMatrix> builder = ImmutableMap.builder();
            requirement.forEach(attribute -> {
                AttrMatrix matrix = attrMap.getAttribute(attribute).extractMatrix();
                if (matrix != null) {
                    builder.put(attribute, matrix);
                }
            });
            matrices = builder.build();
        }

        public static Matrices combine(Matrices... matrices)
        {
            Map<Attribute, AttrMatrix> tmp = new HashMap<>();
            ImmutableMap.Builder<Attribute, AttrMatrix> builder = ImmutableMap.builder();
            for (Matrices matrix : matrices)
            {
                if (matrix == null || matrix.matrices.isEmpty()) continue;
                matrix.matrices.forEach((attribute, attrMatrix) -> {
                    if (tmp.containsKey(attribute))
                        tmp.get(attribute).absorb(attrMatrix);
                    else
                        tmp.put(attribute, attrMatrix.copy());
                });
            }
            tmp.forEach((a,am)-> am.lock());
            tmp.forEach(builder::put);
            return new Matrices(builder);
        }

        @Nullable
        public AttrMatrix get(Attribute attribute)
        {
            return matrices.get(attribute);
        }
    }
}
