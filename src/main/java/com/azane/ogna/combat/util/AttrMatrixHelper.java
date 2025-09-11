package com.azane.ogna.combat.util;

import com.azane.ogna.capability.weapon.IOgnaWeaponCap;
import com.azane.ogna.combat.attr.AttrMap;
import com.azane.ogna.combat.attr.AttrMatrix;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.List;


/**
 * @author azaneNH37 (2025/9/11)
 */
public class AttrMatrixHelper
{
    private final AttrMap.Matrices matrices = new AttrMap.Matrices();
    private AttrMatrix cache = null;

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

    public double submit(double baseValue)
    {
        return cache == null ? baseValue : cache.submit(baseValue);
    }

    public static double commonSubmit(Attribute attribute, double baseValue,IOgnaWeaponCap weaponCap)
    {
        return new AttrMatrixHelper().setAttributes(attribute).weapon(weaponCap).cache().submit(baseValue);
    }
}
