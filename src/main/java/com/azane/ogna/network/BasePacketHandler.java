package com.azane.ogna.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.Function;

public abstract class BasePacketHandler
{

    protected static SimpleChannel createChannel(ResourceLocation name, String version) {
        return NetworkRegistry.ChannelBuilder.named(name)
              .clientAcceptedVersions(version::equals)
              .serverAcceptedVersions(version::equals)
              .networkProtocolVersion(() -> version)
              .simpleChannel();
    }

    private int index = 0;

    protected abstract SimpleChannel getChannel();

    public abstract void initialize();

    protected <MSG extends IOgnmPacket> void registerClientToServer(Class<MSG> type, Function<FriendlyByteBuf, MSG> decoder) {
        registerMessage(type, decoder, NetworkDirection.PLAY_TO_SERVER);
    }

    protected <MSG extends IOgnmPacket> void registerServerToClient(Class<MSG> type, Function<FriendlyByteBuf, MSG> decoder) {
        registerMessage(type, decoder, NetworkDirection.PLAY_TO_CLIENT);
    }

    private <MSG extends IOgnmPacket> void registerMessage(Class<MSG> type, Function<FriendlyByteBuf, MSG> decoder, NetworkDirection networkDirection) {
        getChannel().registerMessage(index++, type, IOgnmPacket::encode, decoder, IOgnmPacket::handle, Optional.of(networkDirection));
    }

    public <MSG> void sendToAll(MSG message) {
        getChannel().send(PacketDistributor.ALL.noArg(), message);
    }

    public <MSG> void sendTo(MSG message, ServerPlayer player) {
        //Validate it is not a fake player, even though none of our code should call this with a fake player
        if (!(player instanceof FakePlayer)) {
            getChannel().send(PacketDistributor.PLAYER.with(() -> player), message);
        }
    }
    public <MSG> void sendListTo(MSG message, Iterable<ServerPlayer> players) {
        players.forEach((player) -> {
            sendTo(message,player);
        });
    }

    public <MSG> void sendToServer(MSG message) {
        getChannel().sendToServer(message);
    }

}