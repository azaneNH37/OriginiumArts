package com.azane.ogna.item;

import com.azane.ogna.lib.ColorHelper;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArkMaterial extends Item
{
    @Getter
    private final int rarity;

    public ArkMaterial(int rarity)
    {
        this(new Properties().stacksTo(64),rarity);
    }

    public ArkMaterial(Properties pProperties,int rarity)
    {
        super(pProperties);
        this.rarity = rarity;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced)
    {
        pTooltipComponents.clear();
        pTooltipComponents.add(Component.translatable(getDescriptionId(pStack)).withStyle(ColorHelper.getRarityColor(rarity)));
    }
}
