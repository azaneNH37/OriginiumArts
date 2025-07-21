package com.azane.ogna.network.to_client;

import com.azane.ogna.network.IOgnmPacket;
import com.azane.ogna.util.OgnaFxHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

@Getter
@AllArgsConstructor
public class FxBlockEffectTriggerPacket implements IOgnmPacket
{
    private final ResourceLocation fx;
    private final BlockPos blockPos;
    private final boolean forceDead;

    public FxBlockEffectTriggerPacket(FriendlyByteBuf buf)
    {
        fx = buf.readResourceLocation();
        blockPos = buf.readBlockPos();
        forceDead = buf.readBoolean();
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(fx);
        buffer.writeBlockPos(blockPos);
        buffer.writeBoolean(forceDead);
    }

    @Override
    public void handle(NetworkEvent.Context context)
    {
        OgnaFxHelper.clientTriggerBlockEffectFx(this);
    }
}
