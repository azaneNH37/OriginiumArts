package com.azane.ogna.network.to_client;

import com.azane.ogna.block.entity.InjectEPTBlockEntity;
import com.azane.ogna.network.IOgnmPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

/**
 * @author azaneNH37 (2025-08-02)
 */
@Getter
@AllArgsConstructor
public class SyncEPTWeaponStackCapPacket implements IOgnmPacket
{
    final UUID stackUUID;
    final CompoundTag stackNBT;
    final BlockPos blockPos;
    final int slotIndex;

    public SyncEPTWeaponStackCapPacket(FriendlyByteBuf buf)
    {
        this.stackUUID = buf.readUUID();
        this.stackNBT = buf.readNbt();
        this.blockPos = buf.readBlockPos();
        this.slotIndex = buf.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeUUID(stackUUID);
        buffer.writeNbt(stackNBT);
        buffer.writeBlockPos(blockPos);
        buffer.writeInt(slotIndex);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(NetworkEvent.Context context)
    {
        Level level = Minecraft.getInstance().level;
        if(level.isClientSide)
        {
            if(level.getBlockEntity(blockPos) instanceof InjectEPTBlockEntity EPTBlockEntity)
            {
                EPTBlockEntity.handleSync(this);
            }
        }
    }
}
