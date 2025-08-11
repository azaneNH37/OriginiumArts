package com.azane.ogna.item;

import com.azane.ogna.lib.ColorHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnergyUnit extends Item
{
    public final int energyValue;

    public EnergyUnit(int energyValue)
    {
        this(new Properties().stacksTo(64).rarity(Rarity.RARE),energyValue);
    }

    public EnergyUnit(Properties pProperties,int energyValue)
    {
        super(pProperties);
        this.energyValue = energyValue;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced)
    {
        pTooltipComponents.add(Component.translatable(getDescriptionId(pStack)+".desp").withStyle(ChatFormatting.DARK_GRAY,ChatFormatting.ITALIC));
    }
}
