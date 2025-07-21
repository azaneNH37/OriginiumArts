package com.azane.ogna.network.to_server;

import com.azane.ogna.item.weapon.AttackType;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.azane.ogna.network.IOgnmPacket;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

@Getter
public class InputAttackPacket implements IOgnmPacket
{
    private long timeStamp;

    private AttackType attackType;
    private long chargeTime;
    private String uuid;

    public InputAttackPacket(AttackType attackType, long chargeTime,String uuid)
    {
        this.timeStamp = System.currentTimeMillis();
        this.attackType = attackType;
        this.chargeTime = chargeTime;
        this.uuid = uuid;
    }

    public InputAttackPacket(FriendlyByteBuf buffer)
    {
        this.timeStamp = buffer.readLong();
        this.attackType = AttackType.values()[buffer.readInt()];
        this.chargeTime = buffer.readLong();
        this.uuid = buffer.readUUID().toString();
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeLong(timeStamp);
        buffer.writeInt(attackType.ordinal());
        buffer.writeLong(chargeTime);
        buffer.writeUUID(UUID.fromString(uuid));
    }

    @Override
    public void handle(NetworkEvent.Context context)
    {
        ServerPlayer player = context.getSender();
        if (player == null)
            return;
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof IOgnaWeapon weapon)
        {
            if(weapon.isStackMatching(mainHand, uuid))
            {
                if (weapon.getWeaponCap(mainHand).canAttack(mainHand, player, attackType))
                {
                    weapon.onServerAttack(mainHand, player, attackType, chargeTime);
                }
            }
        }
    }
}
