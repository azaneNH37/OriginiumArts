package com.azane.ogna.network.to_client;

import com.azane.ogna.network.IOgnmPacket;
import com.azane.ogna.util.OgnaFxHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

@Getter
@AllArgsConstructor
public class FxEntityEffectTriggerPacket implements IOgnmPacket
{
    private final ResourceLocation fx;
    private final int entityID;
    private final boolean forceDead;

    public FxEntityEffectTriggerPacket(FriendlyByteBuf buf)
    {
        fx = buf.readResourceLocation();
        entityID = buf.readInt();
        forceDead = buf.readBoolean();
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(fx);
        buffer.writeInt(entityID);
        buffer.writeBoolean(forceDead);
    }

    @Override
    public void handle(NetworkEvent.Context context)
    {
        OgnaFxHelper.clientTriggerEntityFx(this);
    }
}
