package com.azane.ogna.lib;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;

public final class LevelHelper
{
    public static int getGroundHightAtFullChunk(Level level, BlockPos pos,Heightmap.Types type)
    {
        ChunkPos chunk = new ChunkPos(pos);
        ChunkAccess access = level.getChunk(chunk.x, chunk.z, ChunkStatus.FULL,false);
        if(access == null)
            return pos.getY();
        return access.getHeight(type, pos.getX() & 15, pos.getZ() & 15);
    }
}
