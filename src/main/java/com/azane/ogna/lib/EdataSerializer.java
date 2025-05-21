package com.azane.ogna.lib;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.phys.AABB;

public final class EdataSerializer
{
    public static final EntityDataSerializer<AABB> AA_BB = new EntityDataSerializer<AABB>()
    {
        @Override
        public void write(FriendlyByteBuf pBuffer, AABB pValue)
        {
            pBuffer.writeDouble(pValue.minX).writeDouble(pValue.minY).writeDouble(pValue.minZ)
                .writeDouble(pValue.maxX).writeDouble(pValue.maxY).writeDouble(pValue.maxZ);
        }

        @Override
        public AABB read(FriendlyByteBuf pBuffer)
        {
            return new AABB(pBuffer.readDouble(),pBuffer.readDouble(),pBuffer.readDouble(),
                pBuffer.readDouble(),pBuffer.readDouble(),pBuffer.readDouble());
        }

        @Override
        public AABB copy(AABB pValue)
        {
            return new AABB(pValue.minX, pValue.minY, pValue.minZ, pValue.maxX, pValue.maxY, pValue.maxZ);
        }
    };

    public static void registerES()
    {
        EntityDataSerializers.registerSerializer(AA_BB);
    }
}
