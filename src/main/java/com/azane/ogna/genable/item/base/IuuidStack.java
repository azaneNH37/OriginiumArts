package com.azane.ogna.genable.item.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

/**
 * Interface for items that can have a UUID stack.
 * This is used to identify stacks uniquely, especially in multiplayer scenarios.
 * <br><b>Notice that the item should stack to 1, but I have no ideas how to check it explicitly</b>
 */
public interface IuuidStack
{
    String STACK_UUID_KEY = "stack_uuid";

    /**
     * 为武器生成或获取UUID
     */
    default String getOrCreateStackUUID(ItemStack stack) {
        if (stack.isEmpty())
            return null;

        CompoundTag nbt = stack.getOrCreateTag();
        if (!nbt.contains(STACK_UUID_KEY)) {
            String uuid = UUID.randomUUID().toString();
            nbt.putString(STACK_UUID_KEY, uuid);
            return uuid;
        }

        return nbt.getString(STACK_UUID_KEY);
    }

    /**
     * 获取武器UUID（不创建新的）
     */
    default String getStackUUID(ItemStack stack) {
        if (stack.isEmpty())
            return null;

        CompoundTag nbt = stack.getTag();
        if (nbt == null || !nbt.contains(STACK_UUID_KEY)) {
            return null;
        }

        return nbt.getString(STACK_UUID_KEY);
    }

    /**
     * 验证武器是否匹配指定UUID
     */
    default boolean isStackMatching(ItemStack stack, String expectedUUID) {
        if (expectedUUID == null) return false;
        String actualUUID = getStackUUID(stack);
        return expectedUUID.equals(actualUUID);
    }
}