package com.azane.ogna.client.gui.hud;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.item.genable.IOgnaWeapon;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.registry.AttributeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;

import java.util.List;

public class WeaponHud extends OgnaHud
{
    private static final ResourceLocation BACKGROUND = RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/weapon.png");

    public WeaponHud()
    {
        super(new Vec2(0.85f,0.9f),new Vec2(216f,72f), WindowHud.SIZE,List.of());
    }

    @Override
    public void actuallyRender(GuiGraphics graphics, float partialTicks)
    {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        ItemStack mainHandItem = mc.player.getMainHandItem();

        if(mainHandItem.isEmpty() || !IOgnaWeapon.isWeapon(mainHandItem))
            return;

        IOgnaWeapon weapon = (IOgnaWeapon) mainHandItem.getItem();

        graphics.pose().pushPose();
        blitTextureSimple(graphics,BACKGROUND,216,72);

        graphics.pose().pushPose();
        graphics.pose().translate(70f,5f,0f);
        graphics.pose().scale(3f,3f,1f);
        graphics.drawString(font,"%d".formatted((int)weapon.getWeaponCap(mainHandItem).getCurrentEnergy(mainHandItem)),0,0,0xFFFFFF);
        graphics.pose().popPose();

        graphics.pose().pushPose();
        graphics.pose().translate(85f,37f,0f);
        graphics.pose().scale(2f,2f,1f);
        graphics.drawString(font,"%d".formatted((int)weapon.getWeaponCap(mainHandItem).submitBaseAttrVal(AttributeRegistry.WEAPON_ENERGY_CONSUME.get(),Minecraft.getInstance().player, mainHandItem)),0,0,0x00FFFF);
        graphics.pose().popPose();

        graphics.pose().pushPose();
        graphics.pose().translate(155f,37f,0f);
        graphics.pose().scale(2f,2f,1f);
        graphics.drawString(font,"%d".formatted((int)weapon.getWeaponCap(mainHandItem).submitBaseAttrVal(AttributeRegistry.WEAPON_ENERGY_STORE.get(),Minecraft.getInstance().player, mainHandItem)),0,0,0xFFFFFF);
        graphics.pose().popPose();

        graphics.pose().pushPose();
        graphics.pose().translate(10f,60f,0f);
        graphics.pose().scale(2f,2f,1f);
        graphics.drawString(font,"WEAPON",0,0,0xFFFFFF);
        graphics.pose().popPose();


        graphics.pose().popPose();
    }
}
