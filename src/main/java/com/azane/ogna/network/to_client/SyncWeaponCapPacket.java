package com.azane.ogna.network.to_client;

import com.azane.ogna.capability.skill.ISkillCap;
import com.azane.ogna.capability.weapon.IOgnaWeaponCap;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.azane.ogna.network.IOgnmPacket;
import com.azane.ogna.network.OgnmChannel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class SyncWeaponCapPacket implements IOgnmPacket
{
    public enum CapData
    {
        CURRENT_ENERGY,
        SKILL_SP,
        SKILL_RD
    }

    private final UUID weaponID;
    private final CapData syncData;
    private final double val;

    public SyncWeaponCapPacket(FriendlyByteBuf buf)
    {
        this.weaponID = buf.readUUID();
        this.syncData = buf.readEnum(CapData.class);
        this.val = buf.readDouble();
    }


    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeUUID(weaponID);
        buffer.writeEnum(syncData);
        buffer.writeDouble(val);
    }

    @Override
    public void handle(NetworkEvent.Context context)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player != null)
        {
            ItemStack stack = player.getMainHandItem();
            if(!IOgnaWeapon.isWeapon(stack))
                return;
            IOgnaWeapon weapon = (IOgnaWeapon) stack.getItem();
            if(!weapon.isStackMatching(stack,weaponID.toString()))
                return;
            IOgnaWeaponCap cap = weapon.getWeaponCap(stack);
            ISkillCap skillCap = cap.getSkillCap();
            switch (syncData)
            {
                case CURRENT_ENERGY -> cap.modifyCurrentEnergy(val - cap.getCurrentEnergy(), false, player, stack);
                case SKILL_SP -> skillCap.modifySP(val - skillCap.getSP(),false, player, stack);
                case SKILL_RD -> skillCap.modifyRD( val - skillCap.getRD(),false, player,stack );
            }
        }
    }

    public static void trySend(ServerPlayer player,ItemStack stack,CapData data,double val)
    {
        if(!IOgnaWeapon.isWeapon(stack))
            return;
        IOgnaWeapon weapon = (IOgnaWeapon) stack.getItem();
        OgnmChannel.DEFAULT.sendTo(new SyncWeaponCapPacket(UUID.fromString(weapon.getOrCreateStackUUID(stack)), data,val),player);
    }
}
