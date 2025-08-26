package com.azane.ogna.event;

import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.azane.ogna.network.OgnmChannel;
import com.azane.ogna.network.to_client.SyncMenuSlotItemStackPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * @author azaneNH37 (2025/8/26)
 */
@Mod.EventBusSubscriber
public class ItemStackListener
{
    private static final Set<AbstractContainerMenu> CACHED = new HashSet<>();

    public static final Function<ServerPlayer, ContainerListener> LISTENER_FACTORY = (serverPlayer) -> new ContainerListener()
    {
        @Override
        public void slotChanged(AbstractContainerMenu pContainerToSend, int pDataSlotIndex, ItemStack pStack)
        {
            if(IOgnaWeapon.isWeapon(pStack))
            {
                OgnmChannel.DEFAULT.sendTo(new SyncMenuSlotItemStackPacket(pContainerToSend.containerId,
                    pDataSlotIndex,IOgnaWeapon.getSyncNbt(pStack)),serverPlayer);
            }
        }
        @Override
        public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {}
    };

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        if(event.getEntity() instanceof ServerPlayer serverPlayer)
        {
            if(!CACHED.contains(serverPlayer.inventoryMenu))
            {
                DebugLogger.log("Listening to inventoryMenu of Player {} with its id {}",serverPlayer.getName().getString(),serverPlayer.inventoryMenu.containerId);
                CACHED.add(serverPlayer.inventoryMenu);
                serverPlayer.inventoryMenu.addSlotListener(LISTENER_FACTORY.apply(serverPlayer));
            }
        }
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event)
    {
        if(event.getEntity() instanceof ServerPlayer serverPlayer)
        {
            if(!CACHED.contains(event.getContainer()))
            {
                DebugLogger.log("Listening to container {} with its id {}",event.getContainer(),event.getContainer().containerId);
                CACHED.add(event.getContainer());
                event.getContainer().addSlotListener(LISTENER_FACTORY.apply(serverPlayer));
            }
        }
    }
}
