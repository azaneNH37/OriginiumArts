package com.azane.ogna.network;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.network.to_client.SyncGlobalDatapackPacket;
import com.azane.ogna.network.to_server.InputAttackPacket;
import com.azane.ogna.network.to_server.InputReloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.simple.SimpleChannel;

public class OgnmChannel extends BasePacketHandler
{
    public static final OgnmChannel DEFAULT = new OgnmChannel();

    private OgnmChannel(){}

    private final SimpleChannel CHANNEL = createChannel(RlHelper.build(OriginiumArts.MOD_ID,"main"),"1.0");

    public SimpleChannel getChannel()
    {
        return this.CHANNEL;
    }

    public void initialize()
    {
        registerServerToClient(SyncGlobalDatapackPacket.class,SyncGlobalDatapackPacket::new);

        registerClientToServer(InputAttackPacket.class,InputAttackPacket::new);
        registerClientToServer(InputReloadPacket.class,InputReloadPacket::new);
    }
}
