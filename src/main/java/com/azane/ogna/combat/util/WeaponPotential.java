package com.azane.ogna.combat.util;

import com.azane.ogna.combat.attr.DmgBucket;
import com.azane.ogna.combat.data.AttrModifier;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.registry.ModAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * @author azaneNH37 (2025/9/1)
 */
public class WeaponPotential
{
    public static final int LIMIT1 = 5;
    public static final int LIMIT2 = 16;
    public static final int LIMIT3 = 50;

    public static final Function<Integer,Long> CURVE1 = lv-> (long)Math.pow(lv,1.5)*300;
    public static final Function<Integer,Long> CURVE2 = lv-> (long)Math.pow(lv,2)*120;
    public static final Function<Integer,Long> CURVE3 = lv-> (long)Math.pow(lv,lv/6d)*15;
    public static final Function<Integer,Long> CURVE4 = lv-> Long.MAX_VALUE;

    public static final List<Long> EXP_TABLE = new ArrayList<>();

    public static final List<List<AttrModifier>> POTENTIALS = new ArrayList<>();

    static
    {
        for(int i=0;i<=LIMIT1;i++)
            EXP_TABLE.add(CURVE1.apply(i));
        for(int i=LIMIT1+1;i<=LIMIT2;i++)
            EXP_TABLE.add(CURVE2.apply(i));
        for(int i=LIMIT2+1;i<=LIMIT3;i++)
            EXP_TABLE.add(CURVE3.apply(i));
        EXP_TABLE.add(CURVE4.apply(LIMIT3+1));
        // init potentials
        for(int i=0;i<EXP_TABLE.size();i++)
            POTENTIALS.add(new ArrayList<>());
        POTENTIALS.get(1).add(AttrModifier.of(ModAttribute.CHIP_SET_VOLUME.getId(), UUID.fromString("c5a45d61-fe17-46d8-a3a7-1f42db9215dd"), DmgBucket.TOTAL_ADD,15,1));
        POTENTIALS.get(1).add(AttrModifier.of(ModAttribute.WEAPON_ENERGY_STORE.getId(),UUID.fromString("74e01974-7812-486c-978b-f88505b005ae"), DmgBucket.DIRECT_MUL,0.2,1));
        POTENTIALS.get(2).add(AttrModifier.of(RlHelper.parse("generic.attack_damage"), UUID.fromString("43b7350d-72ca-4b11-b35c-b0a2b17dae51"), DmgBucket.TOTAL_MUL,1.2,1));
        POTENTIALS.get(3).add(AttrModifier.of(ModAttribute.CHIP_SET_VOLUME.getId(), UUID.fromString("16d8d207-66db-4987-868e-417ec3861263"), DmgBucket.TOTAL_ADD,15,1));
        POTENTIALS.get(3).add(AttrModifier.of(ModAttribute.WEAPON_ATTACK_CD.getId(), UUID.fromString("b86516f8-76df-4d69-aa9b-31632b10dbb6"), DmgBucket.TOTAL_MUL,0.9,1));
        POTENTIALS.get(4).add(AttrModifier.of(ModAttribute.CHIP_SET_VOLUME.getId(), UUID.fromString("3908ceb7-32a7-4289-9050-484208570827"), DmgBucket.TOTAL_ADD,15,1));
        POTENTIALS.get(4).add(AttrModifier.of(ModAttribute.WEAPON_ENERGY_CONSUME.getId(), UUID.fromString("85e82a0c-d9ec-44d8-b4e0-4f2a62a314d9"), DmgBucket.DIRECT_ADD,-2,1));
        POTENTIALS.get(5).add(AttrModifier.of(ModAttribute.CHIP_SET_VOLUME.getId(), UUID.fromString("73a051bb-53d3-4fa6-a476-e91757b0add3"), DmgBucket.TOTAL_ADD,20,1));
        for (int i=6;i<=LIMIT2;i++)
            POTENTIALS.get(i).add(AttrModifier.of(ModAttribute.CHIP_SET_VOLUME.getId(), UUID.fromString("42044e63-8009-42e7-9a06-8864b059a486"), DmgBucket.TOTAL_ADD,10,20));
        for(int i=LIMIT2+1;i<=LIMIT3;i++)
            POTENTIALS.get(i).add(AttrModifier.of(ModAttribute.CHIP_SET_VOLUME.getId(), UUID.fromString("f4e2e8b3-2f0c-4d1c-8f7a-1e5f3c6e8b9a"), DmgBucket.TOTAL_ADD,10,50));
    }

    public static int getLevelByExp(long exp)
    {
        for(int i=0;i<EXP_TABLE.size();i++)
            if(exp<=EXP_TABLE.get(i))
                return i;
        return EXP_TABLE.size()-1;
    }

    /*
    public static void applyPotential(IOgnaWeaponCap weaponCap)
    {
        int newLevel = getLevelByExp(weaponCap.getWpExp());
        if(newLevel>weaponCap.getWpLevel())
        {
            weaponCap.setWpLevel(newLevel);
            weaponCap.getAttrModifiers().clear();
            for(int i=1;i<=newLevel;i++)
                weaponCap.getAttrModifiers().addAll(POTENTIALS.get(i));
        }
    }

     */
}
