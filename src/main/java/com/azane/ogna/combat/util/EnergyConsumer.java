package com.azane.ogna.combat.util;

import com.azane.ogna.item.EnergyUnit;
import com.azane.ogna.registry.ModItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class EnergyConsumer
{
    public static int convertItems(Player player, int targetValue) {
        if (targetValue <= 0) {
            return 0;
        }

        Inventory inventory = player.getInventory();
        int currentSum = 0;
        int itemsToGive = 0; // 需要给予多少个新物品

        // 遍历背包，包括主背包和快捷栏
        // 这里简化为遍历 0-35 的槽位
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);

            // 检查物品是否在我们的转换列表中
            if (!stack.isEmpty() && (stack.getItem() instanceof EnergyUnit energyUnit)) {
                int itemValue = energyUnit.energyValue;
                if(itemValue == 0)
                    continue;
                int stackCount = stack.getCount();

                int remainingValue = targetValue - currentSum;

                // 转换当前物品堆栈中的物品
                if (remainingValue > 0) {
                    // 计算需要转换多少个该物品
                    int itemsToConvert = Math.min(stackCount, (int) Math.ceil((double) remainingValue / itemValue));

                    // 实际转换的物品数
                    if (currentSum + itemsToConvert * itemValue <= targetValue) {
                        currentSum += itemsToConvert * itemValue;
                        itemsToGive += itemsToConvert;
                        // 从背包中扣除物品
                        inventory.removeItem(i, itemsToConvert);
                    } else {
                        // 找到恰好能达到目标值的数量
                        int exactItemsToConvert = (remainingValue + itemValue - 1) / itemValue;
                        if (exactItemsToConvert > 0) {
                            currentSum += exactItemsToConvert * itemValue;
                            itemsToGive += exactItemsToConvert;
                            inventory.removeItem(i,exactItemsToConvert);
                        }
                    }
                }
            }

            // 如果已经达到或超过目标值，则停止遍历
            if (currentSum >= targetValue) {
                break;
            }
        }

        // 给予玩家新物品
        if (itemsToGive > 0) {
            while(itemsToGive > 0)
            {
                ItemStack emptyEU = new ItemStack(ModItem.ENERGY_UNIT_SIMPLE_EMPTY.get(),Math.min(64,itemsToGive));
                if(!inventory.add(emptyEU))
                    player.drop(emptyEU, false);
                itemsToGive -= Math.min(64,itemsToGive);
            }
        }

        return currentSum;
    }
}
