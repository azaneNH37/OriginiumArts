package com.azane.ogna.client.gui.hud;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.item.weapon.IOgnaWeapon;
import com.azane.ogna.lib.RlHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;

import java.util.Collections;

public class SkillIconHud extends OgnaHud
{
    private static final ResourceLocation BACKGROUND = RlHelper.build(OriginiumArts.MOD_ID,"textures/gui/skill/back.png");
    private static final int ICON_SIZE = 24;
    private static final int ANIMATION_DURATION = 10; // ticks

    private ItemStack lastMainHandItem = ItemStack.EMPTY;
    private ItemStack currentSkillItem = ItemStack.EMPTY;
    private ItemStack previousSkillItem = ItemStack.EMPTY;
    private int animationTicks = 0;
    private boolean isAnimating = false;

    public SkillIconHud() {
        super(new Vec2(0.75f, 0.85f), new Vec2(ICON_SIZE, ICON_SIZE), WindowHud.SIZE,Collections.emptyList());
    }

    @Override
    public void actuallyRender(GuiGraphics graphics, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        ItemStack mainHandItem = mc.player.getMainHandItem();

        updateItems(mainHandItem);

        if (!currentSkillItem.isEmpty()) {
            renderSkillIcon(graphics, partialTicks);
        }
    }

    private void updateItems(ItemStack mainHandItem) {
        if (!ItemStack.matches(lastMainHandItem, mainHandItem)) {
            ItemStack newSkillItem = isSpecialItem(mainHandItem) ? mainHandItem : ItemStack.EMPTY;

            if (!ItemStack.matches(currentSkillItem, newSkillItem)) {
                if (!currentSkillItem.isEmpty() && !newSkillItem.isEmpty()) {
                    // 新旧特定物品切换
                    previousSkillItem = currentSkillItem;
                    currentSkillItem = newSkillItem;
                    isAnimating = true;
                    animationTicks = 0;
                } else {
                    currentSkillItem = newSkillItem;
                    previousSkillItem = ItemStack.EMPTY;
                    isAnimating = false;
                }
            }
            lastMainHandItem = mainHandItem.copy();
        }

        if (isAnimating) {
            animationTicks++;
            if (animationTicks >= ANIMATION_DURATION) {
                isAnimating = false;
                previousSkillItem = ItemStack.EMPTY;
            }
        }
    }

    private void renderSkillIcon(GuiGraphics graphics, float partialTicks) {
        // 渲染背景
        blitTextureSimple(graphics, BACKGROUND, ICON_SIZE, ICON_SIZE);

        if (isAnimating) {
            float progress = (animationTicks + partialTicks) / ANIMATION_DURATION;

            // 旧图标淡出
            graphics.pose().pushPose();
            graphics.setColor(1f, 1f, 1f, 1f - progress);
            renderItemIcon(graphics, previousSkillItem, 0);
            graphics.pose().popPose();

            // 新图标淡入 + 缩放
            graphics.pose().pushPose();
            float scale = Mth.lerp(progress, 0.5f, 1f);
            float alpha = progress;
            graphics.pose().translate(ICON_SIZE / 2f, ICON_SIZE / 2f, 0);
            graphics.pose().scale(scale, scale, 1f);
            graphics.pose().translate(-ICON_SIZE / 2f, -ICON_SIZE / 2f, 0);
            graphics.setColor(1f, 1f, 1f, alpha);
            renderItemIcon(graphics, currentSkillItem, 0);
            graphics.pose().popPose();
        } else {
            renderItemIcon(graphics, currentSkillItem, 0);
        }

        graphics.setColor(1f, 1f, 1f, 1f);
    }

    private void renderItemIcon(GuiGraphics graphics, ItemStack item, int yOffset) {
        if (!item.isEmpty()) {
            graphics.renderItem(item, 4, 4 + yOffset);
        }
    }

    private boolean isSpecialItem(ItemStack item) {
        // 判断是否为特定物品的逻辑
        return !item.isEmpty() && IOgnaWeapon.isWeapon(item);
    }
}