package com.azane.ogna.genable.item.weapon;

import com.azane.cjsop.annotation.JsonClassTypeBinder;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.genable.item.base.IGenItem;
import com.azane.ogna.genable.item.base.IPolyItemDataBase;
import com.azane.ogna.registry.ItemRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@JsonClassTypeBinder(fullName = "staff", simpleName = "staff", namespace = OriginiumArts.MOD_ID)
public class StaffDataBase extends DefaultWeaponDataBase implements IStaffDataBase
{
    @Override
    public void registerDataBase()
    {
        Item item = ItemRegistry.OGNA_STAFF.get();
        if(item instanceof IPolyItemDataBase<?> polyItem)
        {
            polyItem.castToType(IStaffDataBase.class).registerDataBase(this);
        }
    }

    @Override
    public ItemStack buildItemStack(int count)
    {
        Item item = ItemRegistry.OGNA_STAFF.get();
        if(item instanceof IGenItem genItem)
        {
            return genItem.templateBuildItemStack(buildTag(),1);
        }
        DebugLogger.error("The item %s is not an instance of IGenItem, cannot build item stack.".formatted(item.getDescriptionId()));
        return null;
    }
}
