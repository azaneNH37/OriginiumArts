package com.azane.ogna.combat.util;

import com.azane.ogna.capability.weapon.IOgnaWeaponCap;
import com.azane.ogna.combat.attr.AttrMap;
import com.azane.ogna.combat.attr.AttrMatrix;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import java.util.List;


/**
 * @author azaneNH37 (2025/9/11)
 */
public class AttrMatrixHelper
{
    private final AttrMap.Matrices matrices = new AttrMap.Matrices();
    private AttrMatrix cache = null;

    private double minValue = 0;
    private double maxValue = Double.MAX_VALUE;

    private Iterable<Attribute> attributes;

    public AttrMatrixHelper(){}

    public AttrMatrixHelper setAttributes(Attribute... attributes)
    {
        this.attributes = List.of(attributes);
        return this;
    }

    public AttrMatrixHelper setAttributes(Iterable<Attribute> attributes)
    {
        this.attributes = attributes;
        return this;
    }

    public AttrMatrixHelper clear()
    {
        matrices.clear();
        cache = null;
        return this;
    }

    public AttrMatrixHelper weapon(IOgnaWeaponCap weaponCap)
    {
        if(attributes == null)
            throw new IllegalStateException("Attributes not set");
        matrices.absorb(weaponCap.extractMatrices(attributes));
        return this;
    }

    public AttrMatrixHelper cache(Iterable<Attribute> needs)
    {
        cache = new AttrMatrix(false);
        for (var attr : needs)
        {
            cache.absorb(matrices.get(attr));
        }
        cache.lock();
        return this;
    }

    public AttrMatrixHelper cache(Attribute need)
    {
        cache = matrices.get(need).copy();
        cache.lock();
        return this;
    }

    public AttrMatrixHelper cache()
    {
        return cache(attributes);
    }

    public AttrMatrixHelper clampMin(double minValue)
    {
        this.minValue = minValue;
        return this;
    }
    public AttrMatrixHelper clampMax(double maxValue)
    {
        this.maxValue = maxValue;
        return this;
    }

    public double submit(double baseValue)
    {
        return Mth.clamp(cache == null ? baseValue : cache.submit(baseValue),minValue,maxValue);
    }

    public static double commonSubmit(Attribute attribute, double baseValue,IOgnaWeaponCap weaponCap)
    {
        return new AttrMatrixHelper().setAttributes(attribute).weapon(weaponCap).cache().submit(baseValue);
    }
}
