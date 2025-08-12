package com.azane.ogna.genable.item.base;

import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.debug.log.LogLv;
import com.azane.ogna.resource.helper.IresourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

/**
 * @author azaneNH37 (2025-08-11)
 */
public interface IGenItem
{
    Marker GEN_ITEM_MARKER = MarkerManager.getMarker(ModGenIdentifier.getId()+".GenItemTemplate");
    String GEN_TAG = ModGenIdentifier.getId()+".database";
    String IDENTIFIER_TAG = ModGenIdentifier.getId()+".template_identifier";

    <T extends Item> T getItem();

    default String getTagIdentifier()
    {
        return getItem().getDescriptionId();
    }

    @Nullable
    default String getDatabaseId(ItemStack stack)
    {
        if(!isThisGenItem(stack))
            return null;
        return stack.getOrCreateTag().getCompound(GEN_TAG).getString(IresourceLocation.TAG_RL);
    }

    default boolean isThisGenItem(ItemStack stack)
    {
        if(stack.hasTag())
        {
            CompoundTag tag = stack.getOrCreateTag();
            if(tag.contains(IDENTIFIER_TAG) && tag.getString(IDENTIFIER_TAG).equals(getTagIdentifier()))
            {
                CompoundTag tag1 = stack.getTagElement(GEN_TAG);
                return tag1 != null && tag1.contains(IresourceLocation.TAG_RL);
            }
        }
        return false;
    }

    /**
     * 应该由对应的database侧的类调用
     */
    @Nullable
    default ItemStack templateBuildItemStack(CompoundTag tag,int count)
    {
        if(tag == null || !tag.contains(IresourceLocation.TAG_RL))
        {
            DebugLogger.log(LogLv.ERROR, GEN_ITEM_MARKER, "Item {} templateBuildItemStack called with missing weapon item resource location tag. Ignored. ",getTagIdentifier());
            return null;
        }
        ItemStack stack = new ItemStack(getItem().asItem(), count);
        stack.getOrCreateTag().putString(IDENTIFIER_TAG,getTagIdentifier());
        stack.addTagElement(GEN_TAG, tag);
        return stack;
    }
}