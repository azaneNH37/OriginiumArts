package com.azane.ogna.client.gui.hud;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.capability.skill.ISkillCap;
import com.azane.ogna.capability.weapon.IOgnaWeaponCap;
import com.azane.ogna.genable.item.skill.ISkill;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.registry.ModAttribute;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;

import java.util.Collections;

public class SkillHud extends OgnaHud
{
    private static final ResourceLocation BACK = RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/skill/back.png");
    private static final ResourceLocation ACTIVE = RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/skill/active.png");
    private static final ResourceLocation CANUSE = RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/skill/canuse.png");
    private static final ResourceLocation CHARGE = RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/skill/charge.png");
    private static final ResourceLocation EMPTY = RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/skill/empty.png");
    private static final ResourceLocation LABEL_CHARGE = RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/skill/label_charge.png");
    private static final ResourceLocation LABEL_READY = RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/skill/label_ready.png");

    private static final int ICON_SIZE = 128;

    public SkillHud() {
        super(new Vec2(0.745f, 0.94f), new Vec2(ICON_SIZE, ICON_SIZE), WindowHud.SIZE,Collections.emptyList());
    }

    @Override
    public void actuallyRender(GuiGraphics graphics, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        ItemStack mainHandItem = mc.player.getMainHandItem();

        if(mainHandItem.isEmpty() || !IOgnaWeapon.isWeapon(mainHandItem))
            return;

        //TODO:你封装了个什么（
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        graphics.pose().pushPose();
        graphics.pose().scale(0.55f,0.55f,1f);

        blitTextureSimple(graphics, BACK, ICON_SIZE, ICON_SIZE);

        IOgnaWeaponCap weapon = ((IOgnaWeapon) mainHandItem.getItem()).getWeaponCap(mainHandItem);
        ISkillCap skillCap = weapon.getSkillCap();
        ISkill skill = skillCap.getSkill();

        if(skill == null)
        {
            blitTextureSimple(graphics, EMPTY, ICON_SIZE, ICON_SIZE);
            return;
        }

        ResourceLocation icon = skill.getDisplayContext().getIcon();
        //icon = RlHelper.build(icon.getNamespace(), "textures/item/skill/%s.png".formatted(icon.getPath()));
        graphics.pose().pushPose();
        graphics.pose().translate(4f,4f,0f);
        graphics.pose().scale(120f/128f, 120f/128f, 1f);
        blitTextureSimple(graphics, icon, ICON_SIZE, ICON_SIZE);
        graphics.pose().popPose();

        boolean isActive = skillCap.isActive();
        if(!isActive)
        {
            double maxSP = weapon.submitAttrVal(ModAttribute.SKILL_SP.get(), mc.player, mainHandItem,skill.getSkillData().getSP());
            double chargeProgress = (skillCap.getSP() % maxSP) / maxSP;
            int chargeTicks = (int) (chargeProgress * ICON_SIZE);
            graphics.blit(CHARGE, 0, ICON_SIZE-chargeTicks,0,ICON_SIZE - chargeTicks, ICON_SIZE, chargeTicks,ICON_SIZE,ICON_SIZE);
            if(skillCap.getSP() >= maxSP)
                blitTextureSimple(graphics,CANUSE,ICON_SIZE,ICON_SIZE);
            graphics.pose().pushPose();
            graphics.pose().translate(0f,ICON_SIZE+10f,0f);
            if( skillCap.getSP() >= maxSP )
                blitTextureSimple(graphics, LABEL_READY, ICON_SIZE, 32);
            else
            {
                blitTextureSimple(graphics, LABEL_CHARGE, ICON_SIZE, 32);
                graphics.pose().translate(40f,5f,0f);
                graphics.pose().scale(2.5f,2.5f,1f);
                graphics.drawString(font,"%d/%d".formatted((int)skillCap.getSP()/10,(int)maxSP/10), 0, 0, 0xFFFFFF);
            }
            graphics.pose().popPose();
        }
        else
        {
            double maxDuration = weapon.submitAttrVal(ModAttribute.SKILL_DURATION.get(), mc.player, mainHandItem, skill.getSkillData().getDuration());
            double durationProgress = (skillCap.getRD() % maxDuration) / maxDuration;
            int durationTicks = (int) (durationProgress * ICON_SIZE);
            graphics.setColor(1f,1f,1f,0.45f);
            graphics.blit(ACTIVE, 0, ICON_SIZE - durationTicks,0,ICON_SIZE - durationTicks, ICON_SIZE, durationTicks,ICON_SIZE,ICON_SIZE);
            graphics.setColor(1f,1f,1f,1f);
        }

        graphics.pose().popPose();
    }
}