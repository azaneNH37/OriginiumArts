package com.azane.ogna.combat.util;

import java.util.ArrayList;
import java.util.List;
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

    static
    {
        for(int i=0;i<=LIMIT1;i++)
            EXP_TABLE.add(CURVE1.apply(i));
        for(int i=LIMIT1+1;i<=LIMIT2;i++)
            EXP_TABLE.add(CURVE2.apply(i));
        for(int i=LIMIT2+1;i<=LIMIT3;i++)
            EXP_TABLE.add(CURVE3.apply(i));
        EXP_TABLE.add(CURVE4.apply(LIMIT3+1));
    }

    public static int getLevelByExp(long exp)
    {
        for(int i=0;i<EXP_TABLE.size();i++)
            if(exp<=EXP_TABLE.get(i))
                return i;
        return EXP_TABLE.size()-1;
    }

}
