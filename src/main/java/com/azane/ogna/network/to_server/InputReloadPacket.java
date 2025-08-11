package com.azane.ogna.network.to_server;

import com.azane.ogna.client.gameplay.ReloadState;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.azane.ogna.network.IOgnmPacket;
import com.azane.ogna.network.OgnmChannel;
import com.azane.ogna.network.to_client.SyncReloadStatePacket;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

/**
 * @author azaneNH37 (2025-08-10)
 */
@Getter
public class InputReloadPacket implements IOgnmPacket
{
    public enum ReloadType{
        START,END
    }

    private long timeStamp;

    private ReloadType type;
    private String uuid;

    public InputReloadPacket(ReloadType type,String uuid)
    {
        this.timeStamp = System.currentTimeMillis();
        this.type = type;
        this.uuid = uuid;
    }
    public InputReloadPacket(FriendlyByteBuf buffer)
    {
        this.timeStamp = buffer.readLong();
        this.type = ReloadType.values()[buffer.readInt()];
        this.uuid = buffer.readUUID().toString();
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeLong(timeStamp);
        buffer.writeInt(type.ordinal());
        buffer.writeUUID(UUID.fromString(uuid));
    }

    @Override
    public void handle(NetworkEvent.Context context)
    {
        if(type == ReloadType.START)
            return;
        ServerPlayer player = context.getSender();
        if (player == null)
            return;
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof IOgnaWeapon weapon)
        {
            if(weapon.isStackMatching(mainHand, uuid))
            {
                if (weapon.getWeaponCap(mainHand).canReload(mainHand,player))
                {
                    weapon.onServerReload(mainHand,player);
                }
                else OgnmChannel.DEFAULT.sendTo(new SyncReloadStatePacket(ReloadState.RELOAD_IGNORED),player);
                return;
            }
        }
        OgnmChannel.DEFAULT.sendTo(new SyncReloadStatePacket(ReloadState.WEAPON_MISMATCH),player);
    }
}
