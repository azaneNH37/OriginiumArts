package com.azane.ogna.debug;

import com.azane.ogna.OriginiumArts;
import com.azane.ogna.entity.SlashEntity;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GenSlashItem extends Item
{
    public GenSlashItem()
    {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        RandomSource rand = pLevel.getRandom();
        if(!pLevel.isClientSide())
        {
            //OriginiumArts.LOGGER.warn("Create slash");
            pLevel.addFreshEntity(SlashEntity.createSlash(pLevel,pPlayer,12,
                FastColor.ARGB32.color(255,rand.nextInt(0,255),rand.nextInt(0,255),rand.nextInt(0,255))));
        }
        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }
}
