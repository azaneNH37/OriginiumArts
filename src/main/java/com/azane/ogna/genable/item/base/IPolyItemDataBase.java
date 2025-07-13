package com.azane.ogna.genable.item.base;

import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.debug.log.LogLv;
import com.azane.ogna.lib.RlHelper;
import com.azane.ogna.resource.helper.IresourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Map;
import java.util.Objects;

public interface IPolyItemDataBase<T extends IresourceLocation>
{
    Marker POLY_ITEM_DATABASE = MarkerManager.getMarker("PolyItemDatabase");

    boolean isDataBaseForStack(ItemStack itemStack);

    Map<ResourceLocation, T> getDatabaseCache();

    Class<T> getDataBaseType();

    @SuppressWarnings("unchecked")
    default <U extends IresourceLocation> IPolyItemDataBase<U> castToType(Class<U> type) {
        if(getDataBaseType().equals(type)) {
            return (IPolyItemDataBase<U>) this;
        }
        throw new ClassCastException("Cannot cast to " + type.getName());
    }

    default T getDataBaseForStack(ItemStack itemStack)
    {
        try {
            if(!isDataBaseForStack(itemStack))
            {
                DebugLogger.log(LogLv.ERROR, POLY_ITEM_DATABASE, "ItemStack is not a valid GenItem: {}",itemStack.toString());
                throw new IllegalArgumentException("ItemStack is not a valid GenItem: " + itemStack);
            }
            // 从NBT获取Database ID
            ResourceLocation dbId = RlHelper.parse(Objects.requireNonNull(itemStack.getTagElement(IGenItem.GEN_TAG)).getString(IresourceLocation.TAG_RL));
            T database = getDatabaseCache().get(dbId);

            if (database != null)
                return database;

            DebugLogger.log(LogLv.WARN, POLY_ITEM_DATABASE, "Database not found for ID: " + dbId);
        } catch (Exception e) {
            DebugLogger.log(LogLv.ERROR, POLY_ITEM_DATABASE, "Error getting database for stack: " + e.getMessage());
        }
        throw new IllegalArgumentException("ItemStack is not a valid GenItem: " + itemStack);
    }

    default void registerDataBase(T dataBase)
    {
        if(dataBase == null)
        {
            DebugLogger.log(LogLv.ERROR, POLY_ITEM_DATABASE, "Attempted to register a null database.");
            return;
        }
        ResourceLocation id = dataBase.getId();
        if(getDatabaseCache().containsKey(id))
        {
            DebugLogger.log(LogLv.WARN, POLY_ITEM_DATABASE, "Database with ID {} already exists. Overwriting.", id);
        }
        getDatabaseCache().put(id, dataBase);
        DebugLogger.log(LogLv.INFO, POLY_ITEM_DATABASE, "Registered database with ID: {}", id);
    }
}