package com.azane.ogna.genable.item.weapon;

import com.azane.cjsop.annotation.JsonClassTypeBinder;
import com.azane.ogna.OriginiumArts;
import com.azane.ogna.debug.log.DebugLogger;
import com.azane.ogna.genable.item.base.IGenItem;
import com.azane.ogna.genable.item.base.IPolyItemDataBase;
import com.azane.ogna.registry.ModItem;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * @author azaneNH37 (2025-08-04)
 */
@JsonClassTypeBinder(fullName = "sword", simpleName = "sword", namespace = OriginiumArts.MOD_ID)
public class SwordDataBase extends DefaultWeaponDataBase implements ISwordDataBase
{
    @SerializedName("normal_combo")
    @Getter
    protected int normalCombo = 1;
    @SerializedName("skill_combo")
    @Getter
    protected int skillCombo = 1;

    @Override
    public void registerDataBase()
    {
        Item item = ModItem.OGNA_SWORD.get();
        if(item instanceof IPolyItemDataBase<?> polyItem)
        {
            polyItem.castToType(ISwordDataBase.class).registerDataBase(this);
        }
    }

    @Override
    public ItemStack buildItemStack(int count)
    {
        Item item = ModItem.OGNA_SWORD.get();
        if(item instanceof IGenItem genItem)
        {
            return genItem.templateBuildItemStack(buildTag(),1);
        }
        DebugLogger.error("The item %s is not an instance of IGenItem, cannot build item stack.".formatted(item.getDescriptionId()));
        return null;
    }
}
