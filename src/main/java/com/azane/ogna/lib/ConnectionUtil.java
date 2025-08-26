package com.azane.ogna.lib;

import net.minecraft.server.level.ServerPlayer;

/**
 * @author azaneNH37 (2025/8/26)
 */
public final class ConnectionUtil
{
    public static boolean isLocalConnection(ServerPlayer serverPlayer)
    {
        return serverPlayer.connection.connection.isMemoryConnection();
    }
}
