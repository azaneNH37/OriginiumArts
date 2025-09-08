package com.azane.ogna.mixin;

import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author azaneNH37 (2025/9/8)
 */
@Mixin(value = ServerGamePacketListenerImpl.class)
public abstract class SetCreativeModeSlotMixin
{
    @Shadow
    public ServerPlayer player;

    private static final ConcurrentMap<String, CompoundTag> CACHE = new ConcurrentHashMap<>();

    @Redirect(
        method = "handleSetCreativeModeSlot",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/protocol/game/ServerboundSetCreativeModeSlotPacket;getItem()Lnet/minecraft/world/item/ItemStack;"
        )
    )
    private ItemStack onHandleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket instance)
    {
        ognmarts$deduplicateInventoryStackUUID();
        if(IOgnaWeapon.isWeapon(instance.getItem()))
        {
            ItemStack cStack = instance.getItem();
            IOgnaWeapon weapon = (IOgnaWeapon)cStack.getItem();
            String uuid = weapon.getStackUUID(cStack);
            if(CACHE.containsKey(uuid))
            {
                DebugLogger.warn("Detect Existing Weapon matching UUID, save the ForgeCap on Server side: {}", uuid);
                return ItemStack.of(CACHE.get(uuid).copy());
            }
        }
        return instance.getItem();
    }

    @Inject(
        method = "handleSetCreativeModeSlot",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/Slot;setByPlayer(Lnet/minecraft/world/item/ItemStack;)V",
            shift = At.Shift.AFTER
        ))
    private void afterHandleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket pPacket, CallbackInfo ci)
    {
        ognmarts$deduplicateInventoryStackUUID();
    }

    @Unique
    private void ognmarts$deduplicateInventoryStackUUID()
    {
        //此处刷新所有武器的UUID，防止来自创造物品栏拿取武器的重复UUID
        Set<String> existedUUID = new HashSet<>();
        player.getInventory().items.forEach(
            sStack->{
                if(IOgnaWeapon.isWeapon(sStack))
                {
                    IOgnaWeapon w = (IOgnaWeapon)sStack.getItem();
                    String uuid = w.getOrCreateStackUUID(sStack);
                    if(existedUUID.contains(uuid))
                    {
                        String uuid1 = w.regainStackUUID(sStack);
                        DebugLogger.warn("Detect Duplicated Weapon UUID in Inventory, Regain a new one: {} -> {}", uuid, uuid1);
                        uuid = uuid1;
                    }
                    existedUUID.add(uuid);
                    CACHE.put(uuid,sStack.serializeNBT());
                }
            }
        );
        DebugLogger.warn("Weapon Cache Refreshed with inventory from Player {}, size: {}", player.getName().getString(), CACHE.size());
    }
}
