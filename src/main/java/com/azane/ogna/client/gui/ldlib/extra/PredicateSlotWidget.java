package com.azane.ogna.client.gui.ldlib.extra;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import lombok.Setter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

@LDLRegister(name = "item_slot_predicate", group = "widget.container")
public class PredicateSlotWidget extends SlotWidget
{
    @Setter
    protected Predicate<ItemStack> putPredicate = (s)->true;
    @Setter
    protected BiPredicate<Player,ItemStack> takePredicate = (p,stack)->true;

    @Override
    public boolean canPutStack(ItemStack stack)
    {
        return super.canPutStack(stack) && putPredicate.test(stack);
    }
    @Override
    public boolean canTakeStack(Player player)
    {
        return super.canTakeStack(player) && takePredicate.test(player, getItem());
    }
}
