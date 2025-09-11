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

/**
 * @author azaneNH37 (2025-08-02)
 */
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

    public void clear()
    {
        attributes.clear();
    }

    public Matrices extractMatrices(Iterable<Attribute> requirement)
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

    public CompoundTag serializeNBTFiltered(@Nullable Set<Attribute> filter)
    {
        if (filter == null || filter.isEmpty())
            return serializeNBT();
        var nbt = new CompoundTag();
        attributes.forEach((attribute, unit) -> {
            if (filter.contains(attribute))
                nbt.put(ForgeRegistries.ATTRIBUTES.getKey(attribute).toString(), unit.serializeNBT());
        });
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

        private final Map<Attribute,AttrMatrix> matrices;

        public Matrices()
        {
            matrices = new HashMap<>();
        }

        private Matrices(AttrMap attrMap, Iterable<Attribute> requirement)
        {
            matrices = new HashMap<>();
            requirement.forEach(attribute -> {
                AttrMatrix matrix = attrMap.getAttribute(attribute).extractMatrix();
                if (matrix != null) {
                    matrices.put(attribute, matrix);
                }
            });
        }

        public static Matrices combine(Matrices... matrices)
        {
            Matrices result = new Matrices();
            for (Matrices matrix : matrices)
            {
                if (matrix != null)
                    result.absorb(matrix);
            }
            return result;
        }

        public void absorb(Matrices other)
        {
            if (other == null || other.matrices.isEmpty())
                return;
            other.matrices.forEach((attribute, attrMatrix) -> {
                if (matrices.containsKey(attribute))
                    matrices.get(attribute).absorb(attrMatrix);
                else
                    matrices.put(attribute, attrMatrix.copy());
            });
        }

        public AttrMatrix get(Attribute attribute)
        {
            return matrices.getOrDefault(attribute,AttrMatrix.UNIT_MATRIX);
        }

        public Set<Map.Entry<Attribute, AttrMatrix>> entrySet()
        {
            return ImmutableMap.copyOf(matrices).entrySet();
        }

        public void clear() {matrices.clear();}
    }
}
