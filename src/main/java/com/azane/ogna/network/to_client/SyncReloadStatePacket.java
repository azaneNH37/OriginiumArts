package com.azane.ogna.network.to_client;

import com.azane.ogna.client.gameplay.ReloadState;
import com.azane.ogna.client.gui.hud.OgnaHuds;
import com.azane.ogna.network.IOgnmPacket;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/**
 * @author azaneNH37 (2025-08-10)
 */
@Getter
public class SyncReloadStatePacket implements IOgnmPacket
{
    private final ReloadState reloadState;

    public SyncReloadStatePacket(ReloadState reloadState)
    {
        this.reloadState = reloadState;
    }

    public SyncReloadStatePacket(FriendlyByteBuf buffer)
    {
        this.reloadState = buffer.readEnum(ReloadState.class);
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeEnum(reloadState);
    }

    @Override
    public void handle(NetworkEvent.Context context)
    {
        OgnaHuds.RELOAD_STATUS_HUD.refreshReloadState(reloadState,2500);
    }
}
