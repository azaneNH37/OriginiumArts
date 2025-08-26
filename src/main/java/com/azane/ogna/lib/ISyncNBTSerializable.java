package com.azane.ogna.lib;

import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * @author azaneNH37 (2025/8/26)
 */
public interface ISyncNBTSerializable<T extends Tag> extends INBTSerializable<T>
{
    T serializeSyncNBT();
}
