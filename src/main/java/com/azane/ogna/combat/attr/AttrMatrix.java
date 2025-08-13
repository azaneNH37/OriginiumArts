package com.azane.ogna.combat.attr;

import com.azane.ogna.debug.log.DebugLogger;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 属性矩阵类，用于存储乘区，作为轻量级类可以被大量创建和传递
 * @author azaneNH37 (2025-07-25)
 */
public class AttrMatrix implements INBTSerializable<CompoundTag>
{
    public static final AttrMatrix UNIT_MATRIX = new AttrMatrix(true);

    private final Map<DmgBucket,Double> matrix = new ConcurrentHashMap<>();
    @Getter
    private boolean immutable;

    public AttrMatrix(boolean immutable)
    {
        this.immutable = immutable;
        for (DmgBucket bucket : DmgBucket.values())
        {
            matrix.put(bucket, bucket.getInitialVal());
        }
    }

    public static AttrMatrix combine(boolean immutable, AttrMatrix... matrices)
    {
        AttrMatrix result = new AttrMatrix(false);
        for (AttrMatrix matrix : matrices)
            if(matrix != null)
                result.absorb(matrix);
        result.immutable = immutable;
        return result;
    }

    public AttrMatrix copy()
    {
        AttrMatrix copy = new AttrMatrix(false);
        matrix.forEach(copy::apply);
        copy.immutable = immutable;
        return copy;
    }

    public AttrMatrix copy(boolean immutable)
    {
        AttrMatrix cpy = copy();
        cpy.immutable = immutable;
        return cpy;
    }

    public void lock()
    {
        this.immutable = true;
    }

    public void absorb(AttrMatrix other)
    {
        if (immutable)
        {
            DebugLogger.error("Invalid absorption on immutable AttrMatrix");
            return;
        }
        other.matrix.forEach(this::apply);
    }

    private void modify(DmgBucket bucket, double value, boolean isApply)
    {
        if (immutable)
        {
            DebugLogger.error("Invalid absorption on immutable AttrMatrix");
            return;
        }
        if (!matrix.containsKey(bucket))
            throw new IllegalArgumentException("Invalid DmgBucket: " + bucket);
        matrix.put(bucket, (isApply ? bucket.getApply() : bucket.getRemove()).apply(matrix.get(bucket), value));
    }

    public void apply(DmgBucket bucket,double value){ modify(bucket,value,true);}
    public void remove(DmgBucket bucket,double value){ modify(bucket,value,false);}

    public double submit(double baseValue)
    {
        return ((baseValue+matrix.get(DmgBucket.DIRECT_ADD))*(1D+matrix.get(DmgBucket.DIRECT_MUL))+matrix.get(DmgBucket.TOTAL_ADD))*matrix.get(DmgBucket.TOTAL_MUL);
    }

    @Override
    public CompoundTag serializeNBT()
    {
        var tag = new CompoundTag();
        matrix.forEach((bucket, value) -> {
            tag.putDouble(bucket.name(), value);
        });
        tag.putBoolean("immutable", immutable);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        matrix.clear();
        nbt.getAllKeys().forEach(key -> {
            try {
                DmgBucket bucket = DmgBucket.valueOf(key);
                matrix.put(bucket, nbt.getDouble(key));
            } catch (IllegalArgumentException ignored) {}
        });
        immutable = nbt.getBoolean("immutable");
    }
}
