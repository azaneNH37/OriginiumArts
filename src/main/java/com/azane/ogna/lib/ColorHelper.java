package com.azane.ogna.lib;

import net.minecraft.ChatFormatting;

public final class ColorHelper
{
    public static ChatFormatting getRarityColor(int rarity)
    {
        return switch (rarity) {
            case 1 -> ChatFormatting.GRAY;
            case 2 -> ChatFormatting.DARK_GREEN;
            case 3 -> ChatFormatting.BLUE;
            case 4 -> ChatFormatting.DARK_PURPLE;
            case 5 -> ChatFormatting.GOLD;
            case 6 -> ChatFormatting.DARK_RED;
            default -> ChatFormatting.BLUE;
        };
    }

    public static int getGradientColor(double value) {
        if (value < 0) {
            value = 0;
        }

        int r, g, b;

        if (value <= 4) {
            // 白 (255,255,255) -> 绿 (0,255,0)
            double ratio = value / 4.0;
            r = (int) (255 * (1 - ratio));
            g = 255;
            b = (int) (255 * (1 - ratio));
        } else if (value <= 8) {
            // 绿 (0,255,0) -> 蓝 (0,0,255)
            double ratio = (value - 4) / 4.0;
            r = 0;
            g = (int) (255 * (1 - ratio));
            b = (int) (255 * ratio);
        } else if (value <= 12) {
            // 蓝 (0,0,255) -> 紫 (128,0,128)
            double ratio = (value - 8) / 4.0;
            r = (int) (128 * ratio);
            g = 0;
            b = (int) (255 * (1 - ratio) + 128 * ratio);
        } else if (value <= 16) {
            // 紫 (128,0,128) -> 黄 (255,255,0)
            double ratio = (value - 12) / 4.0;
            r = (int) (128 * (1 - ratio) + 255 * ratio);
            g = (int) (255 * ratio);
            b = (int) (128 * (1 - ratio));
        } else if (value <= 20) {
            // 黄 (255,255,0) -> 红 (255,0,0)
            double ratio = (value - 16) / 4.0;
            r = 255;
            g = (int) (255 * (1 - ratio));
            b = 0;
        } else {
            // > 20: 周期变化，红 (255,0,0) -> 品红 (255,0,255)
            double period = 5.0;
            double phase = (value - 20) % period;
            double ratio = phase / period;
            r = 255;
            g = 0;
            b = (int) (255 * ratio);
        }

        // 确保RGB值在0-255范围内
        r = Math.min(255, Math.max(0, r));
        g = Math.min(255, Math.max(0, g));
        b = Math.min(255, Math.max(0, b));

        // 将RGB值组合成十进制整数
        return (255 << 24) | (r << 16) | (g << 8) | b;
    }
}
