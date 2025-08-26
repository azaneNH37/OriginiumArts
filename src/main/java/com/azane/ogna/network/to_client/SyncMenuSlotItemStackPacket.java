package com.azane.ogna.network.to_client;

import com.azane.ogna.network.IOgnmPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

/**
 * @author azaneNH37 (2025/8/26)
 */
@AllArgsConstructor
@Getter
public class SyncMenuSlotItemStackPacket implements IOgnmPacket
{
    private final int windowID;
    private final int slotIndex;
    private final CompoundTag itemStackNbt;

    public SyncMenuSlotItemStackPacket(FriendlyByteBuf buffer)
    {
        this.windowID = buffer.readInt();
        this.slotIndex = buffer.readInt();
        this.itemStackNbt = buffer.readNbt();
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeInt(windowID);
        buffer.writeInt(slotIndex);
        buffer.writeNbt(itemStackNbt);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(NetworkEvent.Context context)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player != null && player.containerMenu.containerId == windowID)
        {
            player.containerMenu.setItem(slotIndex,player.containerMenu.incrementStateId(), ItemStack.of(itemStackNbt));
        }
    }
}
